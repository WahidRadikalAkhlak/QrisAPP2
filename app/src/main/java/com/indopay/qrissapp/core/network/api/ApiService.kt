package com.indopay.qrissapp.core.network.api

import com.indopay.qrissapp.core.data.response.LastTransactionResponse
import com.indopay.qrissapp.core.data.response.LoginResponse
import com.indopay.qrissapp.core.data.response.LoginVerifyResponse
import com.indopay.qrissapp.core.data.response.LogoutResponse
import com.indopay.qrissapp.core.data.response.ProfileMerchantResponse
import com.indopay.qrissapp.core.data.response.QrModelResponse
import com.indopay.qrissapp.core.data.response.TransactionByDateResponse
import com.indopay.qrissapp.core.data.response.TransactionDetailResponse
import com.indopay.qrissapp.utils.ConstrainKey
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST(ConstrainKey.END_API_LOGIN)
    suspend fun loginUser(
        @Field("username") username: String,
    ) : LoginResponse

    @POST("none")
    suspend fun regiter(
        @Body param: Map<String, String>,
    )

    @FormUrlEncoded
    @POST(ConstrainKey.END_API_OTP_LOGIN)
    suspend fun loginVerifyWithOtp(
        @Field("username") email: String,
        @Field("otp") otpNumber: String,
    ) : LoginVerifyResponse

    @POST(ConstrainKey.END_API_LOGOUT)
    suspend fun reqLogout(
        @Header("Auth-Token") authToken: String,
    ) : LogoutResponse
    @FormUrlEncoded
    @POST(ConstrainKey.END_API_PROFILE)
    suspend fun getProfileByUsername(
        @Header("Auth-Token") authToken: String,
        @Field("username") username: String
    ) : ProfileMerchantResponse

    @FormUrlEncoded
    @POST(ConstrainKey.END_API_LAST_TRX)
    suspend fun getLastTransaction(
        @Header("Auth-Token") authToken: String,
        @Field("page") page: Int =  1,
        @Field("pageSize") pageSize: Int = 5,
        @Field("username") username: String,
        @Field("MID") mID: String,
    ) : LastTransactionResponse

    @FormUrlEncoded
    @POST(ConstrainKey.END_API_TRX_BY_DATE)
    suspend fun getTransactionByDate(
        @Header("Auth-Token") authToken: String,
        @Field("page") page: Int = 1,
        @Field("pageSize") pageSize: Int = 6,
        @Field("username") username: String,
        @Field("MID") mId: String,
        @Field("firstDate") firstDate: String,
        @Field("lastDate") lastDate: String,
    ) : TransactionByDateResponse

    @POST(ConstrainKey.END_API_TRX_DETAIL)
    suspend fun getTransactionDetail(
        @Header("Auth-Token") authToken: String,
        @Body params: Map<String, String>
    ) : TransactionDetailResponse

    @FormUrlEncoded
    @POST(ConstrainKey.END_API_FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Header("Auth-Token") accessToken: String,
        @Field("email") email: String,
    )

    @POST(ConstrainKey.END_API_GENERATE_QR)
    suspend fun generateQrCode(
        @Header("Auth-Token") accessToken: String,
        @Path("merchant_id") mID: Int,
        @Path("qr_type") qrType: String,
        @Path("nominal") nominal: Int,
    ) : QrModelResponse

    @POST(ConstrainKey.END_API_REGENERATE_OTP)
    suspend fun reGenerateOtp(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("otp_number") otpNumber: String,
    )

    @Multipart
    @POST("")
    suspend fun postDataMultipart(

    )



    @GET(ConstrainKey.END_API_MERCHANT_TRX)
    suspend fun getTrxMerchant(
        @HeaderMap headers: Map<String, String>,
    )
}