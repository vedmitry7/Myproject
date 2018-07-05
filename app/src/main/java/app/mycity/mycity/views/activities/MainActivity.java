package app.mycity.mycity.views.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mycity.mycity.EventBusMessages;
import app.mycity.mycity.R;
import app.mycity.mycity.fragments.settings.MainSettingsFragment;
import app.mycity.mycity.views.fragments.FriendsFragment;
import app.mycity.mycity.views.fragments.LongListFragment;
import app.mycity.mycity.views.fragments.SomeoneFriendsFragment;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainAct {

    @BindView(R.id.main_act_top_button)     ImageView topButton;
    @BindView(R.id.main_act_places_button)  ImageView placesButton;
    @BindView(R.id.main_act_search_button)  ImageView searchButton;
    @BindView(R.id.main_act_feed_button)    ImageView feedButton;

    @BindView(R.id.main_act_profile_button) ImageView profileButton;

    FragmentManager fragmentManager;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setIndicator(profileButton);

        fragmentManager = getFragmentManager();

    }

    @OnClick(R.id.main_act_top_button_container)
    public void top(View v){
        setIndicator(topButton);
    }

    @OnClick(R.id.main_act_places_button_container)
    public void places(View v){
        setIndicator(placesButton);
    }

    @OnClick(R.id.main_act_search_button_container)
    public void search(View v){
        setIndicator(searchButton);
    }

    @OnClick(R.id.main_act_feed_button_container)
    public void feed(View v){
        setIndicator(feedButton);
        LongListFragment myFriendsFragment = new LongListFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, myFriendsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.main_act_profile_button_container)
    public void profile(View v){
        profileFragment = new ProfileFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setIndicator(ImageView button){
        topButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        placesButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        searchButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        feedButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        button.setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void startSettings(int i) {
        Fragment settingsFragment = null;
        switch (i){
            case 0 :
                settingsFragment = new MainSettingsFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                transaction.replace(R.id.main_act_fragment_container, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

        }
    }

    @Override
    public void startFriends() {
        FriendsFragment myFriendsFragment = new FriendsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, myFriendsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void startFriendsById(String id) {
        SomeoneFriendsFragment someoneFriendsFragment = new SomeoneFriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        someoneFriendsFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, someoneFriendsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.OpenUser event){
        // your implementation

        SomeoneProfileFragment profileFragment = new SomeoneProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", event.getMessage());
        profileFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Log.d("TAG", "BaCk");
        super.onBackPressed();
    }
}
