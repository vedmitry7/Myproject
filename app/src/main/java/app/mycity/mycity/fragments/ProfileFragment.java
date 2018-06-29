package app.mycity.mycity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.mycity.mycity.Constants;
import app.mycity.mycity.PersistantStorage;
import app.mycity.mycity.R;
import app.mycity.mycity.activities.MainAct;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


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
        Log.i("TAG", "w - " + imageView.getWidth() + "/" + "h" + imageView.getHeight());
        int width = imageView.getWidth();
        imageView.getLayoutParams().height = width;
        imageView.getLayoutParams().height = width;
        imageView.requestLayout();
        Log.i("TAG", "w - " + imageView.getWidth() + "/" + "h" + imageView.getHeight());
        getInfo();
        getFriendsCount();
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
        ApiFactory.getApi().getUser(PersistantStorage.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<User>>() {
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

        ApiFactory.getApi().getUsers(PersistantStorage.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
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
        Log.d("TAG", "FRIENDS");
        activity.startMyFriends();
    }


}
