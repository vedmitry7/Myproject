package app.mycity.mycity.views.fragments.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Album;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.ResponseAlbums;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.FeedPhotoReportAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedPhotoAlbumFragment extends android.support.v4.app.Fragment {


    @BindView(R.id.feedFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.chronicksHolder)
    RelativeLayout holderInfo;

    FeedPhotoReportAdapter adapter;

    List<Album> albumsList;
    Map<String, Group> groups = new HashMap<>();


    boolean isLoading;

    int totalCount = 0;

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

    public static FeedPhotoAlbumFragment createInstance(String name, String subscriptionOnly) {
        FeedPhotoAlbumFragment fragment = new FeedPhotoAlbumFragment();
        Log.i("TAG24", "Create albums fr " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("subscriptionOnly", subscriptionOnly);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new FeedPhotoReportAdapter(albumsList, groups);

     //   final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG24", "ЗАГРУЗКА ДАННЫХ " + albumsList.size());
                        Log.d("TAG24", "Total count " + totalCount);
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > albumsList.size()){
                            Log.d("TAG24", "load feed FROM SCROLL - total - " + totalCount + " " + albumsList.size());
                            loadFeed(albumsList.size());
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
        Log.d("TAG24", "storage - " + String.valueOf(storage == null) + totalCount);


       albumsList = (List<Album>) storage.getDate(getArguments().get("name")+ "_albumsList");
       groups = (Map) storage.getDate(getArguments().get("name")+ "_groups");

        if(albumsList==null){
            albumsList = new ArrayList<>();
            groups = new HashMap<>();
            totalCount = 0;
        } else {
            //totalCount = (int) storage.getDate_ddMMyyyy(getArguments().get("name")+ "_postListTotalCount");
            Log.d("TAG24", "Scroll position - " + storage.getDate(getArguments().get("name")+ "_scrollPosition"));
//            scrollPos = (Integer) storage.getDate(getArguments().get("name")+ "_scrollPosition");
            mayRestore = true;
        }

    }

    private void loadFeed(int offset) {
        Log.d("TAG24", "loadFeed Albums - offset - " + offset);
        if(mayRestore){
            Log.d("TAG24", "RESTORE date - ");
            mayRestore = false;
            adapter.update(albumsList, groups);
            recyclerView.scrollToPosition(scrollPos);
            placeHolder.setVisibility(View.GONE);
        } else {
            ApiFactory.getApi().getAllGroupAlbums(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),  0, "1", getArguments().getString("subscriptionOnly"))
                    .enqueue(new Callback<ResponseContainer<ResponseAlbums>>() {
                        @Override
                        public void onResponse(Call<ResponseContainer<ResponseAlbums>> call, Response<ResponseContainer<ResponseAlbums>> response) {
                            Log.d("TAG24", "RESTORE");

                            if (response.body().getResponse() != null) {

                                if(response.body().getResponse().getCount() !=0){
                                    placeHolder.setVisibility(View.GONE);

                                    if(response.body().getResponse().getCount()==0){
                                        //  placeHolderNoAlbums.setVisibility(View.VISIBLE);
                                    }

                                    for (Group g: response.body().getResponse().getGroups()){
                                        Log.d("TAG24", "G - " + g.getId() + " " + g.getName());
                                        groups.put(g.getId(), g);
                                    }

                                    //  progress.setVisibility(View.GONE);
                                    albumsList.addAll(response.body().getResponse().getItems());
                                    Log.d("TAG24", "Albums size - " + albumsList.size());
                                    adapter.update(albumsList, groups);
                                } else {
                                    placeHolder.setVisibility(View.GONE);
                                    holderInfo.setVisibility(View.VISIBLE);
                                }


                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseContainer<ResponseAlbums>> call, Throwable t) {

                        }
                    });
        }
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("TAG25","chronics save date");
        storage.setDate(getArguments().get("name") + "_albumsList", albumsList);
        storage.setDate(getArguments().get("name") + "_groups", groups);
        super.onStop();
    }
}
