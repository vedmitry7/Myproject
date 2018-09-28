package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.top.AllPeoplesFragment;
import app.mycity.mycity.views.fragments.top.TopCheckinsFragment;
import app.mycity.mycity.views.fragments.top.TopPlacesFragment;

public class PeoplesPagerAdapter extends FragmentPagerAdapter {

    public PeoplesPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.d("TAG21", "Top pager Init");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TopCheckinsFragment();
            case 1:
                return new TopPlacesFragment();
            case 2:
                return new AllPeoplesFragment();

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
                return "Места";
            case 2:
                return "Люди";
        }
        return null;
    }
}
