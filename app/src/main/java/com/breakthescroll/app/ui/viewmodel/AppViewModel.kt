package com.breakthescroll.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.CommunityPostEntity
import com.breakthescroll.app.data.local.entity.HabitCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.SquadGroupEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.data.repository.AppRepository
import com.breakthescroll.app.ui.model.ActivityChallenge
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.model.BuddyScreenContext
import com.breakthescroll.app.ui.model.FeedMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressUiState(
    val stats: UserStatsEntity = UserStatsEntity(),
    val weeklyBars: List<Float> = List(7) { 0.08f },
    val heatmap: List<Float> = List(35) { 0.12f },
    val recent: List<ActivityCompletionEntity> = emptyList(),
    val habitsToday: List<HabitCompletionEntity> = emptyList(),
    val habitsWeek: List<HabitCompletionEntity> = emptyList(),
)

class AppViewModel(private val repository: AppRepository) : ViewModel() {
    val darkTheme: StateFlow<Boolean> = repository.observeDarkTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val displayName: StateFlow<String> = repository.observeDisplayName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Atharv")

    val username: StateFlow<String> = repository.observeUsername()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "atharv")

    val onboardingComplete: StateFlow<Boolean> = repository.observeOnboardingComplete()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val interests: StateFlow<Set<String>> = repository.observeInterests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val stats: StateFlow<UserStatsEntity> = repository.observeStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserStatsEntity())

    val habits: StateFlow<List<HabitEntity>> = repository.observeHabits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val hybridDeck: StateFlow<List<ActivityCatalogEntity>> = repository.observeHybridDeck()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serendipity: StateFlow<List<ActivityCatalogEntity>> = repository.observeSerendipity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val communityPosts: StateFlow<List<CommunityPostEntity>> = repository.observeCommunityPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _groupSearch = MutableStateFlow("")
    val groupSearch: StateFlow<String> = _groupSearch.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val discoverGroups: StateFlow<List<SquadGroupEntity>> = _groupSearch
        .flatMapLatest { repository.searchPublicGroups(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val myGroups: StateFlow<List<SquadGroupEntity>> = username
        .flatMapLatest { repository.observeMyGroups(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _feedMode = MutableStateFlow(FeedMode.Hybrid)
    val feedMode: StateFlow<FeedMode> = _feedMode.asStateFlow()

    private val _buddyMood = MutableStateFlow(BuddyMood.Idle)
    val buddyMood: StateFlow<BuddyMood> = _buddyMood.asStateFlow()

    private val _pendingEngagement = MutableStateFlow<ActivityChallenge?>(null)
    val pendingEngagement: StateFlow<ActivityChallenge?> = _pendingEngagement.asStateFlow()

    private val _completedChallengeId = MutableStateFlow<String?>(null)
    val completedChallengeId: StateFlow<String?> = _completedChallengeId.asStateFlow()

    private val _showPreferences = MutableStateFlow(false)
    val showPreferences: StateFlow<Boolean> = _showPreferences.asStateFlow()

    private val _weeklyBars = MutableStateFlow(List(7) { 0.08f })
    private val _heatmap = MutableStateFlow(List(35) { 0.12f })

    private data class ProgressCore(
        val stats: UserStatsEntity,
        val recent: List<ActivityCompletionEntity>,
        val habitsToday: List<HabitCompletionEntity>,
        val habitsWeek: List<HabitCompletionEntity>,
    )

    val progress: StateFlow<ProgressUiState> = combine(
        combine(
            repository.observeStats(),
            repository.observeAllCompletions(),
            repository.observeHabitCompletionsToday(),
            repository.observeHabitCompletionsThisWeek(),
        ) { stats, recent, habitsToday, habitsWeek ->
            ProgressCore(stats, recent, habitsToday, habitsWeek)
        },
        _weeklyBars,
        _heatmap,
    ) { core, bars, heat ->
        ProgressUiState(
            stats = core.stats,
            weeklyBars = bars,
            heatmap = heat,
            recent = core.recent,
            habitsToday = core.habitsToday,
            habitsWeek = core.habitsWeek,
        )
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

    fun openPreferences() {
        _showPreferences.value = true
    }

    fun dismissPreferences() {
        _showPreferences.value = false
    }

    fun completeOnboarding(selected: Set<String>) {
        viewModelScope.launch {
            repository.completeOnboarding(selected)
            _showPreferences.value = false
            _buddyMood.value = BuddyMood.Cheer
        }
    }

    fun updateInterests(selected: Set<String>) {
        viewModelScope.launch {
            repository.updateInterests(selected)
            _showPreferences.value = false
            _buddyMood.value = BuddyMood.Idle
        }
    }

    fun requestEngagement(challenge: ActivityChallenge) {
        if (_pendingEngagement.value != null) return
        _completedChallengeId.value = null
        _pendingEngagement.value = challenge
        _buddyMood.value = BuddyMood.Working
    }

    fun cancelEngagement() {
        _pendingEngagement.value = null
        _buddyMood.value = BuddyMood.Nudge
    }

    fun confirmEngagement() {
        val challenge = _pendingEngagement.value ?: return
        viewModelScope.launch {
            runCatching { repository.acceptActivity(challenge.source) }
            _completedChallengeId.value = challenge.id
            _pendingEngagement.value = null
            _buddyMood.value = BuddyMood.Cheer
            refreshAnalytics()
        }
    }

    fun consumeCompletedChallengeId() {
        _completedChallengeId.value = null
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
            refreshAnalytics()
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch { repository.deleteHabit(id) }
    }

    fun toggleCheer(id: Long) {
        viewModelScope.launch { repository.toggleCheer(id) }
    }

    fun shareCompletion(completion: ActivityCompletionEntity, note: String) {
        viewModelScope.launch {
            repository.shareCompletionToFeed(displayName.value, completion, note)
            _buddyMood.value = BuddyMood.Cheer
        }
    }

    fun setGroupSearch(query: String) {
        _groupSearch.value = query
    }

    fun createGroup(
        name: String,
        category: String,
        description: String,
        isPublic: Boolean,
        memberUsernames: List<String>,
    ) {
        viewModelScope.launch {
            repository.createGroup(
                name = name,
                category = category,
                description = description,
                isPublic = isPublic,
                ownerUsername = username.value,
                memberUsernames = memberUsernames,
            )
            _buddyMood.value = BuddyMood.Cheer
        }
    }

    fun addMember(groupId: Long, memberUsername: String) {
        viewModelScope.launch {
            repository.addMemberByUsername(groupId, memberUsername)
        }
    }

    fun joinGroup(groupId: Long) {
        viewModelScope.launch {
            repository.joinPublicGroup(groupId, username.value)
            _buddyMood.value = BuddyMood.Cheer
        }
    }

    fun buddyMessage(context: BuddyScreenContext): String {
        val streak = stats.value.currentStreak
        val tasks = stats.value.tasksCompleted
        return when (context) {
            BuddyScreenContext.Activities -> when {
                streak >= 5 -> "🔥 Day $streak. The feed misses you. Keep starving it."
                tasks == 0 -> "Swipe right starts a real timer — cheap taps don't count, chief."
                else -> "Scrolling won't fix your code. Pick a card and earn the win."
            }
            BuddyScreenContext.Habits -> when {
                habits.value.any { it.completedToday } -> "Habit logged. Quiet flex > public reel."
                else -> "One checkbox beats thirty minutes of 'just checking'."
            }
            BuddyScreenContext.Progress -> when {
                streak >= 3 -> "🔥 Streak $streak — receipts don't lie. Neither do I."
                progress.value.recent.isEmpty() && progress.value.habitsToday.isEmpty() ->
                    "Empty log. Tragic. Finish a timer and make history jealous."
                else -> "Your Room history is the only timeline that matters."
            }
            BuddyScreenContext.Groups -> when {
                myGroups.value.isNotEmpty() -> "Squad's watching. Post a win, not a doomscroll diary."
                else -> "Build a real group. Fake squads are just another feed."
            }
        }
    }

    fun refreshAnalytics() {
        viewModelScope.launch {
            runCatching {
                _weeklyBars.value = repository.weeklyBarFractions()
                _heatmap.value = repository.heatmapIntensities()
            }
        }
    }

    class Factory(private val repository: AppRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(repository) as T
        }
    }
}
