package com.github.deskid.autoscrollbanner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet

abstract class AutoScrollViewPager
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AbsAutoScrollLayout<ViewPager>(context, attrs, defStyleAttr) {

    override fun obtainContainer(): ViewPager {
        val viewPager = ViewPager(context)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //do nothing
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //do nothing
            }

            override fun onPageSelected(position: Int) {
                notifySelectionChanged(position)
            }

        })
        return viewPager
    }

    override fun getTotalCount(): Int {
        return container.adapter?.count ?: 0
    }

    override fun setSelection(nextPosition: Int, smoothScroll: Boolean) {

        val bannerCount = getBannerCount()
        val currentIndex = container.currentItem

        val round = Math.abs(nextPosition - currentIndex) / bannerCount
        var goNextIndex = nextPosition

        if (round >= 1) {
            if (goNextIndex > currentIndex) {
                goNextIndex -= (round * bannerCount)
            } else {
                goNextIndex += (round * bannerCount)
            }
        }
        notifySelectionChanged(goNextIndex)

        timer.post(StepScrollRunnable(goNextIndex, smoothScroll))
    }

    protected fun setAdapter(adapter: PagerAdapter) {
        container.adapter = adapter
        notifyDataSetChanged()
    }

    inner class StepScrollRunnable(private val nextPosition: Int, private val smoothScroll: Boolean) : Runnable {
        override fun run() {
            val maxStep = container.offscreenPageLimit
            val currentIndex = container.currentItem

            val offset = Math.abs(currentIndex - nextPosition)

            if (offset > maxStep) {
                container.setCurrentItem(if (currentIndex > nextPosition) currentIndex - maxStep else currentIndex + maxStep, false)
                timer.post(StepScrollRunnable(nextPosition, smoothScroll))
            } else {
                container.setCurrentItem(nextPosition, smoothScroll)
            }
        }
    }
}