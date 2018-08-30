package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.views.fragments.PlaceInfoFragment;
import app.mycity.mycity.views.fragments.PlacesCheckinFragment;
import app.mycity.mycity.views.fragments.SimpleFragment;

public class PlacePagerAdapter extends FragmentStatePagerAdapter {


    String placeId;

    public PlacePagerAdapter(FragmentManager fm, String placeId) {
        super(fm);
        this.placeId = placeId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PlacesCheckinFragment.createInstance(placeId);
            case 1:
                return new PlaceInfoFragment();
            case 2:
                return new SimpleFragment();
        }
        return new PlacesCheckinFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Медиа";
            case 1:
                return "Инфо";
            case 2:
                return "События";
        }
        return null;
    }
}
