package app.mycity.mycity.adapters;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.mycity.mycity.fragments.MyFriendsAllFragment;
import app.mycity.mycity.fragments.SimpleFragment;


public class MyFriendsPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    TabLayout tabLayout;

    public MyFriendsPagerAdapter(FragmentManager fm, TabLayout tabLayout) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MyFriendsAllFragment();
            case 1:
                return new SimpleFragment();
            case 2:
                return new SimpleFragment();
        }
        return null;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
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
