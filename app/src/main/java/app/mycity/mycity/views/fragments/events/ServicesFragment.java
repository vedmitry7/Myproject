package app.mycity.mycity.views.fragments.events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Place;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.api.model.ResponseVisit;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.AllActionRecyclerAdapter;
import app.mycity.mycity.views.adapters.SearchRecyclerAdapter;
import app.mycity.mycity.views.adapters.ServicesRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class ServicesFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    TextView search;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.searchButton)
    ImageView searchButton;

    @BindView(R.id.infoHolder)
    RelativeLayout infoHolder;

    @BindView(R.id.searchResultRecyclerView)
    RecyclerView searchResultRecyclerView;

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;

    ServicesRecyclerAdapter adapter;

    List<Post> postList;
    HashMap<String, Group> groups = new HashMap<String, Group>();

    boolean isLoading;
    int totalCount;

    Storage storage;

    @BindView(R.id.searchContainer)
    ConstraintLayout searchContainer;

    boolean mayRestore;
    private LinearLayoutManager layoutManager;
    private int position;

    String searchText;
    private boolean dontSearch;
    private Integer totalSearchCount;

    SearchRecyclerAdapter searchRecyclerAdapter;
    List<String> searchResult = new ArrayList<>();
    List<String> groupImage = new ArrayList<>();
    private boolean searched;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.services_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static ServicesFragment createInstance(String name) {
        ServicesFragment fragment = new ServicesFragment();
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
        searchContainer.setVisibility(View.GONE);
        searchText = searchResult.get(event.getPosition());
        search.setText(searchResult.get(event.getPosition()));
        postList.clear();
        loadFeed(0);

        App.closeKeyboard(getContext());
    }

    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().post(new EventBusMessages.DefaultStatusBar());

        searchRecyclerAdapter = new SearchRecyclerAdapter(searchResult);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(searchRecyclerAdapter);

        search = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search.setTextColor(getResources().getColor(R.color.white));
        search.setHintTextColor(getResources().getColor(R.color.white));

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        adapter = new ServicesRecyclerAdapter(postList, groups);

        layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > postList.size()){
                            Log.d("TAG21", "load feed ");
                            loadFeed(postList.size());
                        }
                    }
                }
            }
        };

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    App.closeKeyboard(getContext());
                    searchText = search.getText().toString();
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

                if(searched){
                    searchText = "";
                    postList.clear();
                    placeHolder.setVisibility(View.VISIBLE);
                    loadFeed(0);
                    searched = false;
                }

                return true;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        loadFeed(postList.size());
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

    private void loadFeed(int offset) {
        Log.d("TAG21", "LOAD EVENTS");

        if(mayRestore){
            adapter.update(postList, groups);
            layoutManager.scrollToPosition(position);
            mayRestore = false;
            placeHolder.setVisibility(View.GONE);
        } else {
            ApiFactory.getApi().getAllServices(App.accessToken(), App.chosenCity(), searchText,"1", offset).enqueue(new retrofit2.Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseWall>> call, retrofit2.Response<ResponseContainer<ResponseWall>> response) {
                    Log.d("TAG21", "RESPONSE EVENTS");
                    if(response!=null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE ACTION OK");


                        if(response.body().getResponse().getCount() == 0){
                            infoHolder.setVisibility(View.VISIBLE);
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        placeHolder.setVisibility(View.GONE);

                        totalCount = response.body().getResponse().getCount();
                        Log.d("TAG21", "RESPONSE ACTION OK " + totalCount);

                        if(totalCount>0){
                            postList.addAll(response.body().getResponse().getItems());
                            for (Group g: response.body().getResponse().getGroups()
                                    ) {
                                groups.put(g.getId(), g);
                            }
                        }

                        Log.d("TAG21", "Actions size - " + postList.size() + " total - " + totalCount);
                        isLoading = false;

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR ");
                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                    Log.d("TAG21", "RESPONSE EVENTS " + t.getLocalizedMessage());

                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event) {
        Log.d("TAG21", "Like itemId - " + event.getItemId() + " owner - " +  postList.get(event.getAdapterPosition()).getOwnerId().toString());

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "event",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "event",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(1);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void visit(final EventBusMessages.AddVisitor event) {
        if (postList.get(event.getAdapterPosition()).getVisits().getUserVisit() == 1) {
            Log.d("TAG21", "unvisit");
            ApiFactory.getApi().removeVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getVisits().setCount(response.body().getResponse().getVisitors());
                        postList.get(event.getAdapterPosition()).getVisits().setUserVisit(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (postList.get(event.getAdapterPosition()).getVisits().getUserVisit() == 0) {
            Log.d("TAG21", "Visit");
            ApiFactory.getApi().addVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        postList.get(event.getAdapterPosition()).getVisits().setCount(response.body().getResponse().getVisitors());
                        postList.get(event.getAdapterPosition()).getVisits().setUserVisit(1);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        storage = (Storage) context;

        postList = (List<Post>) storage.getDate(getArguments().get("name")+ "_servicesPostList");
        groups = (HashMap<String, Group>) storage.getDate(getArguments().get("name")+ "_servicesGroups");


        if(postList==null){
            Log.d("TAG21", "restore null");
            postList = new ArrayList<>();
            groups = new HashMap();
        } else {
            Log.d("TAG21", "restore ok - " + postList.size());
            position = (int) storage.getDate(getArguments().getString("name") + "_servicesScrollPosition");
            mayRestore = true;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

        if(dismissReason== TabStacker.DismissReason.REPLACED){
            if(storage!=null){
                storage.setDate(getArguments().getString("name") + "_servicesPostList", postList);
                storage.setDate(getArguments().getString("name") + "_servicesGroups", groups);
                storage.setDate(getArguments().getString("name") + "_servicesScrollPosition", layoutManager.findFirstVisibleItemPosition());
            }
        }
        if(dismissReason== TabStacker.DismissReason.BACK){
            if(storage!=null){
                storage.remove(getArguments().getString("name") + "_servicesPostList");
                storage.remove(getArguments().getString("name") + "_servicesGroups");
                storage.remove(getArguments().getString("name") + "_servicesScrollPosition");
            }
        }
    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }

    @Override
    public void onRefresh() {
        postList = new ArrayList<>();
        loadFeed(0);
        placeHolder.setVisibility(View.VISIBLE);
    }
}
