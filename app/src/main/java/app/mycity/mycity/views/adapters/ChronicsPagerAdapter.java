package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.feed.FeedCheckinFragmentNew;
import app.mycity.mycity.views.fragments.feed.FeedPhotoAlbumFragment;

public class ChronicsPagerAdapter extends FragmentPagerAdapter {


    String tabName;
    FeedPhotoAlbumFragment allAlbumFragment;
    FeedPhotoAlbumFragment subscriptionAlbumFragment;

    public ChronicsPagerAdapter(FragmentManager fm, String tabName) {
        super(fm);
        this.tabName = tabName;
        Log.d("TAG21", "Top pager Init");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                allAlbumFragment = FeedPhotoAlbumFragment.createInstance(tabName, "0");
                return allAlbumFragment;
            case 1:
                subscriptionAlbumFragment = FeedPhotoAlbumFragment.createInstance(tabName, "1");
                return subscriptionAlbumFragment;
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

    public FeedPhotoAlbumFragment getAllAlbumFragment() {
        return allAlbumFragment;
    }

    public FeedPhotoAlbumFragment getSubscriptionAlbumFragment() {
        return subscriptionAlbumFragment;
    }
}
