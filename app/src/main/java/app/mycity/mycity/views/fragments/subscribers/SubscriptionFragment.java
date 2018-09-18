package app.mycity.mycity.views.fragments.subscribers;

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

import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.R;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.SubscriptionsPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class SubscriptionFragment extends Fragment implements TabStacker.TabStackInterface{


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    FragmentManager fragmentManager;

    Storage storage;



    public static SubscriptionFragment createInstance(String name, String userId) {
        SubscriptionFragment fragment = new SubscriptionFragment();
        Log.i("TAG21", "Create Subscribers " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.profileFragBackButtonContainer)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Log.i("TAG21","Friends stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
     //   Log.i("TAG21","Friends Fragment - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG","Friends fragment on CreateView");

        title.setText("Подписки");

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
        SubscriptionsPagerAdapter pagerAdapter = new SubscriptionsPagerAdapter(getChildFragmentManager(), getArguments().getString("name"), getArguments().getString("userId"));
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
            Log.d("TAG21", "Delete - " + getArguments().get("name") + "_userlist");
            Log.d("TAG21", "Delete - " + getArguments().get("name") + "_userListOnline");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.setDate(getArguments().get("name") + "_userlist", null);
                    storage.setDate(getArguments().get("name") + "_userListOnline", null);
                }
            }, 200);
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
