package com.example.rmas18577


import android.content.Context
import android.widget.Toast

interface ToastNotifier {
    fun showToast(message: String)
}



class ToastNotifierImpl(private val context: Context) : ToastNotifier {
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
