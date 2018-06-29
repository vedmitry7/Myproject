package app.mycity.mycity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.mycity.mycity.R;
import app.mycity.mycity.activities.MainActivity;
import app.mycity.mycity.adapters.MyFriendsPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFriendsFragment extends Fragment {


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;


    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_friends, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyFriendsPagerAdapter pagerAdapter = new MyFriendsPagerAdapter(fragmentManager, tabLayout );
        viewPager.setAdapter(pagerAdapter);
        //viewPager.addOnPageChangeListener(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        fragmentManager = ((MainActivity) context).getSupportFragmentManager();

    }

}
