package app.mycity.mycity.views.fragments.subscribers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.R;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.FriendsRecyclerAdapter;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribersOnlineListFragment extends Fragment {

    @BindView(R.id.myAllFriendsRecyclerAdapter)
    RecyclerView recyclerView;

    FriendsRecyclerAdapter adapter;
    List<User> userList;

    String id;

    Storage storage;

    boolean mayRestore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_all_friends, container, false);

        Log.d("TAG", "Create " + this.getClass().getSimpleName());
        if(getArguments() != null){
            id = getArguments().getString("userId");
            Log.d("TAG", "__________________________________________________________________________id = " + id);
        }
        Log.i("TAG3","All list created");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //userList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new FriendsRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);
        Log.d("TAG21", "ViewCreated " + this.getClass().getSimpleName());


        this.getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                    {
                    Log.d("TAG21", "BACK ");
                    return true;
                }
                return false;
            }
        } );

        getFriendsList();


    }


    public static SubscribersOnlineListFragment createInstance(String name, String userId) {
        SubscribersOnlineListFragment fragment = new SubscribersOnlineListFragment();
        Log.i("TAG21", "Create Subscribers " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG21", "Attach " + this.getClass().getSimpleName());
        storage = (Storage) context;

        userList = (List<User>) storage.getDate(getArguments().get("name")+ "_userListOnline");

        if(userList==null){
            Log.d("TAG21", "restore user list - null");
            userList = new ArrayList<>();
        } else {
            Log.d("TAG21", "restore user list size - " + userList.size());
            mayRestore = true;
        }
    }

    private void getFriendsList(){
        Log.d("TAG21", "getFriendsList " + this.getClass().getSimpleName());

        if(mayRestore){
            Log.d("TAG21", "restore " + this.getClass().getSimpleName() + " " + userList.size());
            adapter.update(userList);
        } else {
            Log.d("TAG21" +
                    "", "Cant restore " + this.getClass().getSimpleName());
            ApiFactory.getApi().getSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("id"), 1, "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                    UsersContainer users = response.body().getResponse();
                    if(users != null){
                        userList = users.getFriends();
                        Log.d("TAG", "Users all loaded. List size = " + userList.size());
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

    private void getFriendsListById(){
        Log.d("TAG21", "getFriendsListById " + this.getClass().getSimpleName());

        ApiFactory.getApi().getUsersById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), id, "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {


                UsersContainer users = response.body().getResponse();

                if(users != null){
                    userList = users.getFriends();
                    Log.d("TAG", "Users all loaded. List size = " + userList.size());
                    adapter.update(userList);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }


    public void onStart() {
        super.onStart();
        Log.d("TAG", "Start " + this.getClass().getSimpleName());

    }

    public void onResume() {
        super.onResume();
        Log.d("TAG21", "Resume " + this.getClass().getSimpleName());
    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());
        Log.i("TAG21","Friends ALL FRIENDS  resume");
        Log.i("TAG3","All list pause");

    }

    public void onStop() {
        super.onStop();
        storage.setDate(getArguments().get("name") + "_userListOnline", userList);
        Log.d("TAG21", "Stop " + this.getClass().getSimpleName() + " save userList - " + userList.size());
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAG", "Destroy view " + this.getClass().getSimpleName());

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG21", "Destroy " + this.getClass().getSimpleName());
        Log.i("TAG3","All list destroy");

    }

    public void onDetach() {
        super.onDetach();
        Log.d("TAG", "Detach " + this.getClass().getSimpleName());
        Log.i("TAG3","All list detach");

    }

}
