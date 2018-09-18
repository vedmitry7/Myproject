package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.views.fragments.PhotoAlbumsFragment;
import app.mycity.mycity.views.fragments.SimpleFragment;
import app.mycity.mycity.views.fragments.places.PlaceEvents;
import app.mycity.mycity.views.fragments.places.PlaceInfoFragment;
import app.mycity.mycity.views.fragments.places.PlacesCheckinFragment;

public class PlacePagerAdapter extends FragmentPagerAdapter {

    Place place;

    public PlacePagerAdapter(FragmentManager fm, Place place) {
        super(fm);
        Log.d("TAG21", "Place pager Init");
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
                return new PhotoAlbumsFragment();
            case 3:
                return new PlaceEvents();
        }
        return new PlacesCheckinFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Чекины";
            case 1:
                return "Инфо";
            case 2:
                return "Фотоотчеты";
            case 3:
                return "События";
        }
        return null;
    }
}
