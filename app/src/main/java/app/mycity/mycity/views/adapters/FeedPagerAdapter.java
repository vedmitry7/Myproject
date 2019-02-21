package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.SimpleFragment;
import app.mycity.mycity.views.fragments.events.AllEvents;
import app.mycity.mycity.views.fragments.feed.FeedCheckinFragmentNew;
import app.mycity.mycity.views.fragments.feed.FeedPhotoAlbumFragment;

public class FeedPagerAdapter extends FragmentPagerAdapter {

    String tabName;

    public FeedPagerAdapter(FragmentManager fm, String tabName) {
        super(fm);
        this.tabName = tabName;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return FeedCheckinFragmentNew.createInstance(tabName, "all");
            case 1:
                return FeedCheckinFragmentNew.createInstance(tabName, "subscriptions");
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
                return "ВСЕ";
            case 1:
                return "ПОДПИСКИ";
        }
        return null;
    }
}
