package app.mycity.mycity.views.adapters;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.subscribers.SubscribersListFragment;
import app.mycity.mycity.views.fragments.subscribers.FriendsCommonListFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscribersOnlineListFragment;


public class SomeoneFriendsPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fm;
    private TabLayout tabLayout;

    SubscribersListFragment friendsAllListFragment;
    SubscribersOnlineListFragment friendsOnlineListFragment;

    public SomeoneFriendsPagerAdapter(FragmentManager fm, TabLayout tabLayout, String id) {
        super(fm);
        Log.d("TAG", "Constructor " + this.getClass().getSimpleName());
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);

        friendsAllListFragment = new SubscribersListFragment();
        friendsOnlineListFragment = new SubscribersOnlineListFragment();

        friendsAllListFragment.setArguments(bundle);
        friendsOnlineListFragment.setArguments(bundle);

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d("TAG", "new MyFriendsAllFragment() " + this.getClass().getSimpleName());
                return friendsAllListFragment;
            case 1:
                Log.d("TAG", "new MyFriendsOnlineFragment() " + this.getClass().getSimpleName());
                return friendsOnlineListFragment;
            case 2:
                return new FriendsCommonListFragment();
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
            case 2:
                return "Общие";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
