package com.breakthescroll.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.breakthescroll.app.R
import com.breakthescroll.app.data.local.entity.ActivityCompletionEntity
import com.breakthescroll.app.data.local.entity.CommunityPostEntity
import com.breakthescroll.app.data.local.entity.SquadGroupEntity
import com.breakthescroll.app.data.seed.InterestTags
import com.breakthescroll.app.ui.components.buddy.ScrollBuddyBanner
import com.breakthescroll.app.ui.model.BuddyMood
import com.breakthescroll.app.ui.theme.LocalBtsPalette
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GroupsScreen(
    posts: List<CommunityPostEntity>,
    myGroups: List<SquadGroupEntity>,
    discoverGroups: List<SquadGroupEntity>,
    searchQuery: String,
    username: String,
    recentCompletions: List<ActivityCompletionEntity>,
    buddyMood: BuddyMood,
    buddyMessage: String,
    onSearchChange: (String) -> Unit,
    onCheer: (Long) -> Unit,
    onShareCompletion: (ActivityCompletionEntity, String) -> Unit,
    onCreateGroup: (name: String, category: String, description: String, isPublic: Boolean, members: List<String>) -> Unit,
    onAddMember: (groupId: Long, username: String) -> Unit,
    onJoinGroup: (groupId: Long) -> Unit,
    onOpenPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalBtsPalette.current
    var tab by remember { mutableIntStateOf(0) }
    var showCreate by remember { mutableStateOf(false) }
    var showShare by remember { mutableStateOf(false) }
    var shareNote by remember { mutableStateOf("") }
    var addMemberFor by remember { mutableStateOf<SquadGroupEntity?>(null) }
    var memberInput by remember { mutableStateOf("") }
    val latest = recentCompletions.firstOrNull()
    val formatter = remember {
        DateTimeFormatter.ofPattern("MMM d · HH:mm").withZone(ZoneId.systemDefault())
    }
    val tabs = listOf("Feed", "My groups", "Discover")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.groups_title),
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.onBackground,
                    )
                    Text(
                        text = "@$username · squads & wins",
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onSurfaceMuted,
                    )
                }
                IconButton(onClick = onOpenPreferences) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Preferences", tint = palette.primary)
                }
            }

            TabRow(
                selectedTabIndex = tab,
                containerColor = palette.background,
                contentColor = palette.primary,
                indicator = { positions ->
                    if (tab < positions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(positions[tab]),
                            color = palette.primary,
                        )
                    }
                },
                divider = {},
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tab == index,
                        onClick = { tab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (tab == index) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (tab == index) palette.primary else palette.onSurfaceMuted,
                            )
                        },
                    )
                }
            }

            when (tab) {
                0 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Column {
                            Spacer(Modifier.height(8.dp))
                            ScrollBuddyBanner(mood = buddyMood, customMessage = buddyMessage)
                        }
                    }
                    item {
                        ShareStrip(
                            latestTitle = latest?.title,
                            onShare = { if (latest != null) showShare = true },
                        )
                    }
                    if (posts.isEmpty()) {
                        item {
                            EmptyHint("No posts yet — complete an Active Break, then share it here.")
                        }
                    } else {
                        items(posts, key = { it.id }) { post ->
                            CommunityPostCard(
                                post = post,
                                timeLabel = formatter.format(Instant.ofEpochMilli(post.createdAt)),
                                onCheer = { onCheer(post.id) },
                            )
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }

                1 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { Spacer(Modifier.height(8.dp)) }
                    if (myGroups.isEmpty()) {
                        item { EmptyHint("You’re not in any groups yet. Create one or Discover public squads.") }
                    } else {
                        items(myGroups, key = { it.id }) { group ->
                            GroupCard(
                                group = group,
                                joined = true,
                                onPrimary = { addMemberFor = group },
                                primaryLabel = "Add member",
                            )
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Outlined.Search, contentDescription = null, tint = palette.onSurfaceMuted)
                            },
                            placeholder = { Text("Search public groups") },
                            shape = RoundedCornerShape(16.dp),
                            colors = fieldColors(),
                        )
                    }
                    if (discoverGroups.isEmpty()) {
                        item { EmptyHint("No public groups match. Create the first one.") }
                    } else {
                        items(discoverGroups, key = { it.id }) { group ->
                            val joined = myGroups.any { it.id == group.id }
                            GroupCard(
                                group = group,
                                joined = joined,
                                onPrimary = { if (!joined) onJoinGroup(group.id) },
                                primaryLabel = if (joined) "Joined" else "Join",
                            )
                        }
                    }
                    item { Spacer(Modifier.height(100.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreate = true },
            containerColor = palette.primary,
            contentColor = palette.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 96.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Create group")
        }
    }

    if (showCreate) {
        CreateGroupDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, category, description, isPublic, members ->
                onCreateGroup(name, category, description, isPublic, members)
                showCreate = false
                tab = 1
            },
        )
    }

    if (showShare && latest != null) {
        AlertDialog(
            onDismissRequest = { showShare = false },
            title = { Text("Post to feed") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Sharing: ${latest.title}", color = palette.onSurfaceMuted)
                    OutlinedTextField(
                        value = shareNote,
                        onValueChange = { shareNote = it },
                        label = { Text("Caption") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onShareCompletion(latest, shareNote)
                        shareNote = ""
                        showShare = false
                        tab = 0
                    },
                ) { Text("Post") }
            },
            dismissButton = {
                TextButton(onClick = { showShare = false }) { Text("Cancel") }
            },
        )
    }

    addMemberFor?.let { group ->
        AlertDialog(
            onDismissRequest = { addMemberFor = null },
            title = { Text("Add member") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${group.name} · invite by username", color = palette.onSurfaceMuted)
                    OutlinedTextField(
                        value = memberInput,
                        onValueChange = { memberInput = it },
                        label = { Text("@username") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Outlined.GroupAdd, contentDescription = null)
                        },
                        colors = fieldColors(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAddMember(group.id, memberInput)
                        memberInput = ""
                        addMemberFor = null
                    },
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { addMemberFor = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocalBtsPalette.current.primary,
    unfocusedBorderColor = LocalBtsPalette.current.border,
    focusedContainerColor = LocalBtsPalette.current.surface,
    unfocusedContainerColor = LocalBtsPalette.current.surface,
)

@Composable
private fun EmptyHint(text: String) {
    val palette = LocalBtsPalette.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .padding(18.dp),
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceMuted)
    }
}

@Composable
private fun ShareStrip(latestTitle: String?, onShare: () -> Unit) {
    val palette = LocalBtsPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Share a milestone", style = MaterialTheme.typography.titleMedium, color = palette.onBackground)
            Text(
                latestTitle?.let { "Latest: $it" } ?: "Complete an Active Break first",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.onSurfaceMuted,
            )
        }
        IconButton(onClick = onShare, enabled = latestTitle != null) {
            Icon(
                Icons.Outlined.Share,
                contentDescription = "Share",
                tint = if (latestTitle != null) palette.primary else palette.onSurfaceMuted,
            )
        }
    }
}

@Composable
private fun GroupCard(
    group: SquadGroupEntity,
    joined: Boolean,
    onPrimary: () -> Unit,
    primaryLabel: String,
) {
    val palette = LocalBtsPalette.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(group.name, style = MaterialTheme.typography.titleLarge, color = palette.onBackground)
                Text(
                    "${group.category} · ${group.memberCount} members · ${if (group.isPublic) "Public" else "Private"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.onSurfaceMuted,
                )
            }
            Button(
                onClick = onPrimary,
                enabled = !joined || primaryLabel == "Add member",
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primary,
                    contentColor = palette.onPrimary,
                    disabledContainerColor = palette.primaryContainer,
                    disabledContentColor = palette.onSurfaceMuted,
                ),
            ) {
                Text(primaryLabel)
            }
        }
        if (group.description.isNotBlank()) {
            Text(group.description, style = MaterialTheme.typography.bodyMedium, color = palette.onSurfaceMuted)
        }
        Text(
            text = "Owner @${group.ownerUsername}",
            style = MaterialTheme.typography.labelLarge,
            color = palette.highlight,
        )
    }
}

@Composable
private fun CommunityPostCard(
    post: CommunityPostEntity,
    timeLabel: String,
    onCheer: () -> Unit,
) {
    val palette = LocalBtsPalette.current
    val heartColor by animateColorAsState(
        targetValue = if (post.likedByMe) palette.primary else palette.onSurfaceMuted,
        label = "heart",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(palette.surface)
            .border(1.dp, palette.border, RoundedCornerShape(20.dp)),
    ) {
        if (post.imageUrl.isNotBlank()) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = post.activityTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp),
            )
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(palette.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = post.authorName.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.primary,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName + if (post.isMine) " · you" else "",
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.onBackground,
                    )
                    Text(
                        text = "${post.category} · $timeLabel",
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.onSurfaceMuted,
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(post.message, style = MaterialTheme.typography.bodyLarge, color = palette.onBackground)
            Spacer(Modifier.height(4.dp))
            Text("Completed · ${post.activityTitle}", style = MaterialTheme.typography.labelLarge, color = palette.highlight)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onCheer)
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = if (post.likedByMe) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (post.likedByMe) "Unlike" else "Like",
                    tint = heartColor,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "${post.cheers}",
                    style = MaterialTheme.typography.labelLarge,
                    color = heartColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, category: String, description: String, isPublic: Boolean, members: List<String>) -> Unit,
) {
    val palette = LocalBtsPalette.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(InterestTags.all.first()) }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }
    var membersCsv by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create group") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Group name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                )
                Text("Category", style = MaterialTheme.typography.labelLarge, color = palette.onSurfaceMuted)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InterestTags.all.forEach { tag ->
                        FilterChip(
                            selected = category == tag,
                            onClick = { category = tag },
                            label = { Text(tag) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = palette.primaryContainer,
                                selectedLabelColor = palette.primary,
                            ),
                        )
                    }
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                )
                OutlinedTextField(
                    value = membersCsv,
                    onValueChange = { membersCsv = it },
                    label = { Text("Add members (@user1, @user2)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Public (discoverable)", color = palette.onBackground)
                    Switch(checked = isPublic, onCheckedChange = { isPublic = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val members = membersCsv
                        .split(',', ' ')
                        .map { it.trim().removePrefix("@") }
                        .filter { it.isNotBlank() }
                    onCreate(name, category, description, isPublic, members)
                },
                enabled = name.isNotBlank(),
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
