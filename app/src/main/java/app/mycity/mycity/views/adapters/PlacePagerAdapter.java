package app.mycity.mycity.views.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.views.fragments.SimpleFragment;
import app.mycity.mycity.views.fragments.places.PlaceActions;
import app.mycity.mycity.views.fragments.places.PlaceEvents;
import app.mycity.mycity.views.fragments.places.PlaceInfoFragment;
import app.mycity.mycity.views.fragments.places.PlacePhotoAlbumFragment;
import app.mycity.mycity.views.fragments.places.PlaceServices;
import app.mycity.mycity.views.fragments.places.PlacesCheckinFragment;

public class PlacePagerAdapter extends FragmentPagerAdapter {

    Place place;

    String tabName;

    public PlacePagerAdapter(FragmentManager fm, Place place, String name) {
        super(fm);
        Log.d("TAG21", "Place pager Init");
        this.place = place;
        tabName = name;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return PlacesCheckinFragment.createInstance(tabName, String.valueOf(place.getId()));
            case 1:
                return PlaceInfoFragment.createInstance();
            case 2:
                return PlacePhotoAlbumFragment.createInstance(tabName, place.getId());
            case 3:
                return PlaceEvents.createInstance(tabName, place.getId());
            case 4:
                return PlaceActions.createInstance(tabName, place.getId());
            case 5:
                return PlaceServices.createInstance(tabName, place.getId());
        }
        return new SimpleFragment();
    }

    @Override
    public int getCount() {
        return 6;
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
            case 4:
                return "Акции";
            case 5:
                return "Услуги";
        }
        return null;
    }
}
