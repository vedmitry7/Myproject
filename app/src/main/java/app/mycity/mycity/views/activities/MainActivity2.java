package app.mycity.mycity.views.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.Constants;
import app.mycity.mycity.LocationService;
import app.mycity.mycity.NewTestService;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.api.model.ResponseSocketServer;
import app.mycity.mycity.filter_desc_post.FilterImageActivity;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.adapters.PlacesByCoordinatesRecyclerAdapter;
import app.mycity.mycity.views.fragments.CommentsFragment;
import app.mycity.mycity.views.fragments.DialogsFragment;
import app.mycity.mycity.views.fragments.NotificationsFragment;
import app.mycity.mycity.views.fragments.feed.FeedFragment;
import app.mycity.mycity.views.fragments.feed.FeedPhotoReportFragmentContent;
import app.mycity.mycity.views.fragments.feed.FeedPlacesCheckinFragment;
import app.mycity.mycity.views.fragments.feed.PhotoReportFragment;
import app.mycity.mycity.views.fragments.places.PlaceSubscribersFragment;
import app.mycity.mycity.views.fragments.places.UsersInPlaceFragment;
import app.mycity.mycity.views.fragments.profile.UserPlacesFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscribersFragment;
import app.mycity.mycity.views.fragments.subscribers.SomeoneFriendsFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscriptionFragment;
import app.mycity.mycity.views.fragments.places.PlaceFragment;
import app.mycity.mycity.views.fragments.places.PlacesFragment;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
import app.mycity.mycity.views.fragments.settings.MainSettingsFragment;
import app.mycity.mycity.views.fragments.top.PeoplesFragment;
import app.mycity.mycity.views.fragments.top.SuperPeoplesFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity implements MainAct, Storage {

/*    @BindView(R.id.main_act_top_button)     ImageView topButton;
    @BindView(R.id.main_act_places_button)  ImageView placesButton;
    @BindView(R.id.main_act_search_button)  ImageView searchButton;
    @BindView(R.id.main_act_feed_button)    ImageView feedButton;
    @BindView(R.id.main_act_profile_button) ImageView profileButton;*/

    @BindView(R.id.main_act_messages_container)         RelativeLayout messageButton;

    private TabStacker mTabStacker;

    Tab currentTab;

    HashMap<String, Object> date =new HashMap<>();

    @Override
    public Object getDate(String key) {
        Log.i("TAG21", "GET DATE " + key );
        if(date.containsKey(key)){
            Log.i("TabFragment", "contains");
            return date.get(key);
        }
        return null;
    }

    @Override
    public void setDate(String key, Object date) {
        this.date.put(key, date);
    }

    @Override
    public void remove(String key) {
        date.remove(key);
    }

    private enum Tab {
        TAB_PROFILE(0),
        TAB_FEED(1),
        TAB_PEOPLE(2),
        TAB_PLACES(3);

        private int mButtonResId;

        Tab(int buttonResId) {
            mButtonResId = buttonResId;
        }
    }

    android.support.v4.app.FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
   //     window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));

        updateTimestemp();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainActivity2.this, NewTestService.class);
                startService(serviceIntent);

                Intent locServiceIntent = new Intent(MainActivity2.this, LocationService.class);
                startService(locServiceIntent);

            }
        }, 500);

        ButterKnife.bind(this);

        //setIndicator(profileButton);

        fragmentManager = getSupportFragmentManager();

        mTabStacker = new TabStacker(getSupportFragmentManager(), R.id.main_act_fragment_container);

        if (savedInstanceState == null) {
            // new Activity: creates the first Tab
            selectTab(Tab.TAB_FEED);
        } else {
            // restoring Activity: restore the TabStacker, and select the saved selected tab
            mTabStacker.restoreInstance(savedInstanceState);
            Log.i("TAG21", "Clicked on Tab " + mTabStacker.getCurrentTabName());

            Tab selectedTab = Tab.valueOf(mTabStacker.getCurrentTabName());
            selectTab(selectedTab);

            selectTab(Tab.TAB_FEED);
        }

        //Tab click listener

 /*       for (final Tab tab : Tab.values()) {
            final ImageView button = findViewById(tab.mButtonResId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickOnTab(tab);
                }
            });
        }*/

     /*   profileFragment = new ProfileFragment();
        myFriendsFragment = new LongListFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_act_fragment_container, profileFragment);
        transaction.add(R.id.main_act_fragment_container, myFriendsFragment);

        transaction.show(profileFragment).commit();*/

