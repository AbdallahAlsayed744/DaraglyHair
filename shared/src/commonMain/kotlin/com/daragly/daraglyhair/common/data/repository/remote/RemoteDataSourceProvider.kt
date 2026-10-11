package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.domain.remote.IFile
import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class RemoteDataSourceProvider(
    private val apiService: ApiService,
    private val json: Json,
) : IRemoteDataSourceProvider {

    override suspend fun <ResponseBody, RequestBody> post(
        endpoint: String,
        params: Map<String, Any>?,
        headers: Map<String, Any>?,
        requestBody: RequestBody?,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val response = apiService.post(
            endPoint = endpoint,
            params = params,
            headers = headers,
            requestBody = requestBody ?: Unit,
        )
        return handleResponse(response, serializer)
    }

    override suspend fun <ResponseBody> get(
        endpoint: String,
        params: Map<String, Any>?,
        headers: Map<String, Any>?,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val response = apiService.get(endPoint = endpoint, params = params, headers = headers)
        return handleResponse(response, serializer)
    }

    override suspend fun <ResponseBody, RequestBody> put(
        endpoint: String,
        params: Map<String, Any>,
        headers: Map<String, Any>?,
        requestBody: RequestBody?,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val response = apiService.put(
            endPoint = endpoint,
            params = params,
            headers = headers,
            requestBody = requestBody ?: Unit,
        )
        return handleResponse(response, serializer)
    }

    override suspend fun <ResponseBody> delete(
        endpoint: String,
        params: Map<String, Any>?,
        headers: Map<String, Any>?,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val response = apiService.delete(
            endPoint = endpoint,
            params = params,
            headers = headers ?: emptyMap(),
        )
        return handleResponse(response, serializer)
    }

    override suspend fun <ResponseBody> postWithFile(
        endpoint: String,
        params: Map<String, Any>?,
        headers: Map<String, Any>?,
        files: List<Pair<String, IFile>>,
        requestBody: Map<String, Any>?,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val response = apiService.postWithFile(
            endPoint = endpoint,
            params = params,
            headers = headers,
            files = files,
            requestBody = requestBody ?: emptyMap(),
        )
        return handleResponse(response, serializer)
    }

    private suspend fun <ResponseBody> handleResponse(
        response: HttpResponse,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val text = response.bodyAsText()
        
        // Log the response for debugging
        println("API Response Status: ${response.status}")
        println("API Response Body: $text")
        
        try {
            return json.decodeFromString(serializer, text)
        } catch (e: Exception) {
            println("========== DESERIALIZATION ERROR ==========")
            println("Failed to deserialize response: ${e.message}")
            println("Expected type: ${serializer.descriptor.serialName}")
            println("Response status: ${response.status}")
            println("Response body: $text")
            println("==========================================")
            throw e
        }
    }
}
