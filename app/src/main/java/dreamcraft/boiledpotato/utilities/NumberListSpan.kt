package dreamcraft.boiledpotato.utilities

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan

/** utility class for creating custom numbered lists similar to bullet lists */
class NumberListSpan(
    private val gapWidth: Int,
    private val leadWidth: Int,
    private val number: Int
) : LeadingMarginSpan {


    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        // if this line of text is the first line, draw "#)" at x coordinate 0
        // using baseline and specified paint styles
        if (first) c.drawText("$number)", 0F, baseline.toFloat(), p)
    }

    override fun getLeadingMargin(first: Boolean): Int = leadWidth + gapWidth
}