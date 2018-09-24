package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.SimpleFragment;
import app.mycity.mycity.views.fragments.feed.FeedCheckinFragment;
import app.mycity.mycity.views.fragments.feed.FeedEvents;
import app.mycity.mycity.views.fragments.top.TopCheckinsFragment;
import app.mycity.mycity.views.fragments.top.TopPlacesFragment;
import app.mycity.mycity.views.fragments.top.TopUsersFragment;

public class FeedPagerAdapter extends FragmentPagerAdapter {


    String tabName;

    public FeedPagerAdapter(FragmentManager fm, String tabName) {
        super(fm);
        this.tabName = tabName;
        Log.d("TAG21", "Top pager Init");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FeedCheckinFragment.createInstance(tabName);
            case 1:
                return new FeedEvents();
            case 2:
                return new SimpleFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Чекины";
            case 1:
                return "События";
            case 2:
                return "Подписки";
        }
        return null;
    }
}
