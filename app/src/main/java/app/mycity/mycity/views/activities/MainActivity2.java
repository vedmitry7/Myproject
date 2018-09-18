package app.mycity.mycity.views.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.HashMap;
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
import app.mycity.mycity.views.fragments.FeedFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscribersFragment;
import app.mycity.mycity.views.fragments.LongListFragment;
import app.mycity.mycity.views.fragments.subscribers.SomeoneFriendsFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscriptionFragment;
import app.mycity.mycity.views.fragments.places.PlaceFragment;
import app.mycity.mycity.views.fragments.places.PlacesFragment;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
import app.mycity.mycity.views.fragments.settings.MainSettingsFragment;
import app.mycity.mycity.views.fragments.top.TopFragment;
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

    @BindView(R.id.main_act_top_button)     ImageView topButton;
    @BindView(R.id.main_act_places_button)  ImageView placesButton;
    @BindView(R.id.main_act_search_button)  ImageView searchButton;
    @BindView(R.id.main_act_feed_button)    ImageView feedButton;

    @BindView(R.id.main_act_profile_button) ImageView profileButton;

    @BindView(R.id.main_act_messages_container)         RelativeLayout messageButton;
    @BindView(R.id.main_act_notification_container)     RelativeLayout notificattionButton;

    private TabStacker mTabStacker;

    String currentTab;

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

    private enum Tab {
        TAB_TOP(R.id.main_act_top_button),
        TAB_PLACES(R.id.main_act_places_button),
        TAB_PROFILE(R.id.main_act_profile_button),
        TAB_SEARCH(R.id.main_act_search_button),
        TAB_FEED(R.id.main_act_feed_button);

        private int mButtonResId;

        Tab(@IdRes int buttonResId) {
            mButtonResId = buttonResId;
        }
    }

    android.support.v4.app.FragmentManager fragmentManager;

    ProfileFragment profileFragment;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));

        updateTimestemp();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainActivity2.this, TestService.class);
                startService(serviceIntent);

            }
        }, 500);

        ButterKnife.bind(this);

        setIndicator(profileButton);

        fragmentManager = getSupportFragmentManager();

        mTabStacker = new TabStacker(getSupportFragmentManager(), R.id.main_act_fragment_container);



        if (savedInstanceState == null) {
            // new Activity: creates the first Tab
            selectTab(Tab.TAB_FEED);
        } else {
            // restoring Activity: restore the TabStacker, and select the saved selected tab
      /*      mTabStacker.restoreInstance(savedInstanceState);
            Tab selectedTab = Tab.valueOf(mTabStacker.getCurrentTabName());
            selectTab(selectedTab);*/


            selectTab(Tab.TAB_FEED);

        }

        //Tab click listener
        for (final Tab tab : Tab.values()) {
            final ImageView button = findViewById(tab.mButtonResId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickOnTab(tab);
                }
            });
        }

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

        updateButtonStates(clickedTab);

        // switch to Tab Stack
        String tabName = clickedTab.name();
        currentTab = clickedTab.name();

        if (!mTabStacker.switchToTab(tabName)) {    // tries to switch to the TAB STACK
            // no fragment yet on this stack -> push the 1st fragment of the stack

            android.support.v4.app.Fragment fragment = null;

            switch (clickedTab){
                case TAB_TOP:
                    fragment = new TopFragment();
                    break;
                case TAB_PLACES:
                    fragment = new PlacesFragment();
                    break;
                case TAB_PROFILE:
                    fragment = ProfileFragment.createInstance(tabName + "_" + mTabStacker.getCurrentTabSize());
                    break;
                case TAB_SEARCH:
                    fragment = new FeedFragment();
                    break;
                case TAB_FEED:
                    fragment = new FeedFragment();
                    break;
            }

            mTabStacker.replaceFragment(fragment, null);  // no animation

        }
    }

    // Update Button state (white / black)
    private void updateButtonStates(Tab clickedTab) {
        for(final Tab tab : Tab.values()) {
            ImageView button = findViewById(tab.mButtonResId);
            if(tab == clickedTab){
                button.setColorFilter(getResources().getColor(R.color.colorAccent));
            } else {
                button.setColorFilter(getResources().getColor(R.color.colorDefaultButton));
            }
        }
    }

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
        Log.d("TAG21", "PHOTO - ");
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    @OnClick(R.id.main_act_top_button_container)
    public void top(View v){
        setIndicator(topButton);
    }

    @OnClick(R.id.main_act_places_button_container)
    public void places(View v){
   /*     setIndicator(placesButton);
        PlacesFragment placesFragment = new PlacesFragment();
        mTabStacker.replaceFragment(placesFragment, null);
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
      //  transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.add(R.id.main_act_fragment_container, placesFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
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
       /* setIndicator(feedButton);
        FeedFragment feedFragment = new FeedFragment();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
       // transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, feedFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    @OnClick(R.id.main_act_profile_button_container)
    public void profile(View v){
   /*     profileFragment = new ProfileFragment();

        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.add(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack("myProfile");
        transaction.commit();

        clearFragmentStack();*/
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
    public void startSubscribers(EventBusMessages.OpenSubscribers event) {
        SubscribersFragment myFriendsFragment = SubscribersFragment.createInstance(currentTab + "_" + mTabStacker.getCurrentTabSize(), event.getUserId());
        mTabStacker.replaceFragment(myFriendsFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startSubscribers(EventBusMessages.OpenSubscriptions event) {
        SubscriptionFragment subscriptionFragment = SubscriptionFragment.createInstance(currentTab + "_" + mTabStacker.getCurrentTabSize(), event.getUserId());
        mTabStacker.replaceFragment(subscriptionFragment, null);
    }

    String getFragmentName(){
        return currentTab + "_" + mTabStacker.getCurrentTabSize();
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
        SomeoneProfileFragment profileFragment = SomeoneProfileFragment.createInstance(currentTab + "_" + mTabStacker.getCurrentTabSize(), event.getMessage());


        mTabStacker.replaceFragment(profileFragment, null);
    /*    android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //    transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, profileFragment);
        transaction.addToBackStack("someoneProfile");
        transaction.commit();*/
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

    EventBusMessages.OpenPlace openPlace;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.OpenPlace event){

        PlaceFragment placeFragment = new PlaceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("placeId", event.getId());
        bundle.putString("photo780", event.getPhoto780());
        bundle.putString("name", event.getName());
        placeFragment.setArguments(bundle);
        mTabStacker.replaceFragment(placeFragment, null);


/*        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        //    transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.main_act_fragment_container, placeFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/

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

    /*   @Override
       public void onBackPressed() {
           Log.d("TAG", "BaCk");
          // closefragment();
           super.onBackPressed();
       }*/


    @Override
    public void onBackPressed() {
        if (!mTabStacker.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
