package app.mycity.mycity.views.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
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


public class SomeoneProfileFragment extends Fragment {

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
    List<Likes> likeList;
    List<Post> postList;



    String id;
    MainAct activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_profile_fragment_scrolling, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        id = getArguments().getString("ID");



        getInfo();
        getFriendsCount();
        getCheckins();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        photoList = new ArrayList<>();
        adapter = new CheckinRecyclerAdapter(postList);
        recyclerView.setAdapter(adapter);
        Log.i("TAG21","Someone - stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3","SomeOne Profile created");

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
        Log.i("TAG21", "Someone getInfo");
        if(id.equals("") || id == null){
            Log.i("TAG", "ID doesn't exist!");
            return;
        }

        ApiFactory.getApi().getUserById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), id,  "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<User>>() {
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
        if(id.equals("") || id == null) {
            Log.i("TAG", "ID doesn't exist!");
            return;
        }

        ApiFactory.getApi().getUsersById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), id, "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
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


    @OnClick(R.id.profileFragSettingButtonContainer)
    public void settings(View v){
        Log.d("TAG", "SETTINGS");
        activity.startSettings(0);

    }

    @OnClick(R.id.profileFragFriendsButton)
    public void friends(View v){
        Log.d("TAG", "GET FRIENDS BY ID!!!!!!!!!!  " + id);
        activity.startFriendsById(id);
    }

    private void getCheckins(){
        Log.d("TAG21", "SOMEONE PROFILE GET CHECKINS");
        ApiFactory.getApi().getPhotosById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), id, "2").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {

                PhotoContainer photos = response.body().getResponse();

                if(photos != null){
                    photoList = photos.getPhotos();
                    Log.d("TAG21", "photos size = " + photoList.size());
                   // adapter.update(photoList);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG21", "Profile Someone Fragment resume");
        Log.i("TAG3","SomeOne Profile created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG3","SomeOne Profile destroy");
    }
}
