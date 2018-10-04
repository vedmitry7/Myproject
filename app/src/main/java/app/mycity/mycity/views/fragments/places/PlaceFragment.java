package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.PlacesResponse;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.MainActivity2;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PlacePagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceFragment extends Fragment implements TabStacker.TabStackInterface {

    @BindView(R.id.toolbarTitle)
    TextView title;

    @BindView(R.id.backButton)
    ImageView backButton;

    @BindView(R.id.placeViewPager)
    ViewPager viewPager;
    @BindView(R.id.placeTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.delivery)
    View delivery;
    @BindView(R.id.toolbarContent)
    LinearLayout toolbar;

    @BindView(R.id.place_image)
    ImageView imageView;

    @BindView(R.id.placeProgressBar)
    ConstraintLayout progressBar;

    private Place place;

    MainActivity2 activity2;

    Storage storage;

    boolean mayRestore;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_fragment, container, false);
        ButterKnife.bind(this, view);
        Log.d("TAG21", "onCreateView");
        return view;
    }



    public static PlaceFragment createInstance(String name, int tabPos, String placeId) {
        PlaceFragment fragment = new PlaceFragment();
        Log.i("TAG21", "Create PlaceFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        bundle.putInt("tabPos", tabPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    void createPagerAdapter(Place place){
       /* FragmentManager man = null;
        if(activity2.getChild()==null){
            man = getChildFragmentManager();
            activity2.setChild(man);
            Log.d("TAG21", "act child == null " + man);
        } else {
            man = activity2.getChild();
            Log.d("TAG21", "act child != null " + man);
        }

        if(viewPager != null) {
            Log.d("TAG21", "View pager != null");
        }
        if(tabLayout != null) {
            Log.d("TAG21", "Tab layout != null");
        }
        if(place != null) {
            Log.d("TAG21", "place != null");
        }
        if(getChildFragmentManager() != null) {
            Log.d("TAG21", "getChildFragmentManager() != null " + getChildFragmentManager());
        }*/

        PlacePagerAdapter adapter = new PlacePagerAdapter(getChildFragmentManager(), place, getArguments().getString("name"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
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

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Place place) {
        this.place = place;
        Log.d("TAG21", "STICK EVENT PLACE - " + place.getName());

        if(this.place != null) {
           // EventBus.getDefault().removeStickyEvent(place);
        }
      //  loadPlace(place.getPos());
    }

    void loadPlace(String placeId) {
        if(mayRestore){
            createPagerAdapter(place);
            showInfo(place);
            hideProgressBar();
        } else {
            ApiFactory.getApi().getPlaceByIds(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), placeId).enqueue(new Callback<PlacesResponse>() {
                @Override
                public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                    Log.d("TAG21", "PLACE RESPONSE ");

                    if (response.body().getResponse() != null) {
                        Log.d("TAG21", "PLACE LOADED - " + response.body().getResponse().get(0).getName());
                        place = response.body().getResponse().get(0);
                        EventBus.getDefault().postSticky(response.body().getResponse().get(0));
                        createPagerAdapter(response.body().getResponse().get(0));
                        showInfo(response.body().getResponse().get(0));
                        hideProgressBar();
                    } else {
                        Log.d("TAG21", "PLACE RESPONSE NULL");
                    }
                }

                @Override
                public void onFailure(Call<PlacesResponse> call, Throwable t) {
                    Log.d("TAG21", "PLACE RESPONSE FAIL " + t.getLocalizedMessage());
                    Log.d("TAG21", "PLACE RESPONSE FAIL " + t.getCause());
                }
            });
        }
    }

    void hideProgressBar(){
        Log.d("TAG21", "HIDE PROGRESS BAR");
        progressBar.setVisibility(View.GONE);
    }

    /*
    D: onCreateView
D: onViewCreated
D: STICK EVENT PLACE - Чайхана №1 HOUSE
    Place pager Init
D: PLACE INFO - Чайхана №1 HOUSE
D: 0 = 0
D: MAP READY
D: RESPONSE FEED OK
D: post size - 18
    update Photo recycler [app.mycity.mycity.api.model.Post@feb3fd0, app.mycity.mycity.api.model.Post@f7bbbc9, app.mycity.mycity.api.model.Post@15c62ce, app.mycity.mycity.api.model.Post@3c505ef, app.mycity.mycity.api.model.Post@88c88fc, app.mycity.mycity.api.model.Post@47b4b85, app.mycity.mycity.api.model.Post@75fddda, app.mycity.mycity.api.model.Post@a06120b, app.mycity.mycity.api.model.Post@d9738e8, app.mycity.mycity.api.model.Post@82a1b01, app.mycity.mycity.api.model.Post@1edfda6, app.mycity.mycity.api.model.Post@a01c3e7, app.mycity.mycity.api.model.Post@81afb94, app.mycity.mycity.api.model.Post@49c663d, app.mycity.mycity.api.model.Post@dec4e32, app.mycity.mycity.api.model.Post@a1cb783, app.mycity.mycity.api.model.Post@763d00, app.mycity.mycity.api.model.Post@6ee2939]
D: 0 = 0
D: 0 = 0
*/

    void showInfo(Place place){
        Picasso.get().load(place.getPhoto780()).into(imageView);
        title.setText(place.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG21", "onViewCreated");

        Util.indicateTabImageView(getContext(), view, getArguments().getInt("tabPos"));
        Util.setOnTabClick(view);

        final LinearLayout layout = view.findViewById(R.id.toolbarContent);
        layout.setVisibility(View.VISIBLE);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
              //      Log.d("TAG21", String.valueOf(verticalOffset));
              //      Log.d("TAG21", String.valueOf(verticalOffset) + " collapsed");
                    title.setTextColor(Color.BLACK);
                    layout.setVisibility(View.VISIBLE);
                    delivery.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(Color.WHITE);

                    backButton.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                } else if (verticalOffset == 0) {
               //     Log.d("TAG21", String.valueOf(verticalOffset) + " = 0");
                    layout.setVisibility(View.VISIBLE);
                    title.setTextColor(Color.WHITE);
                    backButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                    // Expanded
                } else {
                    // Somewhere in between
                   // Log.d("TAG21", String.valueOf(verticalOffset) + " between");
                    delivery.setVisibility(View.VISIBLE);
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });

        loadPlace(getArguments().getString("placeId"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity2 = (MainActivity2) context;
        storage = (Storage) context;
        place = (Place) storage.getDate(getArguments().get("name")+ "_place");

        if(place!=null){
            mayRestore = true;
        }
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {
        Log.d("TAG21", "Place : onTabFragmentPresented " + presentReason.name());
    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {
        Log.d("TAG21", "Place : onTabFragmentDismissed " + dismissReason.name());
        if(dismissReason == TabStacker.DismissReason.REPLACED){
            Log.d("TAG21", "Place : onTabFragmentDismissed - save place");
            storage.setDate(getArguments().get("name") + "_place", place);
        }

        if(dismissReason == TabStacker.DismissReason.BACK){
            storage.remove(getArguments().get("name")+ "_place");

            storage.remove(getArguments().get("name")+ "_eventsPostList");
            storage.remove(getArguments().get("name")+ "_eventsGroups");

            storage.remove(getArguments().get("name")+ "_albumsList");
            storage.remove(getArguments().get("name")+ "_mapAlbums");

            storage.remove(getArguments().get("name")+ "_postList");
            storage.remove(getArguments().get("name")+ "_profiles");
        }
    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }

    @OnClick(R.id.placeSubscribersCount)
    public void openSubscribers(View v){
        Log.d("TAG21", "Place : Sub...s " );
        EventBus.getDefault().post(new EventBusMessages.OpenPlaceSubscribers(place.getId()));
    }

    @OnClick(R.id.usersInPlace)
    public void openUsersInPlace(View v){
        Log.d("TAG21", "Place : Sub...s " );
        EventBus.getDefault().post(new EventBusMessages.OpenUsersInPlace(place.getId()));
    }
}
