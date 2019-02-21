package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.Constants;
import app.mycity.mycity.views.fragments.UniversalUserListFragment;


public class PlaceSubscribersPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fm;

    String tabName;
    String groupId;

    public PlaceSubscribersPagerAdapter(FragmentManager fm, String name, String groupId) {
        super(fm);
        Log.d("TAG", "Constructor " + this.getClass().getSimpleName());
        Log.i("TAG3","new Friends Pager created");
        tabName = name;
        this.groupId = groupId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d("TAG", "new MyFriendsAllFragment() " + this.getClass().getSimpleName());
                return UniversalUserListFragment.createInstance(tabName, groupId, Constants.KEY_PLACE_SUBSCRIBERS);
            case 1:
                Log.d("TAG", "new MyFriendsOnlineFragment() " + this.getClass().getSimpleName());
                return UniversalUserListFragment.createInstance(tabName, groupId, Constants.KEY_PLACE_ONLINE_SUBSCRIBERS);
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
                return "Мои подписки";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
