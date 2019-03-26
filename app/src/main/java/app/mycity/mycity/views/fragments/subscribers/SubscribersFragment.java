package app.mycity.mycity.views.fragments.subscribers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.R;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.SubscribersPagerAdapter;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class SubscribersFragment extends Fragment implements TabStacker.TabStackInterface{


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    Storage storage;


    public static SubscribersFragment createInstance(String name, String userId) {
        SubscribersFragment fragment = new SubscribersFragment();
        Log.i("TAG21", "Create Subscribers " + name + " " + userId);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("userId", userId);
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

    @OnClick(R.id.backButton)
    public void sadsa(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Log.i("TAG21","Friends stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
     //   Log.i("TAG21","Friends Fragment - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG","Friends fragment on CreateView");

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);

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
        SubscribersPagerAdapter pagerAdapter = new SubscribersPagerAdapter(getChildFragmentManager(), getArguments().getString("name"), getArguments().getString("userId"));
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

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {
        Log.d("TAG21", "REASON - " + dismissReason);
        if(dismissReason == TabStacker.DismissReason.BACK){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.remove(getArguments().get("name") + "_key_subscribers_userlist");
                    storage.remove(getArguments().get("name") + "_key_subscribers_online_userlist");
                    Log.d("TAG24", "remove" + getArguments().get("name") + "_key_subscribers_online_userlist");
                    Log.d("TAG24", "remove" + getArguments().get("name") + "_key_subscribers_userlist");
                }
            }, 200);
            Log.d("TAG21", "Delete - " + getArguments().get("name") + "_userlist");
            Log.d("TAG21", "Delete - " + getArguments().get("name") + "_userListOnline");


            if(storage.getDate(getArguments().get("name") + "_userlist")==null){
                Log.d("TAG21", "Delete -_work");
            } else {
                Log.d("TAG21", "Delete - not _work");
            }
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
