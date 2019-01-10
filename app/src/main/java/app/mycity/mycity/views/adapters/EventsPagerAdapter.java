package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.events.AllActions;
import app.mycity.mycity.views.fragments.events.AllEvents;
import app.mycity.mycity.views.fragments.feed.FeedPhotoAlbumFragment;

public class EventsPagerAdapter extends FragmentPagerAdapter {


    String tabName;

    public EventsPagerAdapter(FragmentManager fm, String tabName) {
        super(fm);
        this.tabName = tabName;
        Log.d("TAG21", "Top pager Init");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return AllEvents.createInstance(tabName, 4);
            case 1:
                return AllActions.createInstance(tabName, 4);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "События";
            case 1:
                return "Акции";
        }
        return null;
    }
}
