package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlaceSubscribersPagerAdapter;
import app.mycity.mycity.views.adapters.SubscribersPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class PlaceSubscribersFragment extends Fragment implements TabStacker.TabStackInterface{


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    Storage storage;


    public static PlaceSubscribersFragment createInstance(String name, int tabPos, String groupId) {
        PlaceSubscribersFragment fragment = new PlaceSubscribersFragment();
        Log.i("TAG23", "Create Subscribers " + name + " " + groupId);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("groupId", groupId);
        bundle.putInt("tabPos", tabPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick (R.id.profileFragBackButtonContainer)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Log.i("TAG21","Friends stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
     //   Log.i("TAG21","Friends Fragment - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG23","Place sub...s  on CreateView");

        Util.indicateTabImageView(getContext(), view, getArguments().getInt("tabPos"));
        Util.setOnTabClick(view);

        title.setText("Подписчики");

        if(tabLayout!=null){
            Log.i("TAG","TAB LAYOUT ! NULL");
        }

        if(viewPager!=null){
            Log.i("TAG","PAGER != NULL");
        }

        if(getChildFragmentManager()!=null){
            Log.i("TAG","getChildFragmentManager !" +
                    " NULL");
        }
        PlaceSubscribersPagerAdapter pagerAdapter = new PlaceSubscribersPagerAdapter(getChildFragmentManager(), getArguments().getString("name"), getArguments().getString("groupId"));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        //viewPager.addOnPageChangeListener(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        Log.d("TAG", "Start " + this.getClass().getSimpleName());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "Attach " + this.getClass().getSimpleName());
        // fragmentManager = ((MainActivity2) context).getSupportFragmentManager();
        storage = (Storage) context;
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume " + this.getClass().getSimpleName());
       // Log.i("TAG21","Friends Fragment resume - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG","Friends fragment resume");
    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());
        Log.i("TAG","Friends fragment pause");
    }

    public void onStop() {
        super.onStop();
        Log.d("TAG", "Stop " + this.getClass().getSimpleName());
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAG", "Destroy view " + this.getClass().getSimpleName());
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "Destroy " + this.getClass().getSimpleName());
        Log.i("TAG3","Friends fragment destroy");
    }

    public void onDetach() {
        super.onDetach();
        Log.d("TAG", "Detach " + this.getClass().getSimpleName());
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {
        Log.d("TAG23", "REASON present- " + presentReason.name());

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

        Log.d("TAG23", "REASON desmiss - " + dismissReason.name());


        if(dismissReason == TabStacker.DismissReason.BACK){

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.remove(getArguments().get("name") + "_" + Constants.KEY_PLACE_SUBSCRIBERS + "_userlist");
                    storage.remove(getArguments().get("name") + "_" + Constants.KEY_PLACE_ONLINE_SUBSCRIBERS + "_userlist");
                    Log.d("TAG23", "delete data " + getArguments().get("name") + "_" + Constants.KEY_PLACE_SUBSCRIBERS + "_userlist");
                    Log.d("TAG23", "delete data " + getArguments().get("name") + "_" + Constants.KEY_PLACE_ONLINE_SUBSCRIBERS + "_userlist");
                }
            },200);
        }
    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }
}
