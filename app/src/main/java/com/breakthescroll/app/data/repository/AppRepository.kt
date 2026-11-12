package com.breakthescroll.app.data.repository

import com.breakthescroll.app.data.local.BreakTheScrollDatabase
import com.breakthescroll.app.data.local.entity.ActivityCatalogEntity
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.CommunityPostEntity
import com.breakthescroll.app.data.local.entity.GroupMemberEntity
import com.breakthescroll.app.data.local.entity.HabitCompletionEntity
import com.breakthescroll.app.data.local.entity.HabitEntity
import com.breakthescroll.app.data.local.entity.SquadGroupEntity
import com.breakthescroll.app.data.local.entity.UserPreferenceEntity
import com.breakthescroll.app.data.local.entity.UserStatsEntity
import com.breakthescroll.app.data.seed.ActivityCatalogSeed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max

class AppRepository(private val db: BreakTheScrollDatabase) {
    private val prefs = db.userPreferenceDao()
    private val statsDao = db.userStatsDao()
    private val habitsDao = db.habitDao()
    private val habitCompletionsDao = db.habitCompletionDao()
    private val completionsDao = db.activityCompletionDao()
    private val catalogDao = db.activityCatalogDao()
    private val postsDao = db.communityPostDao()
    private val groupsDao = db.squadGroupDao()
    private val membersDao = db.groupMemberDao()

    suspend fun bootstrap() {
        val existing = prefs.get()
        if (existing == null) {
            prefs.upsert(
                UserPreferenceEntity(
                    darkTheme = true,
                    displayName = "Atharv",
                    username = "atharv",
                ),
            )
        } else if (existing.username.isBlank()) {
            prefs.upsert(
                existing.copy(
                    username = existing.displayName.trim().lowercase().replace(" ", "_")
                        .ifBlank { "atharv" },
                ),
            )
        }
        if (statsDao.get() == null) {
            statsDao.upsert(UserStatsEntity())
        }
        // Upsert catalog every launch so durationSeconds / new activities stay current.
        catalogDao.upsertAll(ActivityCatalogSeed.items)
    }

    fun observePreferences(): Flow<UserPreferenceEntity> =
        prefs.observe().map { it ?: UserPreferenceEntity() }

    fun observeDarkTheme(): Flow<Boolean> =
        prefs.observe().map { it?.darkTheme ?: true }

    fun observeDisplayName(): Flow<String> =
        prefs.observe().map { it?.displayName ?: "Atharv" }

    fun observeUsername(): Flow<String> =
        prefs.observe().map { it?.username?.ifBlank { "atharv" } ?: "atharv" }

    fun observeOnboardingComplete(): Flow<Boolean> =
        prefs.observe().map { it?.onboardingComplete ?: false }

    fun observeInterests(): Flow<Set<String>> =
        prefs.observe().map { pref ->
            pref?.interestsCsv
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun setDarkTheme(darkTheme: Boolean) {
        val current = prefs.get() ?: UserPreferenceEntity()
        prefs.upsert(current.copy(darkTheme = darkTheme))
    }

    suspend fun completeOnboarding(interests: Set<String>) {
        val current = prefs.get() ?: UserPreferenceEntity()
        prefs.upsert(
            current.copy(
                interestsCsv = interests.joinToString(","),
                onboardingComplete = true,
            ),
        )
    }

    suspend fun updateInterests(interests: Set<String>) {
        val current = prefs.get() ?: UserPreferenceEntity()
        prefs.upsert(
            current.copy(
                interestsCsv = interests.joinToString(","),
                onboardingComplete = true,
            ),
        )
    }

    fun observeStats(): Flow<UserStatsEntity> =
        statsDao.observe().map { it ?: UserStatsEntity() }

    fun observeHabits(): Flow<List<HabitEntity>> =
        habitsDao.observeAll().map { list ->
            val today = LocalDate.now().toEpochDay()
            list.map { habit ->
                if (habit.completedToday && habit.lastCompletedEpochDay != today) {
                    habit.copy(completedToday = false)
                } else {
                    habit
                }
            }
        }

    fun observeHabitCompletions(): Flow<List<HabitCompletionEntity>> =
        habitCompletionsDao.observeAll()

    fun observeHabitCompletionsToday(): Flow<List<HabitCompletionEntity>> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        val start = today.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
        return habitCompletionsDao.observeBetween(start, end)
    }

    fun observeHabitCompletionsThisWeek(): Flow<List<HabitCompletionEntity>> {
        val zone = ZoneId.systemDefault()
        val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        val start = monday.atStartOfDay(zone).toInstant().toEpochMilli()
        val end = monday.plusDays(7).atStartOfDay(zone).toInstant().toEpochMilli()
        return habitCompletionsDao.observeBetween(start, end)
    }

