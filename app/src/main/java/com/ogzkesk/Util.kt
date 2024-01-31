package com.ogzkesk

import android.content.Context
import android.widget.Toast
import java.util.Locale

fun String.capitalize(): String{
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}