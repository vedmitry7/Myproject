package app.mycity.mycity.views.adapters;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.subscribers.SubscribersListFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscribersOnlineListFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscriptionListFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscriptionsOnlineListFragment;


public class SubscribersPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fm;

    String tabName;
    String userId;

    public SubscribersPagerAdapter(FragmentManager fm, String name, String userId) {
        super(fm);
        Log.d("TAG", "Constructor " + this.getClass().getSimpleName());
        Log.i("TAG3","new Friends Pager created");
        tabName = name;
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d("TAG", "new MyFriendsAllFragment() " + this.getClass().getSimpleName());
                return SubscribersListFragment.createInstance(tabName, userId);
            case 1:
                Log.d("TAG", "new MyFriendsOnlineFragment() " + this.getClass().getSimpleName());
                return SubscribersOnlineListFragment.createInstance(tabName, userId);
        }
        return null;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                Log.d("TAG", "getPageTitle " + this.getClass().getSimpleName());
                return "Все";
            case 1:
                return "Online";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