    fun observeCatalog(): Flow<List<ActivityCatalogEntity>> = catalogDao.observeAll()

    /** 100% preference-matched activities — no serendipity mixed in. */
    fun observeForYouDeck(): Flow<List<ActivityCatalogEntity>> =
        combine(catalogDao.observeAll(), observeInterests()) { catalog, interests ->
            buildForYouDeck(catalog, interests)
        }

    /** 100% serendipitous wellness — never preference-filtered. */
    fun observeSerendipity(): Flow<List<ActivityCatalogEntity>> =
        catalogDao.observeSerendipity().map { it.shuffled() }

    suspend fun completeCustomActivity(
        activityId: String,
        title: String,
        category: String,
        durationMinutes: Int,
        imageUrl: String = "",
        isQuickRescue: Boolean = false,
    ) {
        completionsDao.insert(
            ActivityCompletionEntity(
                activityId = activityId,
                title = title,
                category = category,
                durationMinutes = durationMinutes,
                imageUrl = imageUrl,
            ),
        )
        bumpStats(durationMinutes.coerceAtLeast(1), incrementQuickRescue = isQuickRescue)
    }

    fun observeRecentCompletions(limit: Int = 40): Flow<List<ActivityCompletionEntity>> =
        completionsDao.observeRecent(limit)

    fun observeAllCompletions(): Flow<List<ActivityCompletionEntity>> =
        completionsDao.observeAll()

    fun observeCommunityPosts(): Flow<List<CommunityPostEntity>> = postsDao.observeAll()

    fun observeAllGroups(): Flow<List<SquadGroupEntity>> = groupsDao.observeAll()

    fun observePublicGroups(): Flow<List<SquadGroupEntity>> = groupsDao.observePublic()

    fun searchPublicGroups(query: String): Flow<List<SquadGroupEntity>> {
        val q = query.trim()
        return if (q.isEmpty()) groupsDao.observePublic() else groupsDao.searchPublic(q)
    }

    fun observeMyGroups(username: String): Flow<List<SquadGroupEntity>> =
        groupsDao.observeJoinedByUsername(username)

    fun observeGroupMembers(groupId: Long): Flow<List<GroupMemberEntity>> =
        membersDao.observeForGroup(groupId)

    suspend fun addHabit(title: String, cadence: String, accentArgb: Int) {
        habitsDao.insert(
            HabitEntity(
                title = title.trim().ifBlank { "New micro-habit" },
                cadence = cadence.trim().ifBlank { "Daily" },
                accentArgb = accentArgb,
            ),
        )
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        val today = LocalDate.now().toEpochDay()
        val zone = ZoneId.systemDefault()
        val day = LocalDate.now()
        val dayStart = day.atStartOfDay(zone).toInstant().toEpochMilli()
        val dayEnd = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        if (!habit.completedToday || habit.lastCompletedEpochDay != today) {
            habitsDao.update(
                habit.copy(
                    completedToday = true,
                    streakDays = habit.streakDays + 1,
                    lastCompletedEpochDay = today,
                ),
            )
            habitCompletionsDao.insert(
                HabitCompletionEntity(
                    habitId = habit.id,
                    habitTitle = habit.title,
                    accentArgb = habit.accentArgb,
                ),
            )
            bumpStats(minutes = 2)
        } else {
            habitsDao.update(
                habit.copy(
                    completedToday = false,
                    streakDays = max(0, habit.streakDays - 1),
                ),
            )
            habitCompletionsDao.deleteForHabitOnDay(habit.id, dayStart, dayEnd)
        }
    }

    suspend fun deleteHabit(id: Long) {
        habitsDao.delete(id)
    }

    suspend fun acceptActivity(item: ActivityCatalogEntity) {
        completionsDao.insert(
            ActivityCompletionEntity(
                activityId = item.id,
                title = item.title,
                category = item.category,
                durationMinutes = item.durationMinutes,
                imageUrl = item.imageUrl,
            ),
        )
        bumpStats(item.durationMinutes)
    }

    suspend fun completeBreathingReset(minutesSaved: Int = 2) {
        completionsDao.insert(
            ActivityCompletionEntity(
                activityId = "breath-reset",
                title = "Instant breathing reset",
                category = "Mindfulness",
                durationMinutes = minutesSaved,
                imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=900&q=80",
            ),
        )
        bumpStats(minutesSaved)
    }

    suspend fun shareCompletionToFeed(
        authorName: String,
        completion: ActivityCompletionEntity,
        note: String,
    ) {
        postsDao.insert(
            CommunityPostEntity(
                authorName = authorName,
                message = note.ifBlank { "Just completed ${completion.title}. Persistence > scroll." },
                activityTitle = completion.title,
                category = completion.category,
                imageUrl = completion.imageUrl,
                cheers = 0,
                likedByMe = false,
                isMine = true,
            ),
        )
    }

