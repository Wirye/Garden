package com.example.garden.ui.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.example.garden.R
import kotlin.math.round

data class OptimizedTextResult(
    val firstLine: String,
    val secondLine: String,  // пустая строка если нет второй строки
    val totalHeight: Int      // высота в пикселях
)
fun optimizeText(text: String, maxWidthPx: Int, textSizePx: Float, includeFontPadding: Boolean, typeface: Typeface? = null, maxLines: Int = 2 ): OptimizedTextResult {
    val paint = Paint().apply {
        this.textSize = textSizePx
        // Устанавливаем шрифт если указан
        this.typeface = typeface
    }
    // Получаем высоту одной строки
    var lineHeight = 0
    if (includeFontPadding == true) {
        lineHeight = (paint.fontMetrics.bottom - paint.fontMetrics.top).toInt()
    } else {
        lineHeight = (paint.fontMetrics.descent - paint.fontMetrics.ascent).toInt()
    }

    // Разбиваем на слова
    val words = text.split(" ").filter { it.isNotEmpty() }
    if (words.isEmpty()) {
        return OptimizedTextResult("", "", 0)
    }
    // Пробуем разместить в одну строку
    val oneLineWidth = paint.measureText(text)
    if (oneLineWidth <= maxWidthPx) {
        return OptimizedTextResult(text, "", lineHeight)
    }

    if (maxLines == 1) {
        val ellipsized = ellipsizeText(paint, text, maxWidthPx)
        return OptimizedTextResult(ellipsized, "", lineHeight)
    }
    // Нужно разбивать на 2 строки


    var firstString = ""
    var secondString = ""
    var potentialSecondString = ""
    // Чекаем ширину первого слова (если даже 1 слово не помещается в 1 строку)
    if (paint.measureText(words[0]) > maxWidthPx) {
        var k = 0
        while (paint.measureText(firstString) < maxWidthPx) {
            firstString += words[0][k]
            if (k < words[0].length) {
                k += 1
            }
            else {
                break
            }
        }
        if (paint.measureText(firstString) > maxWidthPx) {
            firstString = firstString.removeRange(firstString.lastIndex..firstString.lastIndex)
        }
        if (firstString.isNotEmpty()) {
            firstString = firstString.removeRange(firstString.lastIndex..firstString.lastIndex)
            firstString += "-"
            for (i in firstString.lastIndex until words[0].length) {
                potentialSecondString += words[0][i]
            }
            potentialSecondString += " "
            for (i in 1 until words.size) {
                potentialSecondString += "${words[i]} "
            }
            while (potentialSecondString[potentialSecondString.lastIndex].toString() == " ") {
                potentialSecondString = potentialSecondString.removeRange(potentialSecondString.lastIndex..potentialSecondString.lastIndex)
            }
        }
    }
    // Если в 1 строку влазит хотя бы 1 слово
    else {
        for (i in 0 until words.size) {
            val word = words[i]
            if (paint.measureText(firstString+word) <= maxWidthPx) {
                firstString = firstString+word
                if (paint.measureText("$firstString ") <= maxWidthPx) {
                    firstString = firstString+" "
                } else if (paint.measureText(firstString) >= maxWidthPx){
                    for (o in i until words.size) {
                        val word1 = words[o]
                        potentialSecondString += "$word1 "
                    }
                    potentialSecondString = potentialSecondString.trimEnd()
                    break
                }
            }
            else {
                for (o in i until words.size) {
                    val word1 = words[o]
                    potentialSecondString += "$word1 "
                }
                potentialSecondString = potentialSecondString.trimEnd()
                break
            }
        }
    }
    val potentialSecondStringWords = potentialSecondString.split(" ").filter { it.isNotEmpty() }
    val secondStringInList = mutableListOf<String>()
    for (i in 0 until potentialSecondStringWords.size) {
        val word = potentialSecondStringWords[i]
        var kleiq = ""
        for (o in 0 until secondStringInList.size) {
            kleiq += "${secondStringInList[o]} "
        }
        if (paint.measureText(kleiq + word) <= maxWidthPx) {
            secondStringInList.add(word)
            kleiq = ""
            for (o in 0 until secondStringInList.size) {
                kleiq += "${secondStringInList[o]} "
            }
            secondString = kleiq
        }

        else {
            if (secondStringInList.isEmpty()) {
                secondStringInList.add(potentialSecondStringWords[0])
            }
            var klei = ""
            for (o in 0 until secondStringInList.size) {
                klei += "${secondStringInList[o]} "
            }
            klei = klei.removeRange(klei.lastIndex..klei.lastIndex)
            while (paint.measureText("$klei...") > maxWidthPx) {
                if (secondStringInList.size > 1) {
                    secondStringInList.removeAt(secondStringInList.lastIndex)
                    klei = ""
                    for (o in 0 until secondStringInList.size) {
                        klei += "${secondStringInList[o]} "
                    }
                    klei = klei.removeRange(klei.lastIndex..klei.lastIndex)
                }
                else if (secondStringInList.size == 1) {
                    while (paint.measureText("$klei...") > maxWidthPx) {
                        if (klei.isNotEmpty()) {
                            klei = klei.removeRange(klei.lastIndex..klei.lastIndex)
                        }
                    }
                    break
                }
                else {
                    break
                }
            }
            secondString = "$klei..."
            break
        }

    }
    return OptimizedTextResult(firstString.trimEnd(), secondString.trimEnd(), lineHeight)
}

