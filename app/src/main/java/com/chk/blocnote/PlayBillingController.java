package com.chk.blocnote;

import android.app.Activity;
import android.widget.Toast;
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

/** Achat non consommable. Aucune cle de service Google ne doit etre stockee dans l'APK.
 * TODO production : verifier le token sur le serveur avec Google Play Developer API
 * avant d'accorder le droit definitif et gerer les remboursements (RTDN).
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
    private ProductDetails product;
    private boolean connected;
    private String price = "1,19 €"; // Indicatif ; le prix officiel vient de Google Play.
    private boolean closed;

    public PlayBillingController(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
        billing = BillingClient.newBuilder(activity.getApplicationContext())
            .setListener(this::purchasesUpdated)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts().build())
            .enableAutoServiceReconnection()
            .build();
    }

    public void connect() {
        if (closed) return;
        report("Connexion à Google Play…", false);
        if (billing.isReady()) { connected = true; queryProducts(); queryOwned(false); return; }
        billing.startConnection(new BillingClientStateListener() {
            @Override public void onBillingSetupFinished(BillingResult result) {
                if (closed) return;
                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connected = true;
                    queryOwned(false);
                    queryProducts();
                } else report("Google Play indisponible : " + result.getResponseCode(), false);
            }
            @Override public void onBillingServiceDisconnected() {
                connected = false; report("Connexion Google Play interrompue", false);
            }
        });
    }

    private void queryProducts() {
        if (closed || !connected) return;
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(Collections.singletonList(
                QueryProductDetailsParams.Product.newBuilder()
                  .setProductId(PRODUCT_ID)
                  .setProductType(BillingClient.ProductType.INAPP).build()))
            .build();
        billing.queryProductDetailsAsync(params, (result, details) -> {
            if (closed) return;
            product = null;
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (ProductDetails item : details.getProductDetailsList()) {
                    if (PRODUCT_ID.equals(item.getProductId())) { product = item; break; }
                }
                if (product != null && product.getOneTimePurchaseOfferDetailsList() != null
                        && !product.getOneTimePurchaseOfferDetailsList().isEmpty()) {
                    price = product.getOneTimePurchaseOfferDetailsList().get(0).getFormattedPrice();
                    report("Achat définitif disponible sur Google Play", true);
                    return;
                }
                report("Produit non encore activé sur Google Play Console", false);
            } else report("Tarif Google Play indisponible", false);
        });
    }

    public void restorePurchases() {
        if (!billing.isReady()) { connect(); return; }
        queryOwned(true);
    }

    private void queryOwned(boolean manual) {
        if (closed || !billing.isReady()) return;
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build();
        billing.queryPurchasesAsync(params, (result, purchases) -> {
            if (closed) return;
            if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                if (manual) report("Restauration impossible : réessayez avec Internet", product != null);
                return; // Jamais retirer l'accès sur une simple erreur réseau.
            }
            boolean found = applyPurchases(purchases);
            if (found) listener.onAccessRestored();
            else if (manual) report("Aucun achat définitif trouvé sur ce compte Google Play", product != null);
        });
    }

    private boolean applyPurchases(List<Purchase> purchases) {
        boolean found = false;
        if (purchases != null) for (Purchase p : purchases) {
            if (!p.getProducts().contains(PRODUCT_ID)) continue;
            if (p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                found = true;
                if (!p.isAcknowledged()) {
                    AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(p.getPurchaseToken()).build();
                    billing.acknowledgePurchase(params, result -> {
                        if (!closed && result.getResponseCode() != BillingClient.BillingResponseCode.OK)
                            report("Achat reconnu, confirmation Google Play à réessayer", product != null);
                    });
                }
            } else if (p.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                report("Paiement en attente : aucun débit confirmé", product != null);
            }
        }
        // La relecture reussie des achats Play remet à jour le droit (remboursements).
        TrialAccess.updatePurchase(activity, found);
        return found;
    }

    private void purchasesUpdated(BillingResult result, List<Purchase> purchases) {
        if (closed) return;
        int code = result.getResponseCode();
        if (code == BillingClient.BillingResponseCode.OK && purchases != null) {
            // Ne jamais effacer un droit si le callback n'inclut qu'un achat en attente.
            boolean found = false;
            for (Purchase p : purchases) if (p.getProducts().contains(PRODUCT_ID)
                    && p.getPurchaseState() == Purchase.PurchaseState.PURCHASED) found = true;
            if (found) {
                // La vérification Play côté client est provisoire : backend requis avant vente réelle.
                TrialAccess.updatePurchase(activity, true);
                applyPurchases(purchases);
                listener.onAccessRestored();
            } else report("Paiement en attente : achat non encore activé", product != null);
        } else if (code != BillingClient.BillingResponseCode.USER_CANCELED) {
            report("Achat non terminé. Code Google Play : " + code, product != null);
        }
    }

    public void buy() {
        if (closed) return;
        if (!billing.isReady() || product == null) {
            report("Le produit n'est pas disponible. Vérifier Play Console.", false);
            connect();
            return;
        }
        List<ProductDetails.OneTimePurchaseOfferDetails> offers =
            product.getOneTimePurchaseOfferDetailsList();
        if (offers == null || offers.isEmpty()) { report("Aucune offre d'achat disponible", false); return; }
        BillingFlowParams.ProductDetailsParams p =
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product)
                .setOfferToken(offers.get(0).getOfferToken()).build();
        BillingFlowParams params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(Collections.singletonList(p)).build();
        BillingResult result = billing.launchBillingFlow(activity, params);
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK)
            report("Google Play ne peut pas lancer l'achat", true);
    }

    private void report(String message, boolean enabled) {
        activity.runOnUiThread(() -> {
            if (!closed && !activity.isFinishing() && !activity.isDestroyed())
                listener.onBillingStatus(message, enabled, price);
        });
    }
    public void close() {
        closed = true;
        billing.endConnection();
    }
}
