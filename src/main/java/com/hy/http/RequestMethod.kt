package com.hy.http

/**
 * RequestMethod HTTP request method.

 * @author HeYan
 * *
 * @time 2017/5/24 11:41
 */
enum class RequestMethod constructor(private val value: String) {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    HEAD("HEAD"),

    OPTIONS("OPTIONS"),

    TRACE("TRACE"),

    PATCH("PATCH");

    override fun toString(): String {
        return this.value
    }

}
