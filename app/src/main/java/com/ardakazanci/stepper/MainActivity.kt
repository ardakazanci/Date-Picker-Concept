package com.ardakazanci.stepper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ardakazanci.stepper.ui.theme.StepperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepperTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ElasticSlidingDatePicker(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ElasticSlidingDatePicker(modifier: Modifier = Modifier) {
    val range = (1..31).toList()
    var selectedIndex by remember { mutableIntStateOf(15) }
    val scaleAnim = remember { Animatable(1.05f) }

    LaunchedEffect(selectedIndex) {
        scaleAnim.snapTo(1f)
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessVeryLow
            )
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        DatePickerCard(
            scale = scaleAnim.value,
            selectedIndex = selectedIndex,
            range = range,
            onIndexChange = { newIndex -> selectedIndex = newIndex }
        )
    }
}

@Composable
fun DatePickerCard(
    scale: Float,
    selectedIndex: Int,
    range: List<Int>,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val blurAnim = remember { Animatable(16.dp, Dp.VectorConverter) }
    LaunchedEffect(selectedIndex) {
        blurAnim.snapTo(16.dp)
        blurAnim.animateTo(
            targetValue = 0.dp,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .width(240.dp)
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Crossfade(
            targetState = selectedIndex,
            animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
        ) { target ->
            DatePickerContent(
                target = target,
                range = range,
                onIndexChange = onIndexChange,
                modifier = Modifier.blur(blurAnim.value)
            )
        }
    }
}

@Composable
fun DatePickerContent(
    target: Int,
    range: List<Int>,
    onIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val left = range.getOrNull(target - 1)
    val center = range[target]
    val right = range.getOrNull(target + 1)

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DateText(
            text = left?.toString().orEmpty(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            alpha = 0.4f,
            onClick = { if (target > 0) onIndexChange(target - 1) }
        )
        DateText(
            text = center.toString(),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            alpha = 1f,
            onClick = {
                // not for used now
            }
        )
        DateText(
            text = right?.toString().orEmpty(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            alpha = 0.4f,
            onClick = { if (target < range.lastIndex) onIndexChange(target + 1) }
        )
    }
}

@Composable
fun DateText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    color: Color,
    alpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier
            .alpha(alpha)
            .clickable(enabled = text.isNotEmpty(), onClick = onClick)
    )
}





