package org.geonotes.client.utils.rest

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

import android.util.Log

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException


class GsonPostRequest<ResponseType, RequestType>(
    url: String,
    body: RequestType,
    private val clazz: Class<ResponseType>,
    private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<ResponseType>,
    errorListener: Response.ErrorListener
) : Request<ResponseType>(Method.POST, url, errorListener) {

    private val gson = Gson()

    private val requestBody: String = gson.toJson(body)

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: ResponseType) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ResponseType> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            Response.success(
                gson.fromJson(json, clazz),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }

    override fun getBody(): ByteArray? {
        return try {
            requestBody.toByteArray(charset(PROTOCOL_CHARSET))
        } catch (uee: UnsupportedEncodingException) {
            Log.wtf(
                TAG,
                String.format(
                    "Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody,
                    PROTOCOL_CHARSET
                )
            )
            null
        }

    }

    companion object {
        private val TAG = this::class.simpleName
        private const val PROTOCOL_CHARSET = "utf-8"
    }
}