//     initRealm();

    }

    private void onClickOnTab(Tab clickedTab) {
        Log.i("TAG21", "Clicked on Tab " + clickedTab.name());

        String tabName = clickedTab.name();
        if (mTabStacker.getCurrentTabName().equals(tabName)) {  // The user clicked again on the current stack
            mTabStacker.popToTop(true);  // Pop all but 1st fragment instantly
        } else {
            selectTab(clickedTab);
        }
    }

    private void selectTab(Tab clickedTab) {

        Log.i("TAG21", "Select Tab " + clickedTab.name());

        //updateButtonStates(clickedTab);

        // switch to Tab Stack
        String tabName = clickedTab.name();
        currentTab = clickedTab;

        if (!mTabStacker.switchToTab(tabName)) {    // tries to switch to the TAB STACK
            // no fragment yet on this stack -> push the 1st fragment of the stack

            android.support.v4.app.Fragment fragment = null;

            switch (clickedTab){
                case TAB_PEOPLE:
                    fragment = SuperPeoplesFragment.createInstance(getFragmentName(), mTabStacker.getCurrentTabSize(), 2);
                    break;
                case TAB_PLACES:
                    fragment = new PlacesFragment();
                    break;
                case TAB_PROFILE:
                    fragment = ProfileFragment.createInstance(tabName + "_" + mTabStacker.getCurrentTabSize(),mTabStacker.getCurrentTabSize(), 0);
                    break;
                case TAB_FEED:
                    fragment = FeedFragment.createInstance(getFragmentName(), getCurrentTabPosition());
                    break;
            }

            mTabStacker.replaceFragment(fragment, null);  // no animation

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickTab(EventBusMessages.SwichTab event) {
        Log.i("TAG21", "Click new Tab " + event.getPos());
        onClickOnTab(Tab.values()[event.getPos()]);
    }

    // Update Button state (white / black)
/*    private void updateButtonStates(Tab clickedTab) {
        for(final Tab tab : Tab.values()) {
            ImageView button = findViewById(tab.mButtonResId);
            if(tab == clickedTab){
                button.setColorFilter(getResources().getColor(R.color.colorAccent));
            } else {
                button.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
            }
        }
    }*/

    private void updateTimestemp() {
        ApiFactory.getApi().getSocketServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseSocketServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseSocketServer>> call, Response<ResponseContainer<ResponseSocketServer>> response) {
                if(response.body()!=null){
                    SharedManager.addProperty("ts", response.body().getResponse().getTs());
                    SharedManager.addProperty("socketServer", response.body().getResponse().getServer());
                    Log.d("TAG21", "update TS - " + response.body().getResponse().getTs());
                    Log.d("TAG21", "Socket Server - " + response.body().getResponse().getServer());
                    Toast.makeText(MainActivity2.this, response.body().getResponse().getTs(), Toast.LENGTH_SHORT).show();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.MakeCheckin event){

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Определение местоположения");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View dialogView = inflater.inflate(R.layout.asking_place_dialog, null);
        final RecyclerView recyclerView = dialogView.findViewById(R.id.placesRecyclerView);
        final List placeList = new ArrayList<>();

        final PlacesByCoordinatesRecyclerAdapter adapter = new PlacesByCoordinatesRecyclerAdapter(placeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final ProgressBar progress = dialogView.findViewById(R.id.placesDialogProgress);
        final TextView message = dialogView.findViewById(R.id.placeDialogMessage);


        ApiFactory.getApi().getPlaceByCoordinates(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), SharedManager.getProperty("latitude"), SharedManager.getProperty("longitude"), 300).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, retrofit2.Response<ResponseContainer<ResponsePlaces>> response) {
                if(response.body()!=null){
                    progress.setVisibility(View.GONE);
                    if(response.body().getResponse().getCount()==0){
                        message.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    placeList.addAll(response.body().getResponse().getItems());
                    Log.d("TAG21", "Places size" + response.body().getResponse().getItems().size());
                    adapter.update(placeList);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {

            }
        });


        dialogBuilder.setView(dialogView);
        dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedManager.addProperty("currentPlace", adapter.getSelectedPlaceId());

                if(placeList.size()>0){
                    Dexter.withActivity(MainActivity2.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        Intent intent = new Intent(MainActivity2.this, FilterImageActivity.class);
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
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();




        Log.d("TAG21", "PHOTO - ");


    }

/*    @OnClick(R.id.main_act_top_button_container)
    public void top(View v){
        setIndicator(topButton);
    }

    @OnClick(R.id.main_act_places_button_container)
    public void places(View v){
   *//*     setIndicator(placesButton);
        PlacesFragment placesFragment = new PlacesFragment();
        mTabStacker.replaceFragment(placesFragment, null);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
      //  transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.add(R.id.main_act_fragment_container, placesFragment);
        transaction.addToBackStack(null);
        transaction.commit();*//*
    }

    @OnClick(R.id.main_act_search_button_container)
    public void search(View v){
        setIndicator(searchButton);
        LongListFragment myFriendsFragment = new LongListFragment();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, myFriendsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick(R.id.main_act_feed_button_container)
    public void feed(View v){
       *//* setIndicator(feedButton);
        FeedFragment feedFragment = new FeedFragment();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
       // transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, feedFragment);
        transaction.addToBackStack(null);
        transaction.commit();*//*
    }

    @OnClick(R.id.main_act_profile_button_container)
    public void profile(View v){
   *//*     profileFragment = new ProfileFragment();

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.add(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack("myProfile");
        transaction.commit();

        clearFragmentStack();*//*
    }*/

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
        Log.d("TAG21", "messages " + getCurrentTabPosition());

        DialogsFragment dialogsFragment = DialogsFragment.createInstance(getFragmentName(), getCurrentTabPosition());
        mTabStacker.replaceFragment(dialogsFragment, null);

    }

    @OnClick(R.id.notificationsButton)
    public void notification(View v){
        Log.d("TAG21", "messages " + getCurrentTabPosition());

       EventBus.getDefault().post(new EventBusMessages.OpenNotifications());

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
    public void startSubscribers(EventBusMessages.OpenSubscribers event) {
        SubscribersFragment myFriendsFragment = SubscribersFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getUserId());
        mTabStacker.replaceFragment(myFriendsFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startPlaceSubscribers(EventBusMessages.OpenPlaceSubscribers event) {
        Log.d("TAG21", "start Place Sub...s " );
        PlaceSubscribersFragment myFriendsFragment = PlaceSubscribersFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getGroupId());
        mTabStacker.replaceFragment(myFriendsFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startUsersInPlace(EventBusMessages.OpenUsersInPlace event) {
        Log.d("TAG21", "start Place Sub...s " );
        UsersInPlaceFragment myFriendsFragment = UsersInPlaceFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getGroupId());
        mTabStacker.replaceFragment(myFriendsFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startSubscription(EventBusMessages.OpenSubscriptions event) {
        SubscriptionFragment subscriptionFragment = SubscriptionFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getUserId());
        mTabStacker.replaceFragment(subscriptionFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startUserPlaces(EventBusMessages.OpenUserPlace event) {
        UserPlacesFragment subscriptionFragment = UserPlacesFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getUserId());
        mTabStacker.replaceFragment(subscriptionFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startUserPlaces(EventBusMessages.OpenNotifications event) {
        NotificationsFragment notificationsFragment = NotificationsFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition());
        mTabStacker.replaceFragment(notificationsFragment, null);
    }



    String getFragmentName(){
        return currentTab.name() + "_" + mTabStacker.getCurrentTabSize();
    }

    public void startFriendsById(String id) {
/*        SomeoneFriendsFragment someoneFriendsFragment = new SomeoneFriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        someoneFriendsFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
   //     transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, someoneFriendsFragment);
        transaction.addToBackStack("idFriends");
        transaction.commit();*/

        SomeoneFriendsFragment someoneFriendsFragment = new SomeoneFriendsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        someoneFriendsFragment.setArguments(bundle);

        mTabStacker.replaceFragment(someoneFriendsFragment, null);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.OpenUser event){

        android.support.v4.app.Fragment profileFragment;
        if(event.getMessage().equals(SharedManager.getProperty(Constants.KEY_MY_ID))){
            profileFragment = ProfileFragment.createInstance(
                    getFragmentName(),
                    mTabStacker.getCurrentTabSize(),
                    getCurrentTabPosition());
            Log.d("TAG21", "Start my profile");
        } else {
            profileFragment = SomeoneProfileFragment.createInstance(
                    currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                    getCurrentTabPosition(),
                    event.getMessage());
            Log.d("TAG21", "Start someone");
        }
        mTabStacker.replaceFragment(profileFragment, null);

    }

    int getCurrentTabPosition(){
        return Tab.valueOf(currentTab.name()).mButtonResId;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.OpenComments event){
        CommentsFragment commentsFragment = CommentsFragment.createInstance(
                getFragmentName(),
                getCurrentTabPosition(),
                event.getPostId(),
                event.getOwnerId(),
                event.getType());
        mTabStacker.replaceFragment(commentsFragment, null);
    }

    EventBusMessages.OpenPlace openPlace;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.OpenPlace event){
        PlaceFragment placeFragment = PlaceFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getId());
        mTabStacker.replaceFragment(placeFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlacePhoto(EventBusMessages.OpenPlacePhoto event){
        FeedPlacesCheckinFragment placeFragment = FeedPlacesCheckinFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getPlaceId());
        mTabStacker.replaceFragment(placeFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPhotoReportContent(EventBusMessages.OpenPhotoReportContent event){
        FeedPhotoReportFragmentContent placeFragment = FeedPhotoReportFragmentContent.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getPlaceId(),
                event.getAlbumId(),
                event.getAlbumName(),
                event.getAlbumDate(),
                event.getPosition());
        mTabStacker.replaceFragment(placeFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void asdfasdfdas(EventBusMessages.OpenPhotoReport event){
        PhotoReportFragment photoReportFragment = PhotoReportFragment.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                getCurrentTabPosition(),
                event.getAlbum().getId(),
                event.getAlbum().getTitle(),
                event.getAlbum().getDateCreated());
        mTabStacker.replaceFragment(photoReportFragment, null);
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
        if (!mTabStacker.onBackPressed()) {
            super.onBackPressed();
        }
    }

    /*   @Override
       public void onBackPressed() {
           Log.d("TAG", "BaCk");
          // closefragment();
           super.onBackPressed();
       }*/


    /*    private void setIndicator(ImageView button){
        topButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        placesButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        searchButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        feedButton.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
        button.setColorFilter(getResources().getColor(R.color.colorAccent));
    }*/
}
