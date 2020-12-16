package org.geonotes.client.utils.rest

import android.content.Context
import com.android.volley.Request

import com.android.volley.RequestQueue
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.Volley


class HttpRequestQueue constructor(context: Context, httpStack: BaseHttpStack? = null) {
    companion object {
        @Volatile
        private var INSTANCE: HttpRequestQueue? = null

        fun getInstance(context: Context, httpStack: BaseHttpStack? = null): HttpRequestQueue {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpRequestQueue(context, httpStack).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext, httpStack)
    }

    fun <T> addRequest(req: Request<T>) {
        requestQueue.add(req)
    }
}
