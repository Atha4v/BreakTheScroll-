package com.breakthescroll.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.data.repository.AppRepository
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.FeedMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressUiState(
    val stats: UserStatsEntity = UserStatsEntity(),
    val weeklyBars: List<Float> = List(7) { 0.08f },
    val heatmap: List<Float> = List(35) { 0.12f },
    val recent: List<ActivityCompletionEntity> = emptyList(),
)

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    val darkTheme: StateFlow<Boolean> = repository.observeDarkTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val displayName: StateFlow<String> = repository.observeDisplayName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Atharv")

    val stats: StateFlow<UserStatsEntity> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserStatsEntity())

    val habits: StateFlow<List<HabitEntity>> = repository.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val forYou: StateFlow<List<ActivityCatalogEntity>> = repository.observeForYou()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serendipity: StateFlow<List<ActivityCatalogEntity>> = repository.observeSerendipity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _feedMode = MutableStateFlow(FeedMode.ForYou)
    val feedMode: StateFlow<FeedMode> = _feedMode.asStateFlow()

    private val _buddyMood = MutableStateFlow(BuddyMood.Idle)
    val buddyMood: StateFlow<BuddyMood> = _buddyMood.asStateFlow()

    private val _weeklyBars = MutableStateFlow(List(7) { 0.08f })
    private val _heatmap = MutableStateFlow(List(35) { 0.12f })

    val progress: StateFlow<ProgressUiState> = combine(
        repository.observeStats(),
        repository.observeRecentCompletions(12),
        _weeklyBars,
        _heatmap,
    ) { stats, recent, bars, heat ->
        ProgressUiState(stats = stats, weeklyBars = bars, heatmap = heat, recent = recent)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgressUiState())

    init {
        refreshAnalytics()
    }

    fun setFeedMode(mode: FeedMode) {
        _feedMode.value = mode
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkTheme(enabled) }
    }

    fun acceptActivity(item: ActivityCatalogEntity) {
        viewModelScope.launch {
            repository.acceptActivity(item)
            _buddyMood.value = BuddyMood.Cheer
            refreshAnalytics()
        }
    }

    fun passActivity() {
        _buddyMood.value = BuddyMood.Nudge
    }

    fun completeReset() {
        viewModelScope.launch {
            repository.completeBreathingReset()
            _buddyMood.value = BuddyMood.Cheer
            refreshAnalytics()
        }
    }

    fun addHabit(title: String, cadence: String) {
        viewModelScope.launch {
            val accent = if (darkTheme.value) 0xFF10B981.toInt() else 0xFFC68642.toInt()
            repository.addHabit(title, cadence, accent)
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabit(habit)
            _buddyMood.value = if (!habit.completedToday) BuddyMood.Cheer else BuddyMood.Idle
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch { repository.deleteHabit(id) }
    }

    fun setBuddyWorking() {
        _buddyMood.value = BuddyMood.Working
    }

    fun refreshAnalytics() {
        viewModelScope.launch {
            _weeklyBars.value = repository.weeklyBarFractions()
            _heatmap.value = repository.heatmapIntensities()
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(repository) as T
        }
    }
}
