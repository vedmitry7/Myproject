package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.UserRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class UniversalUserListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.myAllFriendsRecyclerAdapter)
    RecyclerView recyclerView;

    @BindView(R.id.listEmptyContainer)
    ConstraintLayout listEmptyContainer;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    UserRecyclerAdapter adapter;
    List<User> userList;

    String id;

    Storage storage;

    boolean mayRestore;

    String type;

    LinearLayoutManager layoutManager;
    private boolean isLoading;
    private int totalCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_all_friends, container, false);

        Log.d("TAG", "Create " + this.getClass().getSimpleName());
        if(getArguments() != null){
            id = getArguments().getString("ID");
            Log.d("TAG", "__________________________________________________________________________id = " + id);
        }
        Log.i("TAG3","All list created");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        type = getArguments().getString("type");
        //userList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);
        Log.d("TAG21", "ViewCreated " + this.getClass().getSimpleName());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int first = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + userList.size());
                        Log.d("TAG21", "Total count " + totalCount);
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount > userList.size()){
                            Log.d("TAG21", "load feed FROM SCROLL");
                            getUsers(userList.size());
                        }
                    }
                } else {
                    Log.d("TAG21", "did not load yet ");
                }


            }
        };

        getUsers(userList.size());
    }


    public static UniversalUserListFragment createInstance(String name, String id, String type) {
        UniversalUserListFragment fragment = new UniversalUserListFragment();
        Log.i("TAG21", "Create Subscribers LIST " + name + " " + id);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", id);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG21", "Attach " + this.getClass().getSimpleName());
        storage = (Storage) context;

        userList = (List<User>) storage.getDate(getArguments().get("name")+ "_" + getArguments().getString("type") + "_userlist");
        Log.d("TAG24", "get " + getArguments().get("name")+ "_" + getArguments().getString("type") + "_userlist");

        if(userList==null){
            Log.d("TAG24", "restore user list - null");
            Log.d("TAG23", getArguments().getString("type") + " Get date - " + getArguments().get("name") + "_" + getArguments().getString("type") + "_userlist" + " - null");
            userList = new ArrayList<>();
        } else {
            Log.d("TAG23", getArguments().getString("type") + " Get date - " + getArguments().get("name") + "_" + getArguments().getString("type") + "_userlist" + " - zbs");
            Log.d("TAG24", "restore user list size - " + userList.size());
            mayRestore = true;
        }
    }

    private void getUsers(int offset){
        Log.d("TAG21", "getUsers " + this.getClass().getSimpleName() + " user - " + getArguments().getString("id"));

        if(mayRestore){
            Log.d("TAG21", "restore " + this.getClass().getSimpleName() + " " + userList.size());
            placeHolder.setVisibility(View.GONE);
            mayRestore = false;
            if(userList.size()==0){
                listEmptyContainer.setVisibility(View.VISIBLE);
            }
            adapter.update(userList);
        } else {
            Log.d("TAG21" +
                    "", "Cant restore " + this.getClass().getSimpleName());

            retrofit2.Callback<ResponseContainer<UsersContainer>> callback = new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
                @Override
                public void onResponse(Call<ResponseContainer<UsersContainer>> call, Response<ResponseContainer<UsersContainer>> response) {
                    UsersContainer users = response.body().getResponse();
                    if(users != null){

                        swipeRefreshLayout.setRefreshing(false);
                        userList = users.getFriends();
                        totalCount = response.body().getResponse().getCount();
                        isLoading = false;
                        placeHolder.setVisibility(View.GONE);
                        if(userList.size()==0){
                            listEmptyContainer.setVisibility(View.VISIBLE);
                        }
                        Log.d("TAG", "Users all loaded. List size = " + userList.size());
                        adapter.update(userList);
                    } else {
                    }
                }
                @Override
                public void onFailure(Call<ResponseContainer<UsersContainer>> call, Throwable t) {
                }
            };

            switch (type){
                case Constants.KEY_PLACE_SUBSCRIBERS:
                    ApiFactory.getApi().getPlaceSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"),"photo_130", "0").enqueue(callback);
                return;
                case Constants.KEY_PLACE_ONLINE_SUBSCRIBERS:
                    ApiFactory.getApi().getPlaceSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"),"photo_130", "1").enqueue(callback);
                    return;
                case Constants.KEY_USER_IN_PLACE:
                    ApiFactory.getApi().getUsersInPlace(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"),"photo_130", "0").enqueue(callback);
                    return;
                case Constants.KEY_USER_IN_PLACE_SUBSCRIPTION:
                    ApiFactory.getApi().getUsersInPlace(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"),"photo_130", "1").enqueue(callback);
                    return;

                case Constants.KEY_SUBSCRIBERS:
                    ApiFactory.getApi().getSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"), 0, "photo_130").enqueue(callback);
                    return;
                case Constants.KEY_SUBSCRIBERS_ONLINE:
                    ApiFactory.getApi().getSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"), 1, "photo_130").enqueue(callback);
                    return;

                case Constants.KEY_SUBSCRIPTIONS:
                    ApiFactory.getApi().getSubscriptions(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"),0, "photo_130").enqueue(callback);
                    return;
                case Constants.KEY_SUBSCRIPTIONS_ONLINE:
                    ApiFactory.getApi().getSubscriptions(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset, getArguments().getString("id"), 1 , "photo_130").enqueue(callback);
                    return;

            }
        }
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
        storage.setDate(getArguments().get("name") + "_" + getArguments().getString("type") + "_userlist", userList);
        Log.d("TAG24", type + " Save date - " + getArguments().get("name") + "_" + getArguments().getString("type") + "_userlist");
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

    @Override
    public void onRefresh() {
        userList = new ArrayList<>();
        getUsers(0);
        placeHolder.setVisibility(View.VISIBLE);
    }
}
