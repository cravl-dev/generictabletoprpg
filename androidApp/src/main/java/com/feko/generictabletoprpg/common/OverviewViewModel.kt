package com.feko.generictabletoprpg.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class OverviewViewModel<T> : ViewModel() {
    protected val _items =
        MutableStateFlow<List<T>>(listOf())
    val searchString: Flow<String>
        get() = _searchString
    private var _searchString: MutableStateFlow<String> = MutableStateFlow("")
    val items: Flow<List<T>>
        get() = combinedItemFlow
    private val combinedItemFlow: Flow<List<T>> =
        _items.combine(_searchString) { items, searchString ->
            items.filter { item ->
                item is Named
                        && item.name.lowercase().contains(searchString.lowercase())
            }
        }

    val isDialogVisible: Flow<Boolean>
        get() = _isDialogVisible
    protected val _isDialogVisible = MutableStateFlow(false)

    var dialogTitle: String = ""

    val isFabDropdownMenuExpanded: Flow<Boolean>
        get() = _isFabDropdownMenuExpanded
    protected val _isFabDropdownMenuExpanded = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val allItems =
                withContext(Dispatchers.Default) {
                    getAllItems()
                }
            _items.emit(allItems)
        }
    }

    abstract fun getAllItems(): List<T>

    fun searchStringUpdated(searchString: String) {
        viewModelScope.launch {
            _searchString.emit(searchString)
        }
    }

    fun hideDialog() {
        viewModelScope.launch {
            _isDialogVisible.emit(false)
        }
    }

    fun onDismissFabDropdownMenuRequested() {
        viewModelScope.launch {
            _isFabDropdownMenuExpanded.emit(false)
        }
    }

    fun toggleFabDropdownMenu() {
        viewModelScope.launch {
            _isFabDropdownMenuExpanded.emit(!_isFabDropdownMenuExpanded.value)
        }
    }
}