package com.breakthescroll.app.ui.components.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.breakthescroll.app.R
import com.breakthescroll.app.ui.theme.DeepSlate
import com.breakthescroll.app.ui.theme.Obsidian
import com.breakthescroll.app.ui.theme.SereneMint
import com.breakthescroll.app.ui.theme.SoftFrost
import com.breakthescroll.app.ui.theme.SoftFrostMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartResetSheet(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val breath = rememberInfiniteTransition(label = "breath")
    val scale by breath.animateFloat(
        initialValue = 0.86f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathScale",
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Obsidian,
        contentColor = SoftFrost,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.start_reset_title),
                style = MaterialTheme.typography.headlineMedium,
                color = SoftFrost,
            )
            Text(
                text = stringResource(R.string.start_reset_body),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftFrostMuted,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .background(SereneMint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(SereneMint.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Breathe",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftFrost,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inhale · Hold · Exhale · Hold",
                style = MaterialTheme.typography.labelLarge,
                color = SereneMint,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SereneMint,
                    contentColor = DeepSlate,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Complete reset")
            }
            TextButton(onClick = onDismiss) {
                Text("Not now", color = SoftFrostMuted)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
