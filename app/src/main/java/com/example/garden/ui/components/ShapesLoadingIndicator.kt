package com.example.garden.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.example.garden.ui.theme.dimens
import kotlin.math.abs

private val ContainedContainerSize = 48.dp
private val ContainedIndicatorSize = 38.dp

/**
 * Бесконечный индикатор: фигура морфится и крутится сама по себе.
 */
@Composable
fun ShapesLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = MaterialTheme.dimens.loadingIndicatorSizeInCarousels,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val morph = rememberShapesMorph()

    val infiniteTransition = rememberInfiniteTransition(label = "m3_expressive_loading")

    val morphProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morph_progress"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(size)) {
        drawShapesMorph(morph, morphProgress, rotation, color)
    }
}

/**
 * Индикатор, привязанный к прогрессу (0..1), например к растяжению pull-to-refresh.
 * Пока прогресс растёт, фигура проходит пятиугольник -> звезда -> пятиугольник
 * и делает один полный оборот. На прогрессе 1 вид совпадает с началом
 * бесконечного [ShapesLoadingIndicator], поэтому переход между ними незаметен.
 */
@Composable
fun ShapesLoadingIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    size: Dp = MaterialTheme.dimens.loadingIndicatorSizeInCarousels,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val morph = rememberShapesMorph()

    Canvas(modifier = modifier.size(size)) {
        val p = progress().coerceIn(0f, 1f)
        val pulse = 1f - abs(2f * p - 1f)
        drawShapesMorph(
            morph = morph,
            morphProgress = FastOutSlowInEasing.transform(pulse),
            rotation = p * 360f,
            color = color
        )
    }
}

/**
 * [ShapesLoadingIndicator] в круглом контейнере (аналог ContainedLoadingIndicator из M3 Expressive).
 */
@Composable
fun ContainedShapesLoadingIndicator(
    modifier: Modifier = Modifier,
    containerSize: Dp = ContainedContainerSize,
    indicatorSize: Dp = ContainedIndicatorSize,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: Dp = 0.dp
) {
    ShapesIndicatorContainer(modifier, containerSize, containerColor, elevation) {
        ShapesLoadingIndicator(size = indicatorSize, color = color)
    }
}

/**
 * Версия [ContainedShapesLoadingIndicator], привязанная к прогрессу (0..1).
 */
@Composable
fun ContainedShapesLoadingIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    containerSize: Dp = ContainedContainerSize,
    indicatorSize: Dp = ContainedIndicatorSize,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    elevation: Dp = 0.dp
) {
    ShapesIndicatorContainer(modifier, containerSize, containerColor, elevation) {
        ShapesLoadingIndicator(progress = progress, size = indicatorSize, color = color)
    }
}

// Не Surface: он перехватывает касания и мешал бы прокрутке под индикатором.
@Composable
private fun ShapesIndicatorContainer(
    modifier: Modifier,
    containerSize: Dp,
    containerColor: Color,
    elevation: Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(containerSize)
            .shadow(elevation, CircleShape)
            .background(containerColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun rememberShapesMorph(): Morph = remember {
    val pentagon = RoundedPolygon(
        numVertices = 5,
        rounding = CornerRounding(0.35f)
    )
    val starShape = RoundedPolygon.star(
        numVerticesPerRadius = 10,
        innerRadius = 0.72f,
        rounding = CornerRounding(0.25f)
    )
    Morph(pentagon, starShape)
}

private fun DrawScope.drawShapesMorph(
    morph: Morph,
    morphProgress: Float,
    rotation: Float,
    color: Color
) {
    val composePath = morph.toPath(progress = morphProgress).asComposePath()
    val half = this.size.minDimension / 2f

    val matrix = Matrix().apply {
        translate(half, half)
        scale(half, half)
    }
    composePath.transform(matrix)

    rotate(rotation) {
        drawPath(
            path = composePath,
            color = color
        )
    }
}
