package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.PlacesResponse;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
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

    @BindView(R.id.ratingCount)
    TextView ratingCount;

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

    @BindView(R.id.loinLeaveButton)
    ImageView joinLeaveButton;

    @BindView(R.id.placeProgressBar)
    ConstraintLayout progressBar;

    @BindView(R.id.simpleRatingBar)
    ScaleRatingBar ratingBar;

    @BindView(R.id.placeSubscribersCount)
    TextView placeSubscribersCount;

    @BindView(R.id.usersInPlace)
    TextView usersInPlace;

    private Place place;

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

    public static PlaceFragment createInstance(String name, String placeId) {
        PlaceFragment fragment = new PlaceFragment();
        Log.i("TAG21", "Create PlaceFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    void createPagerAdapter(Place place){
        PlacePagerAdapter adapter = new PlacePagerAdapter(getChildFragmentManager(), place, getArguments().getString("name"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG21", "click ");
            }
        });
    }

    @OnClick(R.id.rateButton)
    public void setRating(View v){
        Log.d("TAG21", "click rating");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Оценка заведения");

        View view = getActivity().getLayoutInflater().inflate(R.layout.rating_dialog, null);
        builder.setView(view);

        final ScaleRatingBar serviceRatingBar = view.findViewById(R.id.serviceRatingBar);
        final ScaleRatingBar qualityRatingBar = view.findViewById(R.id.qualityRatingBar);
        final ScaleRatingBar priceRatingBar = view.findViewById(R.id.priceRatingBar);
        final ScaleRatingBar interiorRatingBar = view.findViewById(R.id.interiorRatingBar);

        TextView serviceCount = view.findViewById(R.id.serviceCount);
        serviceCount.setText("(" + place.getRate().getService().getCount() + ")");

        TextView qualityCount = view.findViewById(R.id.qualityCount);
        qualityCount.setText("(" + place.getRate().getQuality().getCount() + ")");

        TextView priceCount = view.findViewById(R.id.priceCount);
        priceCount.setText("(" + place.getRate().getPrice().getCount() + ")");

        TextView interiorCount = view.findViewById(R.id.interiorCount);
        interiorCount.setText("(" + place.getRate().getInterior().getCount() + ")");
        final boolean[] wasChanges = new boolean[4];

        int service = (int) place.getRate().getService().getValue();
        int quality = (int) place.getRate().getQuality().getValue();
        int price = (int) place.getRate().getPrice().getValue();
        int interior = (int) place.getRate().getInterior().getValue();

        serviceRatingBar.setRating(place.getRate().getService().getValue());
        qualityRatingBar.setRating(place.getRate().getQuality().getValue());
        priceRatingBar.setRating(place.getRate().getPrice().getValue());
        interiorRatingBar.setRating(place.getRate().getInterior().getValue());

        BaseRatingBar.OnRatingChangeListener listener = new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                switch (baseRatingBar.getId()){
                    case R.id.serviceRatingBar:
                        wasChanges[0] = true;
                        Log.d("TAG21", "ser");
                        break;
                    case R.id.qualityRatingBar:
                        wasChanges[1] = true;
                        Log.d("TAG21", "qua");
                        break;
                    case R.id.priceRatingBar:
                        wasChanges[2] = true;
                        Log.d("TAG21", "pri");
                        break;
                    case R.id.interiorRatingBar:
                        Log.d("TAG21", "int");
                        wasChanges[3] = true;
                        break;
                }
                Log.d("TAG21", "base - " + baseRatingBar.getRating() + " / v - " + v);
            }
        };

        serviceRatingBar.setOnRatingChangeListener(listener);
        qualityRatingBar.setOnRatingChangeListener(listener);
        priceRatingBar.setOnRatingChangeListener(listener);
        interiorRatingBar.setOnRatingChangeListener(listener);


        final Callback callback = new Callback<ResponseContainer<Success>>() {
            @Override
            public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                Log.d("TAG21", "resp");

            }

            @Override
            public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {
                Log.d("TAG21", "fail");

            }
        };

        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Log.d("TAG21", "ok");

                if(wasChanges[0]){
                    Log.d("TAG21", "send 0 " + (int)place.getRate().getService().getValue());
                    ApiFactory.getApi().rate(App.accessToken(), place.getId(), "service",
                            (int)serviceRatingBar.getRating())
                            .enqueue(callback);
                }
                if(wasChanges[1]){
                    Log.d("TAG21", "send 1 " +  (int)place.getRate().getQuality().getValue());
                    ApiFactory.getApi().rate(App.accessToken(), place.getId(), "quality",
                            (int)qualityRatingBar.getRating())
                            .enqueue(callback);
                }
                if(wasChanges[2]){
                    Log.d("TAG21", "send 2");
                    ApiFactory.getApi().rate(App.accessToken(), place.getId(), "price",
                            (int)priceRatingBar.getRating())
                            .enqueue(callback);
                }
                if(wasChanges[3]){
                    Log.d("TAG21", "send 3");
                    ApiFactory.getApi().rate(App.accessToken(), place.getId(), "interior",
                            (int)interiorRatingBar.getRating())
                            .enqueue(callback);
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }


    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
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
                        ratingBar.setRating(place.getRate().getAll().getValue());
                        ratingCount.setText("(" + place.getRate().getAll().getCount() + ")");
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

    void showInfo(Place place){
        Picasso.get().load(place.getCover1250()).into(imageView);
        title.setText(place.getName());
        placeSubscribersCount.setText("" + place.getCountMembers());
        usersInPlace.setText("Сейчас в заведении: " + place.getCountMembersInPlace());

        if(place.getIsMember()==1){
            Log.d("TAG21", "MEMBER ");
            joinLeaveButton.setImageResource(R.drawable.ic_delete_subscription);
        } else {
            Log.d("TAG21", "NOT MEMBER " );
            joinLeaveButton.setImageResource(R.drawable.ic_add_subscription);
        }
    }

    @OnClick(R.id.loinLeaveButton)
    public void dfkop(View v){
        if(place.getIsMember()==1){
            Log.d("TAG21", "LEAVE group " );
            ApiFactory.getApi().leaveGroup(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), place.getId()).enqueue(new Callback<ResponseContainer<Success>>() {
                @Override
                public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                    if(response.body()!=null){
                        if(response.body().getResponse().getSuccess()==1){
                            place.setIsMember(0);
                            place.setCountMembers(place.getCountMembers()-1);
                            showInfo(place);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {

                }
            });

        } else {
            Log.d("TAG21", "JOIN group " );
            ApiFactory.getApi().joinToGroup(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), place.getId()).enqueue(new Callback<ResponseContainer<Success>>() {
                @Override
                public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                    if(response.body()!=null){
                        if(response.body().getResponse().getSuccess()==1){
                            place.setIsMember(1);
                            place.setCountMembers(place.getCountMembers()+1);
                            showInfo(place);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG21", "onViewCreated");

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);

        final LinearLayout layout = view.findViewById(R.id.toolbarContent);
        layout.setVisibility(View.VISIBLE);

        AppBarLayout appBarLayout = view.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    title.setTextColor(Color.BLACK);
                    layout.setVisibility(View.VISIBLE);
                    delivery.setVisibility(View.GONE);
                    toolbar.setBackgroundColor(Color.WHITE);

                    backButton.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                } else if (verticalOffset == 0) {
                    layout.setVisibility(View.VISIBLE);
                    title.setTextColor(Color.WHITE);
                    backButton.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
                } else {
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

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.remove(getArguments().get("name")+ "_place");

                    storage.remove(getArguments().get("name")+ "_eventsPostList");
                    storage.remove(getArguments().get("name")+ "_eventsGroups");
                    storage.remove(getArguments().get("name")+ "_eventsScrollPosition");

                    storage.remove(getArguments().get("name")+ "_albumsList");
                    storage.remove(getArguments().get("name")+ "_mapAlbums");

                    storage.remove(getArguments().get("name")+ "_postList");
                    storage.remove(getArguments().get("name")+ "_profiles");
                    Log.d("TAG21", "!!!!!!!!!!  remove " + getArguments().get("name")+ "_postList");
                    Log.d("TAG21", "!!!!!!!!!!  remove " + getArguments().get("name")+ "_profiles");

                    storage.remove(getArguments().get("name") + "_actionsPostList");
                    storage.remove(getArguments().get("name") + "_actionsGroups");
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
