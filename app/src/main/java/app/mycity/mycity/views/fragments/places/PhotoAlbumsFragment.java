package app.mycity.mycity.views.fragments.places;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.ResponseAlbums;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.AlbumsRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoAlbumsFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.albumsFragmentRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.photoAlbumPlaceHolder)
    RelativeLayout placeHolderNoAlbums;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout progress;


    List<Albume> albumsList;

    Map albums =  new HashMap<Long, List<Photo>>();
    //Map albums =  new HashMap<Long, List<Photo>>();

    AlbumsRecyclerViewAdapter adapter;

    Storage storage;

    boolean mayRestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_albums_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    public static PhotoAlbumsFragment createInstance(String name, String placeId) {
        PhotoAlbumsFragment fragment = new PhotoAlbumsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new AlbumsRecyclerViewAdapter(albumsList, albums);
        recyclerView.setAdapter(adapter);

        loadAlbums();
    }

    private void loadAlbums() {
        if(mayRestore){
            adapter.update(albumsList, albums);
            progress.setVisibility(View.GONE);
        } else {
            ApiFactory.getApi().getGroupAlbums(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("placeId"), 0)
                    .enqueue(new Callback<ResponseContainer<ResponseAlbums>>() {
                        @Override
                        public void onResponse(Call<ResponseContainer<ResponseAlbums>> call, Response<ResponseContainer<ResponseAlbums>> response) {
                            if (response.body().getResponse() != null) {

                                if(response.body().getResponse().getCount()==0){
                                    placeHolderNoAlbums.setVisibility(View.VISIBLE);
                                }

                                progress.setVisibility(View.GONE);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        storage = (Storage) context;
        Log.d("TAG21", "storage - " + String.valueOf(storage == null));


        albumsList = (List<Albume>) storage.getDate(getArguments().get("name")+ "_albumsList");
        albums = (Map) storage.getDate(getArguments().get("name")+ "_mapAlbums");

        if(albumsList==null){
            Log.d("TAG21", "Albums FRAGMENT Restore null");
            albumsList = new ArrayList<>();
            albums = new HashMap();
        } else {
            Log.d("TAG21", "Albums FRAGMENT Restore ok");
            mayRestore = true;
        }

    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        Log.d("TAG21", "Stop Albums FRAGMENT Save " + getArguments().getString("name"));
        storage.setDate(getArguments().get("name") + "_albumsList", albumsList);
        storage.setDate(getArguments().get("name") + "_mapAlbums", albums);

        super.onStop();
    }
}
