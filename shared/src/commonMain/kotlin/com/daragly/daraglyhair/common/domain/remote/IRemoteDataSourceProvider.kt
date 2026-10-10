package com.daragly.daraglyhair.common.domain.remote

import kotlinx.serialization.KSerializer

interface IRemoteDataSourceProvider {
    suspend fun <ResponseBody, RequestBody> post(
        endpoint: String,
        params: Map<String, Any>? = null,
        headers: Map<String, Any>? = null,
        requestBody: RequestBody? = null,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody

    suspend fun <ResponseBody> get(
        endpoint: String,
        params: Map<String, Any>? = null,
        headers: Map<String, Any>? = null,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody

    suspend fun <ResponseBody, RequestBody> put(
        endpoint: String,
        params: Map<String, Any> = emptyMap(),
        headers: Map<String, Any>? = null,
        requestBody: RequestBody? = null,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody

    suspend fun <ResponseBody> delete(
        endpoint: String,
        params: Map<String, Any>? = null,
        headers: Map<String, Any>? = null,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody

    suspend fun <ResponseBody> postWithFile(
        endpoint: String,
        params: Map<String, Any>? = null,
        headers: Map<String, Any>? = null,
        files: List<Pair<String, IFile>>,
        requestBody: Map<String, Any>? = null,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody
}
