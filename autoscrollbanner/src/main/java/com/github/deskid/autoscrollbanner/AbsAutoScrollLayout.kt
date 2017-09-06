package com.github.deskid.autoscrollbanner

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.github.deskid.autoscrollbanner.utils.childViews

/**
 * todo abstract a adapter
 * getContainerView()
 * getIndicatorView()
 * getIndicatorContainerView()
 * getBannerCount()
 * setSection()
 */
abstract class AbsAutoScrollLayout<T : View>
@JvmOverloads
constructor(context: Context,
            attributeSet: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : RelativeLayout(context, attributeSet, defStyleAttr) {

    protected lateinit var container: T
    private var indicatorContainer: LinearLayout? = null

    private var showIndicator: Boolean = false
    private var indicatorDrawable: Drawable?
    private var indicatorBackground: Drawable?
    private var indicatorPadding: Int = 8
    private var autoStart: Boolean = true
    private var infinite: Boolean = true

    @IndicatorGravity
    private var indicatorGravity: Int = 0

    private var isStop: Boolean = true
    private var scrollRunnable = ScrollRunnable()
    private var delayMillis: Long = 2000L
    private var currentPosition = 0

    inner class ScrollRunnable : Runnable {
        override fun run() {
            if (!isStop) {
                toNext()
                timer.postDelayed(this, delayMillis)
            }
        }
    }

    private fun toNext() {
        val nextPosition = if (currentPosition >= getTotalCount()) 0 else currentPosition + 1
        setSelection(nextPosition)
    }

    @IntDef(LEFT.toLong(), CENTER.toLong(), RIGHT.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class IndicatorGravity

    companion object {
        const val LEFT = 1
        const val CENTER = 2
        const val RIGHT = 3

        var timer: Handler = Handler(Looper.getMainLooper())
    }

    /**
     * todo
     * add a lot getter and setter for attributes
     */

    init {
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.AutoScrollLayout)
        showIndicator = a.getBoolean(R.styleable.AutoScrollLayout_autoscroll_show_indicator, true)
        indicatorDrawable = a.getDrawable(R.styleable.AutoScrollLayout_autoscroll_indicator_drawable)
        indicatorBackground = a.getDrawable(R.styleable.AutoScrollLayout_autoscroll_indicator_background)

        if (indicatorBackground == null) {
            indicatorBackground = ColorDrawable(Color.TRANSPARENT)
        }

        indicatorGravity = a.getInteger(R.styleable.AutoScrollLayout_autoscroll_indicator_gravity, 0)
        indicatorPadding = a.getDimensionPixelOffset(R.styleable.AutoScrollLayout_autoscroll_indicator_padding, 8)
        autoStart = a.getBoolean(R.styleable.AutoScrollLayout_autoscroll_auto_start, true)
        infinite = a.getBoolean(R.styleable.AutoScrollLayout_autoscroll_scroll_infinite, true)

        a.recycle()

        initContainerView()
    }

    private fun initContainerView() {
        container = obtainContainer()
        container.layoutParams = RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(container)
    }

    private fun initIndicatorContainer() {
        if (!showIndicator || getBannerCount() <= 1) {
            indicatorContainer?.let {
                it.removeAllViews()
                parent?.let {
                    (it as ViewGroup).removeView(indicatorContainer)
                }
            }
            return
        }
        removeView(indicatorContainer)

        indicatorContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                topMargin = 0

                when (indicatorGravity) {
                    CENTER -> {
                        //remove other rules
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                        addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                        // add new rule
                        addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                        leftMargin = 0
                        rightMargin = 0
                    }

                    RIGHT -> {
                        // remove other rules
                        addRule(RelativeLayout.CENTER_HORIZONTAL, 0)
                        addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                        // add new rule
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
                        leftMargin = 0
                        rightMargin = 16
                    }
                    else -> {
                        // remove other rules
                        addRule(RelativeLayout.CENTER_HORIZONTAL, 0)
                        addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                        // add new rule
                        addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                        leftMargin = 16
                        rightMargin = 0
                    }
                }
            }

            background = indicatorBackground

            for (i in 0 until getBannerCount()) {
                val indicator = newIndicatorView()
                Log.d("TAG", "add indicator $i : $indicator ")
                addView(indicator, i)
            }

        }//end of LinearLayout

        addView(indicatorContainer)
        updateIndicatorSelection(currentPosition)
    }

    private fun newIndicatorView(): View {
        val indicator = ImageView(context)
        indicator.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            leftMargin = indicatorPadding
            rightMargin = indicatorPadding
            topMargin = indicatorPadding
            bottomMargin = indicatorPadding
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }

        //avoid drawable share the same constantState, always new drawable
        indicator.setImageDrawable(indicatorDrawable?.constantState?.newDrawable(resources))
        return indicator
    }

    private fun startScroll() {
        isStop = false
        timer.postDelayed(scrollRunnable, delayMillis)
    }

    private fun stopScroll() {
        isStop = true
        timer.removeCallbacks(scrollRunnable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoStart) {
            startScroll()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopScroll()
    }

    abstract fun obtainContainer(): T

    abstract fun getBannerCount(): Int

    abstract fun getTotalCount(): Int

    abstract fun setSelection(nextPosition: Int, smoothScroll: Boolean = true)

    fun notifySelectionChanged(index: Int) {
        currentPosition = index
        updateIndicatorSelection(index)
    }

    private fun updateIndicatorSelection(selectionIndex: Int) {
        if (!showIndicator || indicatorContainer == null || indicatorContainer?.parent == null) {
            return
        }

        indicatorContainer?.let {
            val selection = selectionIndex % getBannerCount()
            it.childViews.forEachIndexed { index, view ->
                view.isSelected = (index == selection)
                // view selector size will not update when state changed in MiUI
                // force layout will fix
                view.requestLayout()
            }
        }
    }

    fun notifyDataSetChanged() {
        initIndicatorContainer()
    }
}