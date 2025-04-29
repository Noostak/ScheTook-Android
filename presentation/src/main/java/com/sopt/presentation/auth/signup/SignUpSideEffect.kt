package com.sopt.presentation.auth.signup

sealed interface SignUpSideEffect {
    data class NavigateToCheckInvite(val name: String) : SignUpSideEffect
    data object RequestImagePicker : SignUpSideEffect
    data object ShowSnackBar : SignUpSideEffect
    data object ShowErrorDialog : SignUpSideEffect
}