    /** Instagram-style like toggle — one like per device/user. */
    suspend fun toggleCheer(id: Long) {
        val post = postsDao.get(id) ?: return
        val updated = if (post.likedByMe) {
            post.copy(cheers = max(0, post.cheers - 1), likedByMe = false)
        } else {
            post.copy(cheers = post.cheers + 1, likedByMe = true)
        }
        postsDao.update(updated)
    }

    suspend fun createGroup(
        name: String,
        category: String,
        description: String,
        isPublic: Boolean,
        ownerUsername: String,
        memberUsernames: List<String>,
    ): Long {
        val cleanName = name.trim().ifBlank { "Untitled squad" }
        val groupId = groupsDao.insert(
            SquadGroupEntity(
                name = cleanName,
                category = category.trim().ifBlank { "General" },
                description = description.trim(),
                isPublic = isPublic,
                ownerUsername = ownerUsername,
                memberCount = 1,
            ),
        )
        membersDao.insert(
            GroupMemberEntity(groupId = groupId, username = ownerUsername),
        )
        val uniqueMembers = memberUsernames
            .map { it.trim().removePrefix("@") }
            .filter { it.isNotBlank() && !it.equals(ownerUsername, ignoreCase = true) }
            .distinctBy { it.lowercase() }
        uniqueMembers.forEach { username ->
            membersDao.insert(GroupMemberEntity(groupId = groupId, username = username))
        }
        refreshMemberCount(groupId)
        return groupId
    }

    suspend fun addMemberByUsername(groupId: Long, username: String): Boolean {
        val clean = username.trim().removePrefix("@")
        if (clean.isBlank()) return false
        if (membersDao.isMember(groupId, clean) > 0) return false
        membersDao.insert(GroupMemberEntity(groupId = groupId, username = clean))
        refreshMemberCount(groupId)
        return true
    }

    suspend fun joinPublicGroup(groupId: Long, username: String): Boolean {
        if (membersDao.isMember(groupId, username) > 0) return false
        membersDao.insert(GroupMemberEntity(groupId = groupId, username = username))
        refreshMemberCount(groupId)
        return true
    }

    suspend fun isGroupMember(groupId: Long, username: String): Boolean =
        membersDao.isMember(groupId, username) > 0

    private suspend fun refreshMemberCount(groupId: Long) {
        val group = groupsDao.get(groupId) ?: return
        val count = membersDao.listForGroup(groupId).size
        groupsDao.update(group.copy(memberCount = count))
    }

    private suspend fun bumpStats(minutes: Int, incrementQuickRescue: Boolean = false) {
        val current = statsDao.get() ?: UserStatsEntity()
        val today = LocalDate.now().toEpochDay()
        val yesterday = today - 1
        val newStreak = when (current.lastCompletionEpochDay) {
            null -> 1
            today -> current.currentStreak.coerceAtLeast(1)
            yesterday -> current.currentStreak + 1
            else -> 1
        }
        statsDao.upsert(
            current.copy(
                currentStreak = newStreak,
                longestStreak = max(current.longestStreak, newStreak),
                lastCompletionEpochDay = today,
                screenTimeSavedMinutes = current.screenTimeSavedMinutes + minutes,
                tasksCompleted = current.tasksCompleted + 1,
                quickRescues = current.quickRescues + if (incrementQuickRescue) 1 else 0,
            ),
        )
    }

    suspend fun weeklyBarFractions(): List<Float> {
        val zone = ZoneId.systemDefault()
        val monday = LocalDate.now().with(DayOfWeek.MONDAY)
        return (0..6).map { offset ->
            val day = monday.plusDays(offset.toLong())
            val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val minutes = completionsDao.minutesBetween(start, end)
            if (minutes <= 0) 0.08f else (minutes / 40f).coerceIn(0.15f, 1f)
        }
    }

    suspend fun heatmapIntensities(days: Int = 35): List<Float> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        return (days - 1 downTo 0).map { back ->
            val day = today.minusDays(back.toLong())
            val start = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            when (completionsDao.countBetween(start, end)) {
                0 -> 0.12f
                1 -> 0.35f
                2 -> 0.55f
                3 -> 0.75f
                else -> 0.95f
            }
        }
    }

    companion object {
        fun buildForYouDeck(
            catalog: List<ActivityCatalogEntity>,
            interests: Set<String>,
        ): List<ActivityCatalogEntity> {
            if (catalog.isEmpty()) return emptyList()
            val matched = if (interests.isEmpty()) {
                catalog.filter { it.isPersonalized }
            } else {
                catalog.filter { it.interestTag in interests }
            }
            return matched.shuffled().ifEmpty {
                catalog.filter { it.isPersonalized }.shuffled()
            }
        }
    }
}
