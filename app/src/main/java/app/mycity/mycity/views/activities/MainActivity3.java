package app.mycity.mycity.views.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
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

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.LocationService;
import app.mycity.mycity.SocketService;
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
import app.mycity.mycity.views.fragments.MenuFragment;
import app.mycity.mycity.views.fragments.NotificationsFragment;
import app.mycity.mycity.views.fragments.events.ActionContentFragment;
import app.mycity.mycity.views.fragments.events.AllEvents;
import app.mycity.mycity.views.fragments.events.EventContentFragment;
import app.mycity.mycity.views.fragments.events.EventsFragment;
import app.mycity.mycity.views.fragments.feed.ChronicsFragment;
import app.mycity.mycity.views.fragments.feed.FeedFragment;
import app.mycity.mycity.views.fragments.feed.FeedPhotoReportFragmentContent;
import app.mycity.mycity.views.fragments.feed.FeedPhotoReportFragmentContent2;
import app.mycity.mycity.views.fragments.feed.FeedPhotoReportFragmentContentNew;
import app.mycity.mycity.views.fragments.feed.FeedPlacesCheckinFragment;
import app.mycity.mycity.views.fragments.feed.FeedPlacesCheckinFragmentNew;
import app.mycity.mycity.views.fragments.feed.FeedPlacesCheckinFragmentNew2;
import app.mycity.mycity.views.fragments.feed.PhotoReportFragment;
import app.mycity.mycity.views.fragments.places.PlaceFragment;
import app.mycity.mycity.views.fragments.places.PlaceSubscribersFragment;
import app.mycity.mycity.views.fragments.places.PlacesFragment;
import app.mycity.mycity.views.fragments.places.UsersInPlaceFragment;
import app.mycity.mycity.views.fragments.profile.ProfileCheckinContent;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import app.mycity.mycity.views.fragments.profile.SomeoneProfileFragment;
import app.mycity.mycity.views.fragments.profile.UserPlacesFragment;
import app.mycity.mycity.views.fragments.settings.MainSettingsFragment;
import app.mycity.mycity.views.fragments.subscribers.SomeoneFriendsFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscribersFragment;
import app.mycity.mycity.views.fragments.subscribers.SubscriptionFragment;
import app.mycity.mycity.views.fragments.top.SuperPeoplesFragment;
import butterknife.ButterKnife;
import fr.arnaudguyon.tabstacker.TabStacker;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity3 extends AppCompatActivity implements MainAct, Storage {

    private TabStacker mTabStacker;
    Tab currentTab;
    HashMap<String, Object> date =new HashMap<>();
    android.support.v4.app.FragmentManager fragmentManager;
    private enum Tab {
        TAB_MENU(0);
        private int mButtonResId;
        Tab(int buttonResId) {
            mButtonResId = buttonResId;
        }
    }

    private enum CurrentFragment {
        MENU_FRAGMENT,
        PROFILE_FRAGMENT,
        MESSAGES_FRAGMENT,
        NOTIFICATION_FRAGMENT
    }

    CurrentFragment currentFragment;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_activity_3);

        setStatusBarColor();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));

        updateTimestemp();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainActivity3.this, SocketService.class);
                serviceIntent.putExtra("data", "activity");
                startService(serviceIntent);
            }
        }, 500);
        if(hasPermissionLocation()) {
            startLocationService();
        }


        ButterKnife.bind(this);

        fragmentManager = getSupportFragmentManager();
        mTabStacker = new TabStacker(getSupportFragmentManager(), R.id.main_act_fragment_container);

        openMenuFragment(new EventBusMessages.OpenMenu());

        currentTab = Tab.values()[0];

   /*     String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }*/
    }

    public void setStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If both system bars are black, we can remove these from our layout,
            // removing or shrinking the SurfaceFlinger overlay required for our views.
            Window window = getWindow();
            int statusBarColor = ContextCompat.getColor(this,R.color.colorAccent);

            if (statusBarColor == Color.BLACK && window.getNavigationBarColor() == Color.BLACK) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    private void startLocationService(){
        Intent locServiceIntent = new Intent(MainActivity3.this, LocationService.class);
        startService(locServiceIntent);
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
                case TAB_MENU:
                    fragment = new MenuFragment();
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
                    Toast.makeText(MainActivity3.this, response.body().getResponse().getTs(), Toast.LENGTH_SHORT).show();
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

    private void makePlaceRequestAndMakeCheckin(){
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


        //pause to wait answer from location service
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG23", "get -- ");
                Log.d("TAG23", "lat" +  SharedManager.getProperty("latitude"));
                Log.d("TAG23", "long" +  SharedManager.getProperty("longitude"));

                String lat = SharedManager.getProperty("latitude");
                String lon = SharedManager.getProperty("longitude");

                if(SharedManager.getProperty("latitude")==null){
                    lat = "48.5685831";
                    lon = "39.2961263";
                }

                ApiFactory.getApi().getPlaceByCoordinates(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), lat, lon, 300).enqueue(new Callback<ResponseContainer<ResponsePlaces>>() {
                    @Override
                    public void onResponse(Call<ResponseContainer<ResponsePlaces>> call, Response<ResponseContainer<ResponsePlaces>> response) {
                        Log.d("TAG23", "response");
                        if(response.body()!=null && response.body().getResponse().getItems()!=null){
                            Log.d("TAG23", "response ok");
                            progress.setVisibility(View.GONE);
                            if(response.body().getResponse().getCount()==0){
                                message.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            placeList.addAll(response.body().getResponse().getItems());
                            Log.d("TAG23", "Places size" + response.body().getResponse().getItems().size());
                            adapter.update(placeList);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {
                        Log.d("TAG23", "response fail " + t.getLocalizedMessage());
                    }
                });
            }
        }, 1000);

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

                    Intent intent = new Intent(MainActivity3.this, FilterImageActivity.class);
                    startActivity(intent);
                    Dexter.withActivity(MainActivity3.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.LOCATION_HARDWARE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {

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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.MakeCheckin event){

        EventBus.getDefault().post(new EventBusMessages.UpdateCoordinates());

        if(hasPermissionWriteExternalStorageAndLocation()){
            Log.d("TAG21", "HAS All PERMISSIONS");
            makePlaceRequestAndMakeCheckin();
        } else {
            Log.d("TAG21", "HAS NOT ALL PERMISSIONS");
        }
        Log.d("TAG21", "PHOTO - ");
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 14:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d("TAG21", "take granted");
                    //  readContacts();
                } else {
                    Log.d("TAG21", "take not granted");
                    // permission denied
                }
                return;
        }
    }*/

    public boolean hasPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 13);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public boolean hasPermissionLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 15);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public boolean hasPermissionWriteExternalStorageAndLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ) {
                return true;
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 13);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 19);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("TAG21", "onRequestPermissionsResult  " + requestCode);
        switch (requestCode) {

            case 15:
                startLocationService();
                break;

            case 13: {
                Log.i("TAG21", "case  13 ");
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Good!", Toast.LENGTH_SHORT).show();
                    makePlaceRequestAndMakeCheckin();
                } else {
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED
                            && grantResults[1] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this, "All bad!", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Для того чтобы сделать чекин вам нужно дать разрешение на определение местоположения устройства и предоставить доступ к карте памяти");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Настройки", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToSettings();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            Toast.makeText(this, "Storage bad!", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Для того чтобы сделать чекин вам нужно предоставить доступ к карте памяти");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("Настройки", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSettings();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        if(grantResults[1] == PackageManager.PERMISSION_DENIED){
                            Toast.makeText(this, "Location bad!", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Для того чтобы сделать чекин вам нужно дать разрешение на определение местоположения устройства");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }
            }
        }
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
                getFragmentName(),
                event.getGroupId());
        mTabStacker.replaceFragment(myFriendsFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startUsersInPlace(EventBusMessages.OpenUsersInPlace event) {
        Log.d("TAG21", "start Place Sub...s " );
        UsersInPlaceFragment myFriendsFragment = UsersInPlaceFragment.createInstance(
                getFragmentName(),
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

    String getFragmentName(){
        String s = "TAB_" + mTabStacker.getCurrentTabSize();
        Log.d("TAG23", "tab - " + s);
        return s;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.OpenPlace event){
        PlaceFragment placeFragment = PlaceFragment.createInstance(
                getFragmentName(),
                event.getId());
        mTabStacker.replaceFragment(placeFragment, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlacePhoto(EventBusMessages.OpenPlacePhoto event){
        FeedPlacesCheckinFragmentNew2 placeFragment = FeedPlacesCheckinFragmentNew2.createInstance(
                currentTab.name() + "_" + mTabStacker.getCurrentTabSize(),
                event.getPlaceId(),
                event.getPostId());
        mTabStacker.replaceFragment(placeFragment, null);
    }

    /**
     *      Open Album
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPhotoReportContent(EventBusMessages.OpenPhotoReportContent event){
        FeedPhotoReportFragmentContentNew placeFragment = FeedPhotoReportFragmentContentNew.createInstance(
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openFeed(EventBusMessages.OpenFeed event){
        mTabStacker.replaceFragment(
                FeedFragment.createInstance(getFragmentName(), getCurrentTabPosition()), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openFeed(EventBusMessages.OpenCheckins event){
        mTabStacker.replaceFragment(
                FeedFragment.createInstance(getFragmentName(), getCurrentTabPosition()), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OpenChronics(EventBusMessages.OpenChronics event){

        mTabStacker.replaceFragment(
                ChronicsFragment.createInstance(getFragmentName(), getCurrentTabPosition()), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlaces(EventBusMessages.OpenPlaces event){
        mTabStacker.replaceFragment(
                PlacesFragment.createInstance(getFragmentName()), null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPeople(EventBusMessages.OpenPeople event){
        Log.d("TAG21", "... open people");
        mTabStacker.replaceFragment(
                SuperPeoplesFragment.createInstance(getFragmentName(), mTabStacker.getCurrentTabSize(), 2), null);
    }
    /**
     *      Open Profile
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openProfile(EventBusMessages.OpenProfile event){

        Log.d("TAG21", "... open profile");
        if(!(mTabStacker.getCurrentTopFragment() instanceof ProfileFragment)){
            mTabStacker.replaceFragment(
                    ProfileFragment.createInstance(getFragmentName(), mTabStacker.getCurrentTabSize(), 0), null);
            currentFragment = CurrentFragment.PROFILE_FRAGMENT;
        }
    }
        /**
     *      Open Profile Content
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openProfileContent(EventBusMessages.OpenCheckinProfileContent event){
        mTabStacker.replaceFragment(
                ProfileCheckinContent.createInstance(getFragmentName(), event.getPostId(), event.getStorageKey()), null);

        Log.d("TAG21", "... open profile content");

    }

    /**
     *      Open Event Content
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openEventContent(EventBusMessages.OpenEventContent event){
        mTabStacker.replaceFragment(
                EventContentFragment.createInstance(getFragmentName(), event.getEventId(), event.getOwnerId(), event.getPlaceName()), null);

        Log.d("TAG21", "... open profile content " + event.getPlaceName());

    }


    /**
     *      Open Action Content
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openActionContent(EventBusMessages.OpenActionContent event){
        mTabStacker.replaceFragment(
                ActionContentFragment.createInstance(getFragmentName(), event.getEventId(), event.getOwnerId(), event.getPlaceName()), null);

        Log.d("TAG21", "... open profile content " + event.getPlaceName());

    }


    /**
     *      Open Chat
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openChat(EventBusMessages.OpenChat event){
        Log.d("TAG21", "... open chat");
        if(!(mTabStacker.getCurrentTopFragment() instanceof DialogsFragment)){
            mTabStacker.replaceFragment(
                    DialogsFragment.createInstance(getFragmentName(), getCurrentTabPosition()), null);
            currentFragment = CurrentFragment.MESSAGES_FRAGMENT;
        }
    }
    /**
     *      Open Notification
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openNotifications(EventBusMessages.OpenNotifications event) {
        Log.d("TAG21", "... open notif");
        if(!(mTabStacker.getCurrentTopFragment() instanceof NotificationsFragment)){
            Log.d("TAG21", "not inst create");
            mTabStacker.replaceFragment(NotificationsFragment.createInstance(
                    currentTab.name() + "_" + mTabStacker.getCurrentTabSize(), getCurrentTabPosition()), null);
        }
    }
    /**
     *      Open Menu
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openMenuFragment(EventBusMessages.OpenMenu event){
        if(!(mTabStacker.getCurrentTopFragment() instanceof MenuFragment))
            mTabStacker.replaceFragment(
                    new MenuFragment(), null);
        currentFragment = CurrentFragment.MENU_FRAGMENT;
        mTabStacker.popToTop(true);
    }

    /**
     *      Open Settings
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startSettings(EventBusMessages.MainSettings event){
            mTabStacker.replaceFragment(
                    new MainSettingsFragment(), null);
        currentFragment = CurrentFragment.MENU_FRAGMENT;
    }

    /**
     *      Open Events
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startEvents(EventBusMessages.OpenEvents event){
        mTabStacker.replaceFragment(
                EventsFragment.createInstance(getFragmentName()), null);
        currentFragment = CurrentFragment.MENU_FRAGMENT;
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
    public Object getDate(String key) {
        Log.i("TAG23", "GET DATE " + key );
        if(date.containsKey(key)){
            Log.i("TabFragment", "contains");
            return date.get(key);
        }
        return null;
    }

    @Override
    public void setDate(String key, Object date) {
        Log.i("TAG23", "SAVE " + key);
        this.date.put(key, date);
    }

    @Override
    public void remove(String key) {
        Log.i("TAG23", "REMOVE " + key);
        date.remove(key);
    }


    @Override
    public void onBackPressed() {
        Log.i("TAG21", "                                        name " + mTabStacker.getCurrentTabName() );
        if (!mTabStacker.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
