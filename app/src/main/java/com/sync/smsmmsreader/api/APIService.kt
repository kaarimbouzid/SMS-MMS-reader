package com.sync.smsmmsreader.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("mmsReceived")
    suspend fun sendMMS(@Body requestBody: RequestBody): Response<ResponseBody>

    @POST("smsReceived")
    suspend fun sendSMS(@Body requestBody: RequestBody): Response<ResponseBody>
}