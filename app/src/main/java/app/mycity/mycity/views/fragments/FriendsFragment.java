package app.mycity.mycity.views.fragments;

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

import app.mycity.mycity.R;
import app.mycity.mycity.views.activities.MainActivity;
import app.mycity.mycity.views.adapters.MyFriendsPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsFragment extends Fragment {


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

        // Log.i("TAG21","Friends stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG21","Friends Fragment - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3","Friends fragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "Attach " + this.getClass().getSimpleName());
        fragmentManager = ((MainActivity) context).getSupportFragmentManager();

    }

    public void onStart() {
        super.onStart();
        MyFriendsPagerAdapter pagerAdapter = new MyFriendsPagerAdapter(getChildFragmentManager(), tabLayout );
        viewPager.setAdapter(pagerAdapter);
//       viewPager.setOffscreenPageLimit(3);
        //viewPager.addOnPageChangeListener(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("TAG", "Start " + this.getClass().getSimpleName());

    }

    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume " + this.getClass().getSimpleName());
        Log.i("TAG21","Friends Fragment resume - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3","Friends fragment resume");
    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());
        Log.i("TAG3","Friends fragment pause");
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
}
