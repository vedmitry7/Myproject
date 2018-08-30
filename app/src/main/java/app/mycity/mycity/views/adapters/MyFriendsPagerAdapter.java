package app.mycity.mycity.views.adapters;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.FriendsAllListFragment;
import app.mycity.mycity.views.fragments.FriendsOnlineListFragment;
import app.mycity.mycity.views.fragments.SimpleFragment;


public class MyFriendsPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fm;
    private TabLayout tabLayout;

    public MyFriendsPagerAdapter(FragmentManager fm, TabLayout tabLayout) {
        super(fm);
        Log.d("TAG", "Constructor " + this.getClass().getSimpleName());
        Log.i("TAG3","new Friends Pager created");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Log.d("TAG", "new MyFriendsAllFragment() " + this.getClass().getSimpleName());
                return new FriendsAllListFragment();
            case 1:
                Log.d("TAG", "new MyFriendsOnlineFragment() " + this.getClass().getSimpleName());
                return new FriendsOnlineListFragment();
            case 2:
                return new SimpleFragment();
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
                return "Заявки";
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
