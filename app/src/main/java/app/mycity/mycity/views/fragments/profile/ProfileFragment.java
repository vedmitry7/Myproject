package app.mycity.mycity.views.fragments.profile;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {

    @BindView(R.id.profileFragRoundImage)
    CircleImageView imageView;

    @BindView(R.id.profileFragProgressBarContainer)
    ConstraintLayout progressBar;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    @BindView(R.id.profileFragName)
    TextView name;
    @BindView(R.id.profileFragFriendsTv)
    TextView friendsCount;

    @BindView(R.id.profileFragCurrentPointContainer)
    RelativeLayout currentPoint;

    @BindView(R.id.someOneProfileFragRecyclerView)
    RecyclerView recyclerView;

    CheckinRecyclerAdapter adapter;

    List<Photo> photoList;

    MainAct activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInfo();
        getFriendsCount();
        getCheckins();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        photoList = new ArrayList<>();
        adapter = new CheckinRecyclerAdapter(photoList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainAct) context;
    }


    @OnClick(R.id.profileFragBackButtonContainer)
    public void back(View v){
        getActivity().onBackPressed();
    }


    private void getInfo(){
        ApiFactory.getApi().getUser(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<User>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<User>> call, retrofit2.Response<ResponseContainer<User>> response) {
                User user = response.body().getResponse();
                if(user != null){
                    Log.i("TAG", user.getFirstName());
                    Log.i("TAG", user.getLastName());
                    Log.i("TAG", user.getPhoto780());

                    progressBar.setVisibility(View.GONE);
                    name.setText(user.getFirstName() + " " + user.getLastName());
                    currentPoint.setVisibility(View.GONE);
                    Picasso.get().load(user.getPhoto780()).into(imageView);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<User>> call, Throwable t) {

            }
        });
    }

    private void getFriendsCount(){

        ApiFactory.getApi().getUsers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                if(users != null){
                    friendsCount.setText(String.valueOf(users.getFriends().size()));
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

    private void getCheckins(){
        ApiFactory.getApi().getPhotosById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),SharedManager.getProperty(Constants.KEY_MY_ID), "2").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                PhotoContainer photos = response.body().getResponse();

                if(photos != null){
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    Log.d("TAG", "photos size = " + photoList.size());
                    adapter.update(photoList);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {

            }
        });
    }


    @OnClick(R.id.profileFragSettingButtonContainer)
    public void settings(View v){
        Log.d("TAG", "SETTINGS");
        activity.startSettings(0);

    }

    @OnClick(R.id.profileFragFriendsButton)
    public void friends(View v){
        Log.d("TAG", "FRIENDS");
        activity.startFriends();
    }


}
