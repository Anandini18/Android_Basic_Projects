package com.nandini.android.fragmentstab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var viewPager : ViewPager = findViewById(R.id.viewPager) as ViewPager
        var tabLayout : TabLayout = findViewById(R.id.tabLayout) as TabLayout

        val fragmentAdapter = FragmentAdapter(supportFragmentManager)
        fragmentAdapter.addFragment(AllFragment(),"All")
        fragmentAdapter.addFragment(VideosFragment(),"Videos")
        fragmentAdapter.addFragment(PhotosFragment(),"Photos")

        viewPager.adapter=fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)


    }
}