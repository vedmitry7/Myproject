package app.mycity.mycity.views.fragments.events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
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
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.ChronicsPagerAdapter;
import app.mycity.mycity.views.adapters.EventsPagerAdapter;
import app.mycity.mycity.views.adapters.SearchRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class EventsFragment extends Fragment implements TabStacker.TabStackInterface{


    @BindView(R.id.myFriendsViewPager)
    ViewPager viewPager;
    @BindView(R.id.myFriendsTabLayout)
    TabLayout tabLayout;

    TextView search;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.searchButton)
    ImageView searchButton;


    @BindView(R.id.searchResultRecyclerView)
    RecyclerView searchResultRecyclerView;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    @BindView(R.id.searchContainer)
    ConstraintLayout searchContainer;

    SearchRecyclerAdapter searchRecyclerAdapter;
    List<String> searchResult = new ArrayList<>();
    List<String> groupImage = new ArrayList<>();

    Storage storage;
    private boolean dontSearch;
    EventsPagerAdapter pagerAdapter;
    private Integer totalSearchCount;
    private boolean searched;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static EventsFragment createInstance(String name) {
        EventsFragment fragment = new EventsFragment();
        Log.i("TAG21", "Create FeedFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.searchButton)
    public void search(View v){
        toolBarTitle.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.ClickItem event) {
        Log.d("TAG23", "Place click" );
        dontSearch = true;
        searched = true;
        pagerAdapter.getAllEventsFragment().filter(searchResult.get(event.getPosition()));
        pagerAdapter.getAllActionsFragment().filter(searchResult.get(event.getPosition()));
        searchContainer.setVisibility(View.GONE);
        search.setText(searchResult.get(event.getPosition()));
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
        toolBarTitle.setText("События");

        searchRecyclerAdapter = new SearchRecyclerAdapter(searchResult);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(searchRecyclerAdapter);

        search = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search.setTextColor(getResources().getColor(R.color.white));
        search.setHintTextColor(getResources().getColor(R.color.white));


        pagerAdapter = new EventsPagerAdapter(getChildFragmentManager(), getArguments().getString("name"));
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);


        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    App.closeKeyboard(getContext());
                    search.clearFocus();
                    return true;
                }
                return false;
            }
        });

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
                toolBarTitle.setVisibility(View.VISIBLE);
                searchButton.setVisibility(View.VISIBLE);
                searchContainer.setVisibility(View.GONE);
                if(searched) {
                    pagerAdapter.getAllEventsFragment().filter("");
                    pagerAdapter.getAllActionsFragment().filter("");
                }
                return true;
            }
        });



        Log.d("TAG", "Start " + this.getClass().getSimpleName());
    }


    private void loadPlaces(final int offset, int category, String search, String order) {
        ApiFactory.getApi().getPlaces(App.accessToken(), offset, App.chosenCity(), category, order, search, 1).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
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
                        searchContainer.setVisibility(View.VISIBLE);
                        searchRecyclerAdapter.update2(searchResult, groupImage);

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
        storage = (Storage) context;
    }


    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

        Log.i("TAG25","chronics onTabFragmentDismissed");
        if(dismissReason== TabStacker.DismissReason.BACK){
            Log.i("TAG25","chronics onTabFragmentDismissed BACK");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    storage.remove(getArguments().get("name") + "_eventsPostList");
                    storage.remove(getArguments().get("name") + "_eventsGroups");
                    storage.remove(getArguments().get("name") + "_eventsScrollPosition");
                    storage.remove(getArguments().get("name") + "_actionsPostList");
                    storage.remove(getArguments().get("name") + "_actionsGroups");
                    storage.remove(getArguments().get("name") + "_actionsScrollPosition");
                }
            }, 300);

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
