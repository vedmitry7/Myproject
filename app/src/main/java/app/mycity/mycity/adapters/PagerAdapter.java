package app.mycity.mycity.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import app.mycity.mycity.fragments.SimpleFragment;
import app.mycity.mycity.fragments.mainScreen.FeedFragment;
import app.mycity.mycity.fragments.mainScreen.PlacesFragment;
import app.mycity.mycity.fragments.mainScreen.SearchFragment;
import app.mycity.mycity.fragments.mainScreen.TopFragment;

public class PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

    private FragmentManager fm;
    TabLayout tabLayout;

    TopFragment topFragment;
    PlacesFragment placesFragment;
    SearchFragment searchFragment;
    FeedFragment feedFragment;

    public PagerAdapter(FragmentManager fm, TabLayout tabLayout) {
        super(fm);
        this.fm = fm;
        this.tabLayout = tabLayout;
        topFragment = new TopFragment();
        placesFragment = new PlacesFragment();
        searchFragment = new SearchFragment();
        feedFragment = new FeedFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return topFragment;
            case 1:
                return placesFragment;
            case 3:
                return searchFragment;
            case 4:
                return feedFragment;
            case 2:
                return new SimpleFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(tabLayout!=null){
            for (int i = 0; i < 5; i++) {
                if(i == position){
                    tabLayout.getTabAt(i).getIcon().setColorFilter(Color.parseColor("#009788"), PorterDuff.Mode.SRC_ATOP);
                    continue;
                }
                tabLayout.getTabAt(i).getIcon().setColorFilter(Color.parseColor("#b0c1c9"), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}