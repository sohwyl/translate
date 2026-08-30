package com.example.billing;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;
import com.example.security.KeySecurity;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for Cafe Bazaar In-App Billing Service (v3 - AIDL).
 * Phase 2 Implementation:
 * - Launching Non-Consumable (VIP) Purchases with Secure Developer Payload (Nonce)
 * - Handling activity result and verifying RSA Digital Signatures
 * - Automatic startup purchase restoration (checkVipStatus)
 * - Strict non-consumable protection preventing consumePurchase on VIP items
 * - Comprehensive response code error handling
 */
public class BazaarBillingManager {

    private static final String TAG = "BazaarBillingManager";
    private static final String BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar";
    private static final String BAZAAR_SERVICE_ACTION = "ir.cafebazaar.pardakht.InAppBillingService.BIND";
    private static final String PREFS_NAME = "bazaar_billing_prefs";
    private static final String KEY_SAVED_PAYLOAD = "last_developer_payload";

    public static final int API_VERSION = 3;
    public static final String ITEM_TYPE_INAPP = "inapp";

    // Non-Consumable SKUs for VIP unlock
    public static final String SKU_VIP_PASS = "vip_membership";
    public static final String SKU_PREMIUM_UPGRADE = "premium_upgrade";

    // Bazaar Response Codes
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE = 2;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    private final Context context;
    private final SharedPreferences prefs;
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;
    private boolean mIsBound = false;
    private BillingListener billingListener;
    private String lastDeveloperPayload;

    public interface BillingListener {
        void onServiceConnected();
        void onServiceDisconnected();
        void onError(String message);
    }

    public interface PurchaseFlowListener {
        void onPurchaseFlowStarted();
        void onItemAlreadyOwned(String sku);
        void onPurchaseCanceled();
        void onPurchaseFailed(int responseCode, String message);
    }

    public interface PurchaseResultListener {
        void onPurchaseSuccess(String purchaseData, String signature, String orderId, String sku, String purchaseToken);
        void onItemAlreadyOwned(String sku);
        void onPurchaseCanceled();
        void onSignatureVerificationFailed();
        void onPayloadMismatch();
        void onPurchaseFailed(int responseCode, String message);
    }

    public interface VipStatusListener {
        void onVipStatusChecked(boolean isVipActive, String sku, String purchaseData);
    }

    public BazaarBillingManager(Context context, BillingListener listener) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.billingListener = listener;
        this.lastDeveloperPayload = prefs.getString(KEY_SAVED_PAYLOAD, null);
    }

    /**
     * Connects to Cafe Bazaar InAppBillingService via AIDL.
     */
    public void connectService() {
        if (mIsBound && mService != null) {
            if (billingListener != null) {
                billingListener.onServiceConnected();
            }
            return;
        }

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "Bazaar InAppBillingService connected.");
                mService = IInAppBillingService.Stub.asInterface(service);
                mIsBound = true;
                if (billingListener != null) {
                    billingListener.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "Bazaar InAppBillingService disconnected.");
                mService = null;
                mIsBound = false;
                if (billingListener != null) {
                    billingListener.onServiceDisconnected();
                }
            }
        };

        Intent serviceIntent = new Intent(BAZAAR_SERVICE_ACTION);
        serviceIntent.setPackage(BAZAAR_PACKAGE_NAME);

        try {
            boolean bound = context.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
            if (!bound) {
                Log.i(TAG, "Cafe Bazaar service is not available on this device.");
                if (billingListener != null) {
                    billingListener.onError("برنامه کافه بازار روی دستگاه نصب نیست یا اتصال برقرار نشد.");
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception while binding Bazaar service: " + e.getMessage());
            if (billingListener != null) {
                billingListener.onError("خطا در برقراری ارتباط با سرویس بازار: " + e.getMessage());
            }
        }
    }

    /**
     * Disconnects and unbinds from Cafe Bazaar service.
     */
    public void disconnectService() {
        if (mIsBound && mServiceConn != null) {
            try {
                context.unbindService(mServiceConn);
            } catch (Exception e) {
                Log.w(TAG, "Error unbinding service: " + e.getMessage());
            }
            mIsBound = false;
            mService = null;
            mServiceConn = null;
        }
    }

    public boolean isConnected() {
        return mIsBound && mService != null;
    }

    /**
     * Generates a cryptographically secure random Nonce string for developerPayload
     * to prevent Replay Attacks.
     */
    private String generateSecureNonce() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Launches the purchase intent for a non-consumable product (e.g. premium_upgrade / vip_membership).
     * Uses a secure random developerPayload to prevent Replay Attacks.
     */
    public void launchVipPurchase(Activity activity, String sku, int requestCode, PurchaseFlowListener listener) {
        if (!isConnected()) {
            if (listener != null) {
                listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE, "سرویس پرداخت کافه بازار متصل نیست.");
            }
            return;
        }

        try {
            // Generate & persist cryptographically secure developer payload (Nonce)
            String nonce = generateSecureNonce();
            this.lastDeveloperPayload = nonce;
            prefs.edit().putString(KEY_SAVED_PAYLOAD, nonce).apply();

            Bundle buyIntentBundle = mService.getBuyIntent(API_VERSION, context.getPackageName(), sku, ITEM_TYPE_INAPP, nonce);
            if (buyIntentBundle == null) {
                if (listener != null) {
                    listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "پاسخی از بازار دریافت نشد.");
                }
                return;
            }

            int responseCode = buyIntentBundle.getInt("RESPONSE_CODE", BILLING_RESPONSE_RESULT_ERROR);
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                android.app.PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                if (pendingIntent != null) {
                    if (listener != null) {
                        listener.onPurchaseFlowStarted();
                    }
                    activity.startIntentSenderForResult(
                            pendingIntent.getIntentSender(),
                            requestCode,
                            new Intent(),
                            0, 0, 0
                    );
                } else {
                    if (listener != null) {
                        listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "تراکنش قابل اجرا نیست (PendingIntent خالی است).");
                    }
                }
            } else if (responseCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                Log.i(TAG, "Item " + sku + " is already owned by user.");
                if (listener != null) {
                    listener.onItemAlreadyOwned(sku);
                }
            } else if (responseCode == BILLING_RESPONSE_RESULT_USER_CANCELED) {
                if (listener != null) {
                    listener.onPurchaseCanceled();
                }
            } else {
                String errorMsg = getErrorMessage(responseCode);
                Log.e(TAG, "getBuyIntent failed code: " + responseCode + " msg: " + errorMsg);
                if (listener != null) {
                    listener.onPurchaseFailed(responseCode, errorMsg);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception launching purchase flow", e);
            if (listener != null) {
                listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "خطا در شروع فرآیند خرید: " + e.getMessage());
            }
        }
    }

    /**
     * Handles activity result from Bazaar purchase flow.
     * Validates INAPP_PURCHASE_DATA, developerPayload nonce, and RSA digital signature using KeySecurity.
     */
    public void handleActivityResult(int requestCode, int expectedRequestCode, int resultCode, Intent data, PurchaseResultListener listener) {
        if (requestCode != expectedRequestCode) {
            return;
        }

        if (data == null) {
            if (listener != null) {
                listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "اطلاعات خریدی بازگردانده نشد.");
            }
            return;
        }

        int responseCode = data.getIntExtra("RESPONSE_CODE", BILLING_RESPONSE_RESULT_OK);

        if (resultCode == Activity.RESULT_OK && responseCode == BILLING_RESPONSE_RESULT_OK) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (purchaseData == null || dataSignature == null) {
                Log.e(TAG, "Purchase data or signature is null.");
                if (listener != null) {
                    listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "دادههای خرید ناقص هستند.");
                }
                return;
            }

            try {
                JSONObject json = new JSONObject(purchaseData);
                String sku = json.optString("productId");
                String orderId = json.optString("orderId");
                String purchaseToken = json.optString("purchaseToken");
                String returnedPayload = json.optString("developerPayload");

                // 1. Replay attack prevention: verify developerPayload matches
                String expectedPayload = prefs.getString(KEY_SAVED_PAYLOAD, lastDeveloperPayload);
                if (expectedPayload != null && !expectedPayload.equals(returnedPayload)) {
                    Log.e(TAG, "SECURITY RISK: Developer payload mismatch! Expected: " + expectedPayload + ", Got: " + returnedPayload);
                    if (listener != null) {
                        listener.onPayloadMismatch();
                    }
                    return;
                }

                // 2. RSA Signature Verification
                String rsaPublicKey = KeySecurity.getBazaarPublicKey();
                boolean isSignatureValid = KeySecurity.verifyPurchase(rsaPublicKey, purchaseData, dataSignature);

                if (isSignatureValid) {
                    Log.i(TAG, "✅ Purchase signature verified successfully for SKU: " + sku);
                    if (listener != null) {
                        listener.onPurchaseSuccess(purchaseData, dataSignature, orderId, sku, purchaseToken);
                    }
                } else {
                    Log.e(TAG, "❌ SECURITY FAILURE: Purchase signature verification failed!");
                    if (listener != null) {
                        listener.onSignatureVerificationFailed();
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error parsing purchase data JSON", e);
                if (listener != null) {
                    listener.onPurchaseFailed(BILLING_RESPONSE_RESULT_ERROR, "خطا در پردازش اطلاعات خرید: " + e.getMessage());
                }
            }

        } else if (responseCode == BILLING_RESPONSE_RESULT_USER_CANCELED || resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "Purchase canceled by user.");
            if (listener != null) {
                listener.onPurchaseCanceled();
            }
        } else if (responseCode == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
            Log.i(TAG, "Item already owned during result callback.");
            if (listener != null) {
                listener.onItemAlreadyOwned(SKU_PREMIUM_UPGRADE);
            }
        } else {
            String errorMsg = getErrorMessage(responseCode);
            Log.e(TAG, "Purchase flow returned error code: " + responseCode + " - " + errorMsg);
            if (listener != null) {
                listener.onPurchaseFailed(responseCode, errorMsg);
            }
        }
    }

    /**
     * Startup Restoration (Restore Purchases):
     * Queries all owned non-consumable items from Cafe Bazaar and verifies their RSA signature.
     * Automatically unlocks VIP features if valid purchase exists.
     */
    public void checkVipStatus(String targetSku, VipStatusListener listener) {
        if (!isConnected()) {
            if (listener != null) {
                listener.onVipStatusChecked(false, targetSku, null);
            }
            return;
        }

        try {
            Bundle purchases = mService.getPurchases(API_VERSION, context.getPackageName(), ITEM_TYPE_INAPP, null);
            if (purchases == null) {
                if (listener != null) {
                    listener.onVipStatusChecked(false, targetSku, null);
                }
                return;
            }

            int responseCode = purchases.getInt("RESPONSE_CODE", BILLING_RESPONSE_RESULT_ERROR);
            if (responseCode == BILLING_RESPONSE_RESULT_OK) {
                ArrayList<String> ownedSkus = purchases.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList = purchases.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList = purchases.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");

                if (ownedSkus != null && purchaseDataList != null && signatureList != null) {
                    String rsaPublicKey = KeySecurity.getBazaarPublicKey();
                    for (int i = 0; i < ownedSkus.size(); i++) {
                        String sku = ownedSkus.get(i);
                        if (sku.equals(targetSku) || sku.equals(SKU_VIP_PASS) || sku.equals(SKU_PREMIUM_UPGRADE)) {
                            String purchaseData = purchaseDataList.get(i);
                            String signature = signatureList.get(i);

                            boolean isValid = KeySecurity.verifyPurchase(rsaPublicKey, purchaseData, signature);
                            if (isValid) {
                                Log.i(TAG, "VIP status restored and verified for SKU: " + sku);
                                if (listener != null) {
                                    listener.onVipStatusChecked(true, sku, purchaseData);
                                }
                                return;
                            } else {
                                Log.e(TAG, "Signature check failed during purchase restoration for SKU: " + sku);
                            }
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException checking purchases", e);
        }

        if (listener != null) {
            listener.onVipStatusChecked(false, targetSku, null);
        }
    }

    /**
     * CRITICAL SECURITY & LOGIC RULE:
     * Non-Consumable (VIP / Premium Upgrade) products MUST NOT BE CONSUMED.
     * Calling consumePurchase on a VIP SKU will cause the user to lose their permanent VIP ownership!
     */
    public int consumePurchase(String purchaseToken, String sku) {
        if (SKU_VIP_PASS.equals(sku) || SKU_PREMIUM_UPGRADE.equals(sku)) {
            Log.e(TAG, "CRITICAL ERROR: Attempted to consume non-consumable product (" + sku + "). Operation BLOCKED.");
            return BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
        }

        if (!isConnected()) {
            return BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE;
        }

        try {
            return mService.consumePurchase(API_VERSION, context.getPackageName(), purchaseToken);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in consumePurchase", e);
            return BILLING_RESPONSE_RESULT_ERROR;
        }
    }

    /**
     * Fetches details (price, title, description) for product SKUs.
     */
    public Bundle getSkuDetails(List<String> skuList) {
        if (!isConnected()) return null;
        try {
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", new ArrayList<>(skuList));
            return mService.getSkuDetails(API_VERSION, context.getPackageName(), ITEM_TYPE_INAPP, querySkus);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getSkuDetails", e);
            return null;
        }
    }

    /**
     * Translates Bazaar response code integers into user-friendly localized messages.
     */
    public static String getErrorMessage(int responseCode) {
        switch (responseCode) {
            case BILLING_RESPONSE_RESULT_USER_CANCELED:
                return "خرید توسط کاربر لغو شد.";
            case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
                return "ارتباط با سرویس بازار برقرار نیست.";
            case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                return "پرداخت درونبرنامهای در نسخه فعلی بازار پشتیبانی نمیشود.";
            case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                return "محصول مورد نظر در بازار یافت نشد یا غیرفعال است.";
            case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                return "خطای پیکربندی برنامه (Developer Error). شناسه محصول یا تنظیمات نامعتبر است.";
            case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                return "شما قبلاً این محصول را خریداری کردهاید.";
            case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
                return "محصول در لیست خریدهای شما یافت نشد.";
            case BILLING_RESPONSE_RESULT_ERROR:
            default:
                return "خطای ناشناخته در انجام تراکنش (کد: " + responseCode + ").";
        }
    }
}
