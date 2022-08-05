package com.flottapps.calculadora.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.flottapps.calculadora.R
import com.flottapps.calculadora.model.OnBoardingData

class OnBoardingViewPagerAdapter(private var onBoardingDataList: List<OnBoardingData>): PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.onboarding, null)
        val imageView = view.findViewById<ImageView>(R.id.ob_picture)
        val title = view.findViewById<TextView>(R.id.ob_title)
        val desc = view.findViewById<TextView>(R.id.ob_desc)

        title.text = onBoardingDataList[position].title
        desc.text = onBoardingDataList[position].desc
        imageView.setImageResource(onBoardingDataList[position].picture)

        container.addView(view)
        return view
    }
}