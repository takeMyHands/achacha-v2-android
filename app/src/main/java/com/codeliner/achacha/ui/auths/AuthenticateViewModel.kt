package com.codeliner.achacha.ui.auths

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codeliner.achacha.data.patterns.Pattern
import com.codeliner.achacha.data.patterns.PatternRepository
import kotlinx.coroutines.*
import timber.log.Timber

enum class PatternMode {
    LOGIN, SAVE
}

class AuthenticateViewModel(
        private val repository: PatternRepository
): ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val storedPattern = repository.readStoredPattern()

    private val _onLogin = MutableLiveData<Boolean>()
    val onLogin: LiveData<Boolean> get() = _onLogin
    fun loginJob(pattern: Pattern) {
        storedPattern.value?.let {
            when (pattern.patternIsSame(pattern, it)) {
                true -> _onLogin.value = true
                false -> _onLogin.value = false
            }
        }
    }
    fun loginJobComplete() {
        _onLogin.value = null
    }

    fun createPatternJob(pattern: Pattern) {
        uiScope.launch {
            clearPattern()
            createPattern(pattern)
        }
    }

    private suspend fun createPattern(pattern: Pattern) {
        withContext(Dispatchers.IO) {
            repository.createPattern(pattern)
        }
    }

    private val _onClearPattern = MutableLiveData<Boolean>()
    val onClearPattern: LiveData<Boolean> get() = _onClearPattern
    fun clearPatternAsk() {
        _onClearPattern.value = true
    }

    fun clearPatternJob() {
        uiScope.launch {
            clearPattern()
        }
    }

    private suspend fun clearPattern() {
        withContext(Dispatchers.IO) {
            repository.clearPattern()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}