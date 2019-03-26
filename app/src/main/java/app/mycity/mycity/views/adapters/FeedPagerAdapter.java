package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.mycity.mycity.views.fragments.feed.FeedCheckinFragmentNew;

public class FeedPagerAdapter extends FragmentPagerAdapter {

    String tabName;

    FeedCheckinFragmentNew allCheckinFragment;
    FeedCheckinFragmentNew subscriptionsCheckinFragment;

    public FeedPagerAdapter(FragmentManager fm, String tabName) {
        super(fm);
        this.tabName = tabName;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                allCheckinFragment = FeedCheckinFragmentNew.createInstance(tabName, "all");
                return allCheckinFragment;
            case 1:
                subscriptionsCheckinFragment = FeedCheckinFragmentNew.createInstance(tabName, "subscriptions");
                return subscriptionsCheckinFragment;
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

    public FeedCheckinFragmentNew getAllCheckinFragment() {
        return allCheckinFragment;
    }

    public FeedCheckinFragmentNew getSubscriptionsCheckinFragment() {
        return subscriptionsCheckinFragment;
    }
}
