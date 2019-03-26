package app.mycity.mycity.views.fragments.feed;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.FeedPagerAdapter;
import app.mycity.mycity.views.adapters.SearchRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class FeedFragment extends Fragment implements TabStacker.TabStackInterface{


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;
    @BindView(R.id.toolBarTitle)
    TextView title;

    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.searchButton)
    ImageView searchButton;
    @BindView(R.id.searchResultRecyclerView)
    RecyclerView searchResultRecyclerView;
    @BindView(R.id.searchContainer)
    ConstraintLayout searchContainer;

    FeedPagerAdapter pagerAdapter;

    SearchRecyclerAdapter searchRecyclerAdapter;
    List<String> searchResult = new ArrayList<>();
    List<String> groupImage = new ArrayList<>();

    Storage storage;
    private Integer totalSearchCount;

    EditText searchEditText;
    private boolean dontSearch;
    private boolean searched;
    private String searchText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public static FeedFragment createInstance(String name) {
        FeedFragment fragment = new FeedFragment();
        Log.i("TAG21", "Create FeedFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.searchButton)
    public void search(View v){
        title.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.ClickItem event) {
        Log.d("TAG23", "Place click" );
        dontSearch = true;
        searched = true;
        searchText = searchResult.get(event.getPosition());
        pagerAdapter.getAllCheckinFragment().filter(searchResult.get(event.getPosition()));
        pagerAdapter.getSubscriptionsCheckinFragment().filter(searchResult.get(event.getPosition()));
        searchContainer.setVisibility(View.GONE);
        searchEditText.setText(searchResult.get(event.getPosition()));
        App.closeKeyboard(getContext());
    }

    @OnClick(R.id.backButton)
    public void sadsa(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().post(new EventBusMessages.DefaultStatusBar());

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);
        title.setText("Чекины");

        pagerAdapter = new FeedPagerAdapter(getChildFragmentManager(), getArguments().getString("name"));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        Log.d("TAG", "Start " + this.getClass().getSimpleName());

        searchRecyclerAdapter = new SearchRecyclerAdapter(searchResult);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(searchRecyclerAdapter);

        Log.d("TAG24", "set listener");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("TAG24", "sublime " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TAG24", "change " + newText);
                if(newText.length()==0)
                    return true;

                if(newText.equals(searchText)){
                    Log.d("TAG24", "eq...");
                    return true;
                }

                if(!dontSearch){
                    loadPlaces(0, 0, newText, "rate");
                } else {
                    dontSearch = false;
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d("TAG24", "Close " );
                searchView.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                searchContainer.setVisibility(View.GONE);
                if(searched){
                    pagerAdapter.getSubscriptionsCheckinFragment().filter("");
                    pagerAdapter.getAllCheckinFragment().filter("");
                    searched = false;
                }

                //   searchView.setIconified(true);
                return true;
            }
        });
    }

    private void loadPlaces(final int offset, int category, String search, String order) {
        ApiFactory.getApi().getPlaces(App.accessToken(), offset, App.chosenCity(), category, order, search, 0).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, retrofit2.Response<ResponseContainer<ResponsePlaces>> response) {
                if(response.body()!=null){
                    totalSearchCount = response.body().getResponse().getCount();
                    if(response.body().getResponse().getItems().size()==0){
                        Log.d("TAG21", "Places size НОООООООООООООООООООООЛЬ!" );
                    } else {
                        Log.d("TAG21", "Places size не НОООООООООООООООООООООЛЬ!" );
                        searchResult.clear();
                        groupImage.clear();
                        for (Place p:response.body().getResponse().getItems()
                                ) {
                            searchResult.add(p.getName());
                            groupImage.add(p.getPhoto130());
                            Log.d("TAG21", "Name place "  + p.getName());
                        }
                        searchRecyclerAdapter.update2(searchResult, groupImage);
                        searchContainer.setVisibility(View.VISIBLE);
                        //searchRecyclerAdapter.update(searchResult);
                    }
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {
                Log.d("TAG21", "places fail "  + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "Attach " + this.getClass().getSimpleName());
        // fragmentManager = ((MainActivity2) context).getSupportFragmentManager();
        storage = (Storage) context;
    }

    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume " + this.getClass().getSimpleName());
        // Log.i("TAG21","Friends Fragment resume - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG","Friends fragment resume");
    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());
        Log.i("TAG","Friends fragment pause");
    }

    public void onStop() {
        super.onStop();
        Log.d("TAG", "Stop " + this.getClass().getSimpleName());
        EventBus.getDefault().unregister(this);

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

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

        if(dismissReason == TabStacker.DismissReason.BACK){

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.remove(getArguments().get("name") + "_postList_" + "all");
                    storage.remove(getArguments().get("name") + "_postListTotalCount_" + "all");
                    storage.remove(getArguments().get("name") + "_profiles_" + "all");
                    storage.remove(getArguments().get("name") + "_groups_" + "all");
                    storage.remove(getArguments().get("name") + "_scrollPosition_" + "all");

                    storage.remove(getArguments().get("name") + "_postList_" + "subscriptions");
                    storage.remove(getArguments().get("name") + "_postListTotalCount_" + "subscriptions");
                    storage.remove(getArguments().get("name") + "_profiles_" + "subscriptions");
                    storage.remove(getArguments().get("name") + "_groups_" + "subscriptions");
                    storage.remove(getArguments().get("name") + "_scrollPosition_" + "subscriptions");

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
}
