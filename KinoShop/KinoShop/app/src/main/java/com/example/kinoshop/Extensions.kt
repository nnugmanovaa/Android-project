package com.example.kinoshop

import android.widget.EditText
import androidx.annotation.StringRes
import com.google.android.material.textfield.TextInputLayout

val EditText.trimmedText: String
    get() = text?.toString()?.trim().orEmpty()

fun TextInputLayout.showError(@StringRes stringRes: Int) {
    error = context.resources.getString(stringRes)
}

fun TextInputLayout.hideError() {
    error = null
    isErrorEnabled = false
}

