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
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Achat définitif Google Play, validé par le serveur partagé CHK Paiements.
 * Un résultat BillingClient PURCHASED n'est JAMAIS suffisant pour attribuer l'accès.
 * Sans endpoint HTTPS configuré / prêt, aucun parcours d'achat ne s'ouvre.
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
    private final Set<String> verifying = Collections.synchronizedSet(new HashSet<>());
    private final String endpoint = BuildConfig.CHK_PAYMENTS_VERIFY_ENDPOINT;
    private ProductDetails product;
    private String price = "Prix communiqué par Google Play";
    private boolean connected, verifierReady, checkingHealth, closed;

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

    private boolean ready() {
        return !closed && connected && billing.isReady() && product != null && verifierReady;
    }

    public void connect() {
        if (closed) return;
        report("Connexion sécurisée à Google Play et CHK Paiements…");
        checkVerifierHealth();
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
                } else report("Google Play indisponible (" + result.getResponseCode() + ")");
            }
            @Override public void onBillingServiceDisconnected() {
                connected = false;
                report("Connexion Google Play interrompue.");
            }
        });
    }

    private static JSONObject requestJson(URL url, JSONObject payload) throws Exception {
        if (!"https".equalsIgnoreCase(url.getProtocol())) throw new SecurityException("HTTPS obligatoire");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setConnectTimeout(6500);
        connection.setReadTimeout(10000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Accept", "application/json");
        try {
            if (payload != null) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                byte[] data = payload.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream stream = connection.getOutputStream()) { stream.write(data); }
            }
            int status = connection.getResponseCode();
            if (status != 200) throw new IllegalStateException("Réponse du serveur : " + status);
            try (InputStream stream = connection.getInputStream()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int n;
                while ((n = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                    if (out.size() > 16384) throw new IllegalStateException("Réponse trop volumineuse");
                }
                return new JSONObject(out.toString(StandardCharsets.UTF_8.name()));
            }
        } finally { connection.disconnect(); }
    }

    private boolean configured() {
        return endpoint != null && endpoint.startsWith("https://")
                && endpoint.endsWith("/api/v1/play/verify");
    }

    private void checkVerifierHealth() {
        if (closed || checkingHealth) return;
        if (!configured()) {
            verifierReady = false;
            report("Vérificateur CHK Paiements non configuré : vente désactivée.");
            return;
        }
        checkingHealth = true;
        new Thread(() -> {
            boolean available = false;
            try {
                String base = endpoint.substring(0, endpoint.length() - "verify".length());
                JSONObject response = requestJson(new URL(base + "health"), null);
                available = response.optBoolean("billingReady", false)
                    && response.optBoolean("ok", false);
            } catch (Exception ignored) {}
            final boolean value = available;
            main.post(() -> {
                if (closed) return;
                verifierReady = value;
                checkingHealth = false;
                report(value ? "Vérification CHK Paiements disponible." :
                    "CHK Paiements indisponible : aucun paiement ne sera lancé.");
            });
        }, "blocnote-chk-payments-health").start();
    }

    private void queryProducts() {
        if (closed || !connected) return;
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
            .setProductList(Collections.singletonList(
                QueryProductDetailsParams.Product.newBuilder().setProductId(PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP).build()))
            .build();
        billing.queryProductDetailsAsync(params, (result, details) -> {
            if (closed) return;
            product = null;
            if (result.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && details.getProductDetailsList() != null) {
                for (ProductDetails item : details.getProductDetailsList()) {
                    if (PRODUCT_ID.equals(item.getProductId())) { product = item; break; }
                }
                if (product != null && product.getOneTimePurchaseOfferDetailsList() != null
                        && !product.getOneTimePurchaseOfferDetailsList().isEmpty()) {
                    price = product.getOneTimePurchaseOfferDetailsList().get(0).getFormattedPrice();
                    report(verifierReady ? "Achat unique disponible." :
                            "Produit disponible ; vérification du serveur nécessaire.");
                    return;
                }
            }
            report("Produit Google Play non encore activé pour cette application.");
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
                if (manual) report("Restauration impossible : vérifier la connexion Google Play.");
                return; // Ne pas retirer une licence précédemment validée sur erreur réseau.
            }
            boolean foundPurchased = false;
            if (purchases != null) for (Purchase purchase : purchases) {
                if (!purchase.getProducts().contains(PRODUCT_ID)) continue;
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    foundPurchased = true;
                    verifyWithChkPayments(purchase);
                } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                    report("Paiement en attente : accès non activé.");
                }
            }
            if (!foundPurchased) {
                // Succès de queryPurchasesAsync : achat absent = licence révoquée ou jamais acquise.
                TrialAccess.updatePurchase(activity, false);
                report(manual ? "Aucun achat définitif trouvé sur ce compte Play." :
                    "Statut des licences Google Play actualisé.");
            }
        });
    }

    private void purchasesUpdated(BillingResult result, List<Purchase> purchases) {
        if (closed) return;
        if (result.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            report("Achat annulé : aucun paiement effectué.");
            return;
        }
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
            report("Achat non confirmé par Google Play.");
            return;
        }
        for (Purchase purchase : purchases) {
            if (!purchase.getProducts().contains(PRODUCT_ID)) continue;
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                verifyWithChkPayments(purchase);
            else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
                report("Paiement en attente : la licence n'est pas activée.");
        }
    }

    private void verifyWithChkPayments(Purchase purchase) {
        if (closed || purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED
                || !purchase.getProducts().contains(PRODUCT_ID)
                || purchase.getPurchaseToken() == null || purchase.getPurchaseToken().isEmpty())
            return;
        if (!configured()) {
            report("Vérification CHK Paiements non configurée. Achat non débloqué.");
            return;
        }
        final String token = purchase.getPurchaseToken();
        if (!verifying.add(token)) return;
        new Thread(() -> {
            boolean verified = false;
            boolean explicitlyRejected = false;
            String error = "";
            try {
                JSONObject payload = new JSONObject();
                payload.put("packageName", "com.chk.blocnote");
                payload.put("productId", PRODUCT_ID);
                payload.put("purchaseToken", token);
                JSONObject response = requestJson(new URL(endpoint), payload);
                verified = response.optBoolean("verified", false)
                    && PRODUCT_ID.equals(response.optString("productId"))
                    && "com.chk.blocnote".equals(response.optString("packageName"));
            } catch (Exception ex) {
                error = ex.getMessage() == null ? "réseau indisponible" : ex.getMessage();
                // 403 = vérificateur CHK a consulté Google et refuse explicitement l'achat.
                // 503 / panne réseau = état indéterminé : garder une ancienne licence vérifiée.
                explicitlyRejected = "Réponse du serveur : 403".equals(error);
            }
            final boolean ok = verified;
            final boolean revoked = explicitlyRejected;
            final String fail = error;
            main.post(() -> {
                verifying.remove(token);
                if (closed) return;
                if (!ok) {
                    if (revoked) TrialAccess.updatePurchase(activity, false);
                    report("Achat non encore validé par CHK Paiements"
                        + (fail.isEmpty() ? "." : " : " + fail));
                    return;
                }
                // SEUL chemin d'attribution : après validation du token sur le serveur CHK.
                TrialAccess.updatePurchase(activity, true);
                report("Achat unique vérifié : accès définitif activé.");
                listener.onAccessRestored();
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(token).build();
                    billing.acknowledgePurchase(params, result -> {
                        if (!closed && result.getResponseCode() != BillingClient.BillingResponseCode.OK)
                            report("Licence validée ; confirmation Google Play à réessayer.");
                    });
                }
            });
        }, "blocnote-play-verify-chk").start();
    }

    public void buy() {
        if (closed) return;
        if (!ready()) {
            report("Paiement bloqué : catalogue Google Play ou CHK Paiements indisponible.");
            if (!connected) connect(); else checkVerifierHealth();
            return;
        }
        List<ProductDetails.OneTimePurchaseOfferDetails> offers =
            product.getOneTimePurchaseOfferDetailsList();
        if (offers == null || offers.isEmpty()) {
            report("Aucune offre d'achat disponible.");
            return;
        }
        BillingFlowParams.ProductDetailsParams.Builder p =
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(product);
        String offerToken = offers.get(0).getOfferToken();
        if (offerToken != null && !offerToken.isEmpty()) p.setOfferToken(offerToken);
        BillingResult result = billing.launchBillingFlow(activity,
            BillingFlowParams.newBuilder().setProductDetailsParamsList(
                Collections.singletonList(p.build())).build());
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK)
            report("Google Play ne peut pas ouvrir la fenêtre de paiement.");
    }

    private void report(String message) {
        main.post(() -> {
            if (!closed && !activity.isFinishing() && !activity.isDestroyed())
                listener.onBillingStatus(message, ready(), price);
        });
    }

    public void close() {
        closed = true;
        verifying.clear();
        billing.endConnection();
    }
}
