package com.feko.generictabletoprpg.common.filter

import com.feko.generictabletoprpg.filters.Filter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FilterViewModelExtension : IMutableFilterViewModelExtension {
    override val filter: StateFlow<Filter?>
        get() = _filter
    private val _filter = MutableStateFlow<Filter?>(null)
    override val filterButtonVisible: Flow<Boolean>
        get() = _filterButtonVisible
    override val filterOffButtonVisible: Flow<Boolean> = filter.map { it != null }
    private val _filterButtonVisible = MutableStateFlow(false)

    override suspend fun filterUpdated(newFilter: Filter?) {
        _filter.emit(newFilter)
    }

    override suspend fun setFilterButtonVisible(isVisible: Boolean) {
        _filterButtonVisible.emit(isVisible)
    }

}
