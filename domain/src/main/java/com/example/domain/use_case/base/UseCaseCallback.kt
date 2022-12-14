package com.example.domain.use_case.base

import com.example.domain.model.ApiError

interface UseCaseCallback<T> {
    fun onSuccess(t: ArrayList<T>)
    fun onError(throwable: ApiError)
}