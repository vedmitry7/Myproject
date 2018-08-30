package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.views.fragments.places.PlaceInfoFragment;
import app.mycity.mycity.views.fragments.places.PlacesCheckinFragment;
import app.mycity.mycity.views.fragments.SimpleFragment;

public class PlacePagerAdapter extends FragmentStatePagerAdapter {

    Place place;

    public PlacePagerAdapter(FragmentManager fm, Place place) {
        super(fm);
        this.place = place;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PlacesCheckinFragment.createInstance(String.valueOf(place.getId()));
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
