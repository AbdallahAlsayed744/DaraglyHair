package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.domain.remote.IFile
import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
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

    @Suppress("UNCHECKED_CAST")
    private suspend fun <ResponseBody> handleResponse(
        response: HttpResponse,
        serializer: KSerializer<ResponseBody>,
    ): ResponseBody {
        val text = response.bodyAsText()
        // Endpoints such as PUT api/personal/profile and the appointment ones answer with a plain
        // body — a bare id, or nothing at all — that is not a JSON document worth decoding.
        if (serializer.descriptor.serialName == STRING_SERIAL_NAME) {
            val raw = text.trim()
            val value = runCatching { json.decodeFromString(String.serializer(), raw) }
                .getOrDefault(raw)
            return value as ResponseBody
        }
        try {
            return json.decodeFromString(serializer, text)
        } catch (e: Exception) {
            // Bodies are never printed: auth responses carry tokens. The Ktor Logging plugin
            // shows the raw payload with Authorization sanitized.
            DebugLog.d { "AUTH decode failed: ${e.message} type=${serializer.descriptor.serialName} status=${response.status}" }
            throw e
        }
    }

    companion object {
        private const val STRING_SERIAL_NAME = "kotlin.String"
    }
}
