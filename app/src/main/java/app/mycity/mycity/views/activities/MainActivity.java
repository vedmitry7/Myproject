package app.mycity.mycity.views.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.TestService;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseSocketServer;
import app.mycity.mycity.filter_desc_post.FilterImageActivity;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.fragments.DialogsFragment;
import app.mycity.mycity.views.fragments.LongListFragment;
import app.mycity.mycity.views.fragments.feed.FeedCheckinFragment;
import app.mycity.mycity.views.fragments.places.PlaceFragment;
import app.mycity.mycity.views.fragments.places.PlacesFragment;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
import app.mycity.mycity.views.fragments.settings.MainSettingsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MainAct {

    @BindView(R.id.main_act_top_button)     ImageView topButton;
    @BindView(R.id.main_act_places_button)  ImageView placesButton;
    @BindView(R.id.main_act_search_button)  ImageView searchButton;
    @BindView(R.id.main_act_feed_button)    ImageView feedButton;

    @BindView(R.id.main_act_profile_button) ImageView profileButton;

    @BindView(R.id.main_act_messages_container)         RelativeLayout messageButton;
    @BindView(R.id.main_act_notification_container)     RelativeLayout notificattionButton;


    android.support.v4.app.FragmentManager fragmentManager;

    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateTimestemp();

        new Timer().schedule(new TimerTask() {
           @Override
           public void run() {
               Intent serviceIntent = new Intent(MainActivity.this, TestService.class);
               startService(serviceIntent);

           }
       }, 500);

        ButterKnife.bind(this);

        setIndicator(profileButton);

        fragmentManager = getSupportFragmentManager();


     /*   profileFragment = new ProfileFragment();
        myFriendsFragment = new LongListFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_act_fragment_container, profileFragment);
        transaction.add(R.id.main_act_fragment_container, myFriendsFragment);

        transaction.show(profileFragment).commit();*/

//     initRealm();

    }

    private void updateTimestemp() {
        ApiFactory.getApi().getSocketServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseSocketServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseSocketServer>> call, Response<ResponseContainer<ResponseSocketServer>> response) {
                if(response.body()!=null){
                    SharedManager.addProperty("ts", response.body().getResponse().getTs());
                    Log.d("TAG21", "update TS - " + response.body().getResponse().getTs());
                    Toast.makeText(MainActivity.this, response.body().getResponse().getTs(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseSocketServer>> call, Throwable t) {

            }
        });
    }


    public void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("chat.realm_" + SharedManager.getProperty(Constants.KEY_LOGIN))
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @OnClick(R.id.mainActAddBtn)
    public void photo(View v){
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent intent = new Intent(MainActivity.this, FilterImageActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Permissions are not granted!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }


    void clearFragmentStack(){
        FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
        int count = fm.getBackStackEntryCount();
        for(int i = 0; i < count; ++i) {
            fm.popBackStack();
        }
    }

    private void clearStack(){
        int count = fragmentManager.getBackStackEntryCount();
        while(count > 0){
            fragmentManager.popBackStack();
            count--;
        }
    }

    @OnClick(R.id.main_act_messages_container)
    public void message(View v){
        Log.d("TAG", "messages");

        DialogsFragment dialogsFragment = new DialogsFragment();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
      //  transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, dialogsFragment);
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

        switch (i){
            case 0 :
                MainSettingsFragment settingsFragment = new MainSettingsFragment();
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
           //     transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                transaction.replace(R.id.main_act_fragment_container, settingsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.OpenUser event){
        SomeoneProfileFragment profileFragment = new SomeoneProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", event.getMessage());
        profileFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //    transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack("someoneProfile");
        transaction.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.OpenComments event){
        /*CommentsFragment commentsFragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postId", event.getPostId());
        bundle.putString("ownerId", event.getOwnerId());
        commentsFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //    transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, commentsFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("postId", event.getPostId());
        intent.putExtra("ownerId", event.getOwnerId());
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.OpenPlace event){
        PlaceFragment placeFragment = new PlaceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("placeId", event.getId());
        placeFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //    transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, placeFragment);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void closefragment() {
        getFragmentManager().beginTransaction().remove(getVisibleFragment()).commit();
    }

    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getFragmentManager().findFragmentByTag(tag);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Log.d("TAG", "BaCk");
       // closefragment();
        super.onBackPressed();
    }
}
