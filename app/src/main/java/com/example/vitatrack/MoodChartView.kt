package com.example.vitatrack

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class MoodChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var moodData: Map<String, Int> = mapOf()
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    /** Called from HomeFragment to send mood data */
    fun setMoodData(data: Map<String, Int>) {
        moodData = data
        invalidate() // redraw chart
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (moodData.isEmpty()) {
            textPaint.textSize = 48f
            canvas.drawText("No mood data yet 😶", width / 2f, height / 2f, textPaint)
            return
        }

        val maxVal = max(1, moodData.values.maxOrNull() ?: 1)
        val barWidth = width / (moodData.size * 2f)
        var x = barWidth

        for ((emoji, count) in moodData) {
            val ratio = count.toFloat() / maxVal
            val barHeight = ratio * (height * 0.6f)

            // color gradient based on ratio
            val shader = LinearGradient(
                x, height - barHeight, x, height.toFloat(),
                Color.parseColor("#20DF6C"),
                Color.parseColor("#FFB300"),
                Shader.TileMode.CLAMP
            )
            barPaint.shader = shader

            canvas.drawRoundRect(
                RectF(x, height - barHeight, x + barWidth, height.toFloat()),
                20f, 20f, barPaint
            )

            // draw emoji label
            textPaint.textSize = 36f
            textPaint.color = Color.BLACK
            canvas.drawText(emoji, x + barWidth / 2, height - barHeight - 20, textPaint)

            // draw count
            textPaint.textSize = 28f
            textPaint.color = Color.DKGRAY
            canvas.drawText(count.toString(), x + barWidth / 2f, height.toFloat() - 10f, textPaint)


            x += barWidth * 2
        }

        // title
        textPaint.textSize = 38f
        textPaint.color = Color.DKGRAY
        canvas.drawText("Mood Frequency Chart", width / 2f, 60f, textPaint)
    }
}
