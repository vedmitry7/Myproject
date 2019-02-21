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
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.PhotoReportRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoReportFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.photoAlbumRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.placeName)
    TextView albumName;

    List<Photo> photoList;
    PhotoReportRecyclerAdapter adapter;

    String reportId;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;


    public static PhotoReportFragment createInstance(String name, String albumId, String albumName, Long albumDate) {
        PhotoReportFragment fragment = new PhotoReportFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("albumId", albumId);
        bundle.putString("albumName", albumName);
        bundle.putLong("albumDate", albumDate);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_album_fragment, container, false);
        ButterKnife.bind(this, view);

        if(getArguments().getString("name")!=null){
            albumName.setText(getArguments().getString("albumName"));
        } else {
            albumName.setText("");
        }

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);

        reportId = getArguments().getString("id");

        return view;
    }

    @OnClick(R.id.backButton)
    public void backButton(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().post(new EventBusMessages.DefaultStatusBar());

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
      //  recyclerView.setNestedScrollingEnabled(false);

        adapter = new PhotoReportRecyclerAdapter(photoList, getArguments().getString("albumId"));
       // adapter.setImageClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + photoList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= photoList.size()){
                            Log.d("TAG21", "load feed ");
                            loadMedia(photoList.size());
                        }
                    }
                }
            }
        };
        loadMedia(photoList.size());

        //super.onViewCreated(view, savedInstanceState);
    }

    private void loadMedia(final int offset) {

        if(mayRestore){
            Log.d("TAG24", "RESTORE PLACE CHECKIN - " );
            if(photoList.size()==0){
              //  placeCheckinPlaceHolder.setVisibility(View.VISIBLE);
            }
            adapter.update(photoList);
        } else {
            Log.d("TAG24", "Can not RESTORE PLACE CHECKIN ");
            ApiFactory.getApi().getAlbum(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "1", getArguments().getString("albumId"), "0").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
                @Override
                public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                    if(response.body().getResponse()!=null){
                        Log.d("TAG24", "Album " + " size - " + response.body().getResponse().getPhotos().size());
                        photoList.addAll(response.body().getResponse().getPhotos());
                        adapter.update(photoList);
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {
                }
            });
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
        storage = (Storage) context;
        photoList = (List<Photo>) storage.getDate(getArguments().get("name")+ "_postList");

        if(photoList ==null){
            Log.d("TAG21", "restore null");
            photoList = new ArrayList<>();
        } else {
            Log.d("TAG21", "restore ok - " + photoList.size());
            mayRestore = true;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void photoClick(EventBusMessages.PhotoReportPhotoClick event){
        EventBus.getDefault().post(new EventBusMessages.OpenPhotoReportContent (
                String.valueOf(photoList.get(event.getPosition()).getPlaceId()),
                getArguments().getString("albumId"),
                getArguments().getString("albumName"),
                getArguments().getLong("albumDate"),
                event.getPosition()));
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        Log.d("TAG21", "Stop CHECKIN FRAGMENT Save " + getArguments().getString("name"));
        Log.d("TAG21", "!!!!!!!!!!  Save " + getArguments().get("name") + "_postList");
        Log.d("TAG21", "!!!!!!!!!!  Save " + getArguments().get("name") + "_profiles");
     //   storage.setDate(getArguments().get("name") + "_postList", photoList);
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }
}
