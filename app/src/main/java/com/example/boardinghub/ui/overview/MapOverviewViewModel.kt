package com.example.boardinghub.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapOverviewViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Map Overview"
    }
    val text: LiveData<String> = _text
}