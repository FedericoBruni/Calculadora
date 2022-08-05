package com.flottapps.calculadora

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.flottapps.calculadora.Calculator.Companion.prefs
import com.flottapps.calculadora.adapter.OnBoardingViewPagerAdapter
import com.flottapps.calculadora.model.OnBoardingData
import com.google.android.material.tabs.TabLayout
import java.util.ArrayList

class ViewPagerActivity : AppCompatActivity() {

    var positionVP = 0
    private var onBoardingViewPagerAdapter: OnBoardingViewPagerAdapter? = null
    private var tabLayout: TabLayout? = null
    private var onBoardingViewPager : ViewPager? = null
    lateinit var nextButton: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewpager)
        tabLayout = findViewById(R.id.tab_indicator)
        nextButton = findViewById(R.id.next_button)
        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(OnBoardingData(getString(R.string.ob_swipe_title), getString(R.string.ob_swipe_desc), R.drawable.swipe_onboarding))
        onBoardingData.add(OnBoardingData(getString(R.string.ob_rate_us_title), getString(R.string.ob_rate_us_desc), R.drawable.rate_onboarding))
        setOnBoardingViewPagerAdapter(onBoardingData)

        positionVP = onBoardingViewPager!!.currentItem
        nextButton.setOnClickListener {
            if (positionVP < onBoardingData.size){
                positionVP++
                onBoardingViewPager!!.currentItem = positionVP
            }
            if (positionVP == onBoardingData.size){
                prefs.saveFirstTimeConfig()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                positionVP = tab!!.position
                if (tab.position == onBoardingData.size -1){
                    nextButton.text = getString(R.string.finish)
                } else {
                    nextButton.text = getString(R.string.next)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setOnBoardingViewPagerAdapter(onBoardingData: List<OnBoardingData>){
        onBoardingViewPager = findViewById(R.id.viewPager)
        onBoardingViewPagerAdapter = OnBoardingViewPagerAdapter(onBoardingData)
        onBoardingViewPager!!.adapter = onBoardingViewPagerAdapter
        tabLayout?.setupWithViewPager(onBoardingViewPager)
    }
}