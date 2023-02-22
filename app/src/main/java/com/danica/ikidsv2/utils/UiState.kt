package com.danica.ikidsv2.utils

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Successful<out T>(val data : T) : UiState<T>()
    data class Failed(val message : String) : UiState<Nothing>()
}
