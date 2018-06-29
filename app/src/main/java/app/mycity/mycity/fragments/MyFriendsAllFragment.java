package app.mycity.mycity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.PersistantStorage;
import app.mycity.mycity.R;
import app.mycity.mycity.adapters.FriendsRecyclerAdapter;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFriendsAllFragment extends Fragment {


    @BindView(R.id.myAllFriendsRecyclerAdapter)
    RecyclerView recyclerView;

    FriendsRecyclerAdapter adapter;
    List<User> userList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_all_friends, container, false);


        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new FriendsRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);

        getFriendsList();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void getFriendsList(){

        ApiFactory.getApi().getUsersWithFields(PersistantStorage.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                for (User u:users.getFriends()
                     ) {
                    Log.d("TAG", "User link - " + u.getPhoto130());
                }

                if(users != null){
                    userList = users.getFriends();
                    Log.d("TAG", "Users list size = " + userList.size());
                    adapter.update(userList);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

}
