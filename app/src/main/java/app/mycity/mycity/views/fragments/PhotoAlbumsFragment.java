package app.mycity.mycity.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Albume;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseAlbums;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.CommentActivity;
import app.mycity.mycity.views.adapters.AlbumsRecyclerViewAdapter;
import app.mycity.mycity.views.adapters.MyRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoAlbumsFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.albumsFragmentRecyclerView)
    RecyclerView recyclerView;


    List<Albume> albumsList;

    Map albums =  new HashMap<Long, List<Photo>>();
    //Map albums =  new HashMap<Long, List<Photo>>();


    AlbumsRecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_albums_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        albumsList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new AlbumsRecyclerViewAdapter(albumsList, albums);
        recyclerView.setAdapter(adapter);


        loadAlbums();
    }

    private void loadAlbums() {

        ApiFactory.getApi().getGroupAlbums(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "1", 0)
                .enqueue(new Callback<ResponseContainer<ResponseAlbums>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseAlbums>> call, Response<ResponseContainer<ResponseAlbums>> response) {
                if (response.body().getResponse() != null) {

                    albumsList = response.body().getResponse().getItems();
                    Log.d("TAG21", "Albums size - " + albumsList.size());

                    for (final Albume a:albumsList
                         ) {
                    }
                    adapter.update(albumsList, albums);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseAlbums>> call, Throwable t) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LoadAlbum event){
        if(!albums.containsKey(event.getAlbumId())){
            Log.d("TAG21", "Album loading.... position - " + event.getAdapterPosition());
            ApiFactory.getApi().getAlbum(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "1",
                    String.valueOf(event.getAlbumId())).enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
                @Override
                public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                    if(response.body().getResponse()!=null){
                        Log.d("TAG21", "Album " + event.getAlbumId() + " size - " + response.body().getResponse().getPhotos().size());
                        albums.put(event.getAlbumId(), response.body().getResponse().getPhotos());
                    }
                    adapter.updatePosition(albumsList, albums, event.getAdapterPosition());
                }

                @Override
                public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {
                }
            });
        } else {
            Log.d("TAG21", "Album already loaded");
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

}
