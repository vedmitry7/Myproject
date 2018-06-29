package app.mycity.mycity.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import app.mycity.mycity.R;
import app.mycity.mycity.fragments.MyFriendsFragment;
import app.mycity.mycity.fragments.ProfileFragment;
import app.mycity.mycity.fragments.settings.MainSettingsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainAct {

    @BindView(R.id.main_act_top_button)     ImageView topButton;
    @BindView(R.id.main_act_places_button)  ImageView placesButton;
    @BindView(R.id.main_act_search_button)  ImageView searchButton;
    @BindView(R.id.main_act_feed_button)    ImageView feedButton;

    @BindView(R.id.main_act_profile_button)     ImageView profileButton;

    FragmentManager fragmentManager;

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
    }

    @OnClick(R.id.main_act_profile_button_container)
    public void profile(View v){
        ProfileFragment profileFragment = new ProfileFragment();

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
    public void startMyFriends() {
        MyFriendsFragment myFriendsFragment = new MyFriendsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, myFriendsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
