package app.mycity.mycity.views.fragments;

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
import app.mycity.mycity.R;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.views.adapters.FriendsLongListRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LongListFragment extends Fragment {

    @BindView(R.id.longListFragRecyclerView)
    RecyclerView recyclerView;

    List<User> userList;
    FriendsLongListRecyclerAdapter adapter;

    boolean isLoading;
    int offset;
    int totalCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.long_list, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userList = new ArrayList<>();
        adapter = new FriendsLongListRecyclerAdapter(userList);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItems = layoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG", "ЗАГРУЗКА ДАННЫХ " + userList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= userList.size()){
                            loadList(userList.size());
                        }
                    }
                }
            }
        };

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);

        loadList(offset);
    }

    private void loadList(final int offset) {
        ApiFactory.getApi().getFriendsList(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), offset).enqueue(new Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<UsersContainer>> call, Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();
                totalCount = response.body().getResponse().getCount();
                Log.d("TAG", "TOTAL - " + totalCount);
                if(users != null){
                    userList.addAll(users.getFriends());
                    Log.d("TAG", "offset = " + offset + " List Size = " + response.body().getResponse().getFriends().size());

                    adapter.update(userList);
                    isLoading = false;
                } else {
                    Log.d("TAG", "error");
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
