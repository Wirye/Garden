package com.example.garden.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.example.garden.ui.theme.dimens

@Composable
fun ShapesLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = MaterialTheme.dimens.loadingIndicatorSizeInCarousels,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val pentagon = remember {
        RoundedPolygon(
            numVertices = 5,
            rounding = CornerRounding(0.35f)
        )
    }

    val starShape = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 10,
            innerRadius = 0.72f,
            rounding = CornerRounding(0.25f)
        )
    }

    val morph = remember { Morph(pentagon, starShape) }

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
        val composePath = morph.toPath(progress = morphProgress).asComposePath()

        val matrix = Matrix().apply {
            translate(size.toPx() / 2f, size.toPx() / 2f)
            scale(size.toPx() / 2f, size.toPx() / 2f)
        }
        composePath.transform(matrix)

        rotate(rotation) {
            drawPath(
                path = composePath,
                color = color
            )
        }
    }
}