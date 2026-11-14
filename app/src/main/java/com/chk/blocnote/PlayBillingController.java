package com.chk.blocnote;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.Collections;
import java.util.List;

/**
 * Achat unique non consommable entièrement géré par Google Play Billing.
 *
 * Le client interroge Google Play au démarrage et après chaque achat afin de
 * restaurer la licence sans dépendre d'un serveur CHK. Google Play traite les
 * paiements, les états d'achat et leur acquittement.
 *
 * Limite : la validation côté appareil est moins robuste contre la fraude
 * qu'une validation du purchaseToken par Google Play Developer API sur serveur.
 * Aucun achat PENDING, annulé ou échoué ne donne accès au premium.
 */
public final class PlayBillingController {
    public static final String PRODUCT_ID = "blocnote_premium_lifetime";

    public interface Listener {
        void onBillingStatus(String message, boolean purchaseEnabled, String formattedPrice);
        void onAccessRestored();
    }

    private final Activity activity;
    private final Listener listener;
    private final BillingClient billing;
    private final Handler main = new Handler(Looper.getMainLooper());
    private boolean connected, closed, queryingPurchases;
    private ProductDetails product;
    private String price = "Prix communiqué par Google Play";

    public PlayBillingController(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
        this.billing = BillingClient.newBuilder(activity.getApplicationContext())
            .setListener(this::purchasesUpdated)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts().build())
            .enableAutoServiceReconnection()
            .build();
    }

    private boolean ready() {
        return !closed && connected && billing.isReady() && product != null
            && product.getOneTimePurchaseOfferDetailsList() != null
            && !product.getOneTimePurchaseOfferDetailsList().isEmpty();
    }

    public void connect() {
        if (closed) return;
        report("Connexion au paiement sécurisé Google Play…");
        if (billing.isReady()) {
            connected = true;
            queryOwned(false);
            queryProducts();
            return;
        }
        billing.startConnection(new BillingClientStateListener() {
            @Override public void onBillingSetupFinished(BillingResult result) {
                if (closed) return;
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connected = true;
                    queryOwned(false);
                    queryProducts();
                } else {
                    connected = false;
                    report("Google Play indisponible (" + result.getResponseCode() + ").");
                }
            }

            @Override public void onBillingServiceDisconnected() {
                connected = false;
                report("Connexion Google Play interrompue.");
            }
        });
    }

    private void queryProducts() {
        if (closed || !billing.isReady()) return;
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(Collections.singletonList(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP).build()))
            .build();

        billing.queryProductDetailsAsync(params, (result, details) -> {
            if (closed) return;
            product = null;
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && details.getProductDetailsList() != null) {
                for (ProductDetails item : details.getProductDetailsList()) {
                    if (PRODUCT_ID.equals(item.getProductId())) {
                        product = item;
                        break;
                    }
                }
                if (ready()) {
                    price = product.getOneTimePurchaseOfferDetailsList()
                        .get(0).getFormattedPrice();
                    report("Achat définitif Google Play disponible.");
                    return;
                }
            }
            report("Produit d'achat unique non disponible sur Google Play.");
        });
    }

    public void restorePurchases() {
        if (closed) return;
        if (!billing.isReady()) {
            connect();
            return;
        }
        queryOwned(true);
    }

    private static boolean isValidOwnedPurchase(Purchase purchase) {
        return purchase != null
            && purchase.getProducts().contains(PRODUCT_ID)
            && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
            && purchase.getPurchaseToken() != null
            && !purchase.getPurchaseToken().isEmpty();
    }

    private void queryOwned(boolean manual) {
        if (closed || !billing.isReady() || queryingPurchases) return;
        queryingPurchases = true;
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP).build();
        billing.queryPurchasesAsync(params, (result, purchases) -> {
            queryingPurchases = false;
            if (closed) return;
            if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                // Une erreur réseau ne signifie pas qu'un achat a été remboursé.
                report("Impossible de vérifier les achats avec Google Play. Réessayez.");
                return;
            }
            boolean owned = false;
            boolean pending = false;
            if (purchases != null) {
                for (Purchase purchase : purchases) {
                    if (!purchase.getProducts().contains(PRODUCT_ID)) continue;
                    if (isValidOwnedPurchase(purchase)) {
                        owned = true;
                        acknowledgeIfNecessary(purchase);
                    } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                        pending = true;
                    }
                }
            }
            boolean hadAccess = TrialAccess.purchased(activity);
            // La réponse réussie de Google Play reflète les achats du compte actif.
            TrialAccess.updatePurchase(activity, owned);
            if (owned) {
                report("Achat définitif Google Play reconnu.");
                if (!hadAccess) listener.onAccessRestored();
            } else if (pending) {
                report("Achat Google Play en attente : accès non encore activé.");
            } else {
                report(manual ? "Aucun achat définitif sur ce compte Google Play."
                    : "Aucune licence payante trouvée sur ce compte.");
            }
        });
    }

    private void acknowledgeIfNecessary(Purchase purchase) {
        if (closed || !isValidOwnedPurchase(purchase) || purchase.isAcknowledged()) return;
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.getPurchaseToken()).build();
        billing.acknowledgePurchase(params, result -> {
            if (!closed && result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                // La restauration au prochain démarrage relancera l'acquittement.
                report("Achat reconnu ; confirmation Google Play à réessayer.");
            }
        });
    }

    private void purchasesUpdated(BillingResult result, List<Purchase> purchases) {
        if (closed) return;
        if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            report("Achat annulé : aucun paiement confirmé.");
            return;
        }
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            report("Paiement non confirmé par Google Play.");
            return;
        }
        boolean foundPurchased = false;
        boolean foundPending = false;
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                if (!purchase.getProducts().contains(PRODUCT_ID)) continue;
                if (isValidOwnedPurchase(purchase)) foundPurchased = true;
                else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
                    foundPending = true;
            }
        }
        if (foundPurchased) {
            // Toujours réinterroger le catalogue des achats de Google Play avant
            // d'attribuer le premium ; ne pas se contenter du callback d'achat.
            queryOwned(true);
        } else if (foundPending) {
            report("Paiement en attente : aucun accès définitif pour le moment.");
        } else {
            report("Aucun achat définitif vérifié.");
        }
    }

    public void buy() {
        if (closed) return;
        if (!ready()) {
            report("Paiement indisponible : vérifier Google Play et le produit.");
            if (!connected) connect();
            else queryProducts();
            return;
        }
        if (TrialAccess.purchased(activity)) {
            report("Licence définitive déjà acquise.");
            return;
        }
        List<ProductDetails.OneTimePurchaseOfferDetails> offers =
            product.getOneTimePurchaseOfferDetailsList();
        BillingFlowParams.ProductDetailsParams.Builder item =
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product);
        String offerToken = offers.get(0).getOfferToken();
        if (offerToken != null && !offerToken.isEmpty()) {
            item.setOfferToken(offerToken);
        }
        BillingResult result = billing.launchBillingFlow(activity,
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(item.build()))
                .build());
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            report("Impossible d'ouvrir la fenêtre Google Play.");
        }
    }

    private void report(String message) {
        main.post(() -> {
            if (!closed && !activity.isFinishing() && !activity.isDestroyed()) {
                listener.onBillingStatus(message, ready(), price);
            }
        });
    }

    public void close() {
        closed = true;
        billing.endConnection();
    }
}