private fun ellipsizeText(paint: Paint, text: String, maxWidth: Int): String {
    if (paint.measureText(text) <= maxWidth) return text
    var result = text
    while (paint.measureText(result + "...") > maxWidth && result.isNotEmpty()) {
        result = result.dropLast(1)
    }
    return result + "..."
}


/**
 * Вычисляет параметры для отображения цифры в квадрате
 * @param squareSize Размер стороны квадрата в пикселях
 * @param digit Цифра для отображения
 * @param context Контекст для доступа к ресурсам
 * @return Triple(textSize, paddingLeft, paddingTop) - размер шрифта и отступы для точного центрирования
 */
fun calculateDigitParams(squareSize: Int, digit: Char, context: Context): Triple<Float, Int, Int> {
    // Желаемая высота цифры = 3/4 от высоты квадрата
    val desiredHeight = squareSize * 0.75f

    // Создаем Paint для измерения
    val paint = Paint().apply {
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.google_sans_medium)
        textAlign = Paint.Align.LEFT
    }

    // Бинарный поиск оптимального размера шрифта
    // Максимальный размер - высота квадрата (чтобы точно не вылезло)
    var low = 1f
    var high = squareSize.toFloat()  // Ограничиваем максимальным размером квадрата
    var bestSize = desiredHeight

    repeat(20) {
        val mid = (low + high) / 2f
        paint.textSize = mid

        val fontMetrics = paint.fontMetrics
        val textHeight = fontMetrics.descent - fontMetrics.ascent

        when {
            textHeight > desiredHeight -> high = mid
            textHeight < desiredHeight -> low = mid
            else -> {
                bestSize = mid
                low = mid
                high = mid
            }
        }
    }

    bestSize = low
    paint.textSize = bestSize

    // Измеряем ширину текста
    val textWidth = paint.measureText(digit.toString())

    // Вычисляем горизонтальные отступы для центрирования
    val totalHorizontalPadding = squareSize - textWidth
    val paddingLeft = (totalHorizontalPadding / 2f).coerceAtLeast(0f)

    // Вычисляем вертикальные отступы для центрирования
    val fontMetrics = paint.fontMetrics

    // Высота текста для проверки
    val textHeight = fontMetrics.descent - fontMetrics.ascent

    // Центр текста относительно baseline
    val textCenter = (fontMetrics.ascent + fontMetrics.descent) / 2f

    // Позиция baseline, чтобы центр текста оказался в центре квадрата
    val baselinePosition = squareSize / 2f - textCenter

    // Преобразуем baseline в paddingTop
    // baselinePosition - это расстояние от верха квадрата до baseline
    // Нам нужно paddingTop, который будет отступом до baseline
    var paddingTop = baselinePosition.toInt().coerceAtLeast(0)

    paddingTop = round((squareSize - paddingTop).toFloat() / 2f).toInt()

    return Triple(bestSize, paddingLeft.toInt(), paddingTop)
}