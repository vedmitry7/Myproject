package app.mycity.mycity.views.fragments.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.FeedRecyclerAdapter;
import app.mycity.mycity.views.adapters.NewFeedRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedCheckinFragmentNew extends android.support.v4.app.Fragment {


    @BindView(R.id.feedFragmentRecyclerView)
    RecyclerView recyclerView;

    NewFeedRecyclerAdapter adapter;

    List<Post> postList;
    Map profiles;
    Map groups;

    boolean isLoading;

    int totalCount = 53 ;

    Storage storage;

    boolean mayRestore;

    int scrollPos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_checkin_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static FeedCheckinFragmentNew createInstance(String name) {
        FeedCheckinFragmentNew fragment = new FeedCheckinFragmentNew();
        Log.i("TAG21", "Create Subscribers LIST " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new NewFeedRecyclerAdapter(postList, profiles, groups);

        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        Log.d("TAG21", "Total count " + totalCount);
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > postList.size()){
                            Log.d("TAG21", "load feed FROM SCROLL");
                            loadFeed(postList.size());
                        }
                    }
                } else {
                    Log.d("TAG21", "did not load yet ");
                }
            }
        };
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(2, App.dpToPx(getActivity(), 2), false));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        Log.d("TAG21", "load feed FROM ON_CREATE");
        loadFeed(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        storage = (Storage) context;
        Log.d("TAG21", "storage - " + String.valueOf(storage == null) + totalCount);


        postList = (List<Post>) storage.getDate(getArguments().get("name")+ "_postList");
        profiles = (Map) storage.getDate(getArguments().get("name")+ "_profiles");
        groups = (Map) storage.getDate(getArguments().get("name")+ "_groups");

        if(postList==null){
            postList = new ArrayList<>();
            profiles = new HashMap();
            groups = new HashMap();
            totalCount = 0;
        } else {
            totalCount = (int) storage.getDate(getArguments().get("name")+ "_postListTotalCount");
            Log.d("TAG21", "Scroll position - " + storage.getDate(getArguments().get("name")+ "_scrollPosition"));
            scrollPos = (Integer) storage.getDate(getArguments().get("name")+ "_scrollPosition");
            mayRestore = true;
        }

    }

    private void loadFeed(int offset) {
        Log.d("TAG21", "loadFeed - offset - " + offset);
        if(mayRestore){
            Log.d("TAG21", "RESTORE date - ");
            mayRestore = false;
            adapter.update(postList, profiles, groups);
            recyclerView.scrollToPosition(scrollPos);
        } else {
            ApiFactory.getApi().getFeed(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "1", offset, "photo_130").enqueue(new retrofit2.Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseWall>> call, retrofit2.Response<ResponseContainer<ResponseWall>> response) {

                    if(response!=null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE FEED OK");

                        totalCount = response.body().getResponse().getCount();

                        postList.addAll(response.body().getResponse().getItems());

                        for (Profile p: response.body().getResponse().getProfiles()
                                ) {
                         //   Log.d("TAG21", "P - " + p.getFirstName()+ " " + p.getLastName());
                            profiles.put(p.getId(), p);
                        }

                        for (Group g: response.body().getResponse().getGroups()){
                         //   Log.d("TAG21","G - " + g.getId() + " " + g.getName());
                            groups.put(g.getId(), g);
                        }

                        Log.d("TAG21", "post size - " + postList.size() + " total - " + totalCount);
                        isLoading = false;

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR ");
                    }

                    adapter.update(postList, profiles, groups);
                }
                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                }
            });

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event) {
        Log.d("TAG21", "Like " + event.getItemId());

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
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
                    "post",
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


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        storage.setDate(getArguments().get("name") + "_postList", postList);
        storage.setDate(getArguments().get("name") + "_postListTotalCount", totalCount);
        storage.setDate(getArguments().get("name") + "_profiles", profiles);
        storage.setDate(getArguments().get("name") + "_groups", groups);
        storage.setDate(getArguments().get("name") + "_scrollPosition", ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition());

        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
