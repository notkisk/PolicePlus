package com.example.policeplus.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScanViewModel : ViewModel() {
    private val _extractedText = MutableLiveData<String>()
    val extractedText: LiveData<String> = _extractedText

    fun setExtractedText(text: String) {
        _extractedText.value = text
    }
}
