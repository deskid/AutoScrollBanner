package com.github.deskid.autoscrollbanner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.deskid.autoscrollbanner.utils.loadUrl
import com.github.deskid.autoscrollbanner.utils.toast
import java.util.*

class AutoScrollBanner
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AutoScrollViewPager(context, attrs, defStyleAttr) {

    private var images: ArrayList<String> = ArrayList()

    fun setImageData(images: ArrayList<String>) {
        if (images.isEmpty()) {
            return
        }

        this.images.clear()
        this.images.addAll(images)
        setAdapter(BannerAdapter())
    }

    //this function is invoked in parent init constructor, but where images has not init yet
    override fun getBannerCount(): Int {
        if (images == null) {
            return 0
        }
        return images.size
    }

    inner class BannerAdapter : PagerAdapter() {

        private val size = images.size

        override fun isViewFromObject(view: View?, obj: Any?): Boolean = view == obj

        override fun getCount(): Int {
            return Int.MAX_VALUE
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            val index = (position) % size
            val url = images[index]
            val view = ImageView(context)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
            view.setOnClickListener {
                //todo click listener
                context.toast("you clicked $index banner")
            }

            //todo get rid of Glide dependency
            view.loadUrl(url)

            container?.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return view
        }

        override fun destroyItem(container: ViewGroup?, position: Int, obj: Any?) {
            container?.removeView(obj as View)
        }
    }
}

