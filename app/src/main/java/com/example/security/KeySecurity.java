package com.example.security;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

/**
 * Security helper for Cafe Bazaar In-App Billing (Phase 1).
 * Reconstructs RSA Public Key at runtime using XOR bitwise manipulation
 * to prevent static analysis (e.g. JADX/String extraction) and obfuscate the key.
 */
public class KeySecurity {

    private static final String TAG = "BazaarSecurity";
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    // Obfuscated Base64 RSA Public Key bytes stored as byte array chunks
    // To be XORed at runtime with XOR_MASK
    private static final byte[] OBFUSCATED_KEY_BYTES = new byte[] {
        (byte) 0x63, (byte) 0x12, (byte) 0x7E, (byte) 0x3A,
        (byte) 0x21, (byte) 0x45, (byte) 0x1B, (byte) 0x09,
        (byte) 0x3F, (byte) 0x5C, (byte) 0x71, (byte) 0x2B
    };

    // Secret XOR mask byte array
    private static final byte[] XOR_MASK = new byte[] {
        (byte) 0x5A, (byte) 0x7E, (byte) 0x1F, (byte) 0x32
    };

    /**
     * Reconstructs the RSA Public Key String dynamically at runtime using XOR operations.
     * Prevents static string search tools from locating the plain-text Base64 key.
     *
     * @param obfuscatedBytes Array of encrypted/XORed bytes representing the Base64 key
     * @param mask Secret XOR mask array
     * @return Reconstructed Base64 RSA Public Key string
     */
    public static String getDeobfuscatedPublicKey(byte[] obfuscatedBytes, byte[] mask) {
        if (obfuscatedBytes == null || mask == null || mask.length == 0) {
            return "";
        }
        byte[] deobfuscated = new byte[obfuscatedBytes.length];
        for (int i = 0; i < obfuscatedBytes.length; i++) {
            deobfuscated[i] = (byte) (obfuscatedBytes[i] ^ mask[i % mask.length]);
        }
        return new String(deobfuscated);
    }

    /**
     * Reconstructs the default Cafe Bazaar RSA Public Key at runtime.
     * Developers should replace OBFUSCATED_KEY_BYTES and XOR_MASK with their encrypted key bytes.
     */
    public static String getBazaarPublicKey() {
        return getDeobfuscatedPublicKey(OBFUSCATED_KEY_BYTES, XOR_MASK);
    }

    /**
     * Generates a PublicKey object from a Base64-encoded string.
     */
    public static PublicKey generatePublicKey(String encodedPublicKey) {
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (Exception e) {
            Log.e(TAG, "Error generating RSA Public Key", e);
            return null;
        }
    }

    /**
     * Verifies that the purchase data received from Bazaar was signed by Bazaar's RSA private key.
     *
     * @param publicKey Base64-encoded RSA public key
     * @param signedData JSON payload string received from Bazaar
     * @param signature Signature string received from Bazaar
     * @return true if valid signature, false otherwise
     */
    public static boolean verifyPurchase(String publicKey, String signedData, String signature) {
        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(publicKey) || TextUtils.isEmpty(signature)) {
            Log.e(TAG, "Purchase verification failed due to missing or empty input.");
            return false;
        }

        PublicKey key = generatePublicKey(publicKey);
        if (key == null) {
            Log.e(TAG, "Invalid RSA Public Key.");
            return false;
        }

        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(key);
            sig.update(signedData.getBytes());
            byte[] decodedSignature = Base64.decode(signature, Base64.DEFAULT);
            if (!sig.verify(decodedSignature)) {
                Log.e(TAG, "Signature verification failed for signed data!");
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception during purchase signature verification", e);
            return false;
        }
    }
}
