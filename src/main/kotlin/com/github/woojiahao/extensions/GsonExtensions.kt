package com.github.woojiahao.extensions

import com.google.gson.Gson

inline fun <reified T> Gson.read(source: String) = fromJson<T>(source, T::class.java)