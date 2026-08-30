package com.example.billing

import retrofit2.Response
import retrofit2.http.*

data class VerifyPurchaseRequest(
    val purchaseToken: String,
    val productId: String = "premium_unlock_v1",
    val deviceId: String,
    val packageName: String = "com.example"
)

data class VerifyPurchaseResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val sku: String?,
    val error: String?
)

data class RestorePurchaseRequest(
    val purchaseToken: String,
    val productId: String = "premium_unlock_v1",
    val deviceId: String,
    val packageName: String = "com.example"
)

data class RestorePurchaseResponse(
    val success: Boolean,
    val restored: Boolean?,
    val message: String?,
    val token: String?,
    val error: String?
)

data class PremiumContentResponse(
    val status: String,
    val totalPhrases: Int,
    val audioQuality: String,
    val features: List<String>
)

interface BazaarBackendApi {

    @POST("verify-purchase")
    suspend fun verifyPurchase(
        @Body request: VerifyPurchaseRequest
    ): Response<VerifyPurchaseResponse>

    @POST("restore-purchase")
    suspend fun restorePurchase(
        @Body request: RestorePurchaseRequest
    ): Response<RestorePurchaseResponse>

    @GET("premium-content")
    suspend fun getPremiumContent(
        @Header("Authorization") authHeader: String
    ): Response<PremiumContentResponse>
}
