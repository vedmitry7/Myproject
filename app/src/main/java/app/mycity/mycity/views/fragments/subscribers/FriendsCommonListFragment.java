package app.mycity.mycity.views.fragments.subscribers;

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

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.views.adapters.UserRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsCommonListFragment extends Fragment {


    @BindView(R.id.myAllFriendsRecyclerAdapter)
    RecyclerView recyclerView;

    UserRecyclerAdapter adapter;
    List<User> userList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_all_friends, container, false);
        Log.d("TAG", "Create " + this.getClass().getSimpleName());


        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("TAG", "ViewCreated " + this.getClass().getSimpleName());

        userList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new UserRecyclerAdapter(userList);
        recyclerView.setAdapter(adapter);

      //  getFriendsList();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("TAG", "Attach " + this.getClass().getSimpleName());

    }

    public void onStart() {
        super.onStart();
        Log.d("TAG", "Start " + this.getClass().getSimpleName());

    }

    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume " + this.getClass().getSimpleName());

    }

    public void onPause() {
        super.onPause();
        Log.d("TAG", "Pause " + this.getClass().getSimpleName());

    }

    public void onStop() {
        super.onStop();
        Log.d("TAG", "Stop " + this.getClass().getSimpleName());

    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TAG", "Destroy view " + this.getClass().getSimpleName());

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "Destroy " + this.getClass().getSimpleName());

    }

    public void onDetach() {
        super.onDetach();
        Log.d("TAG", "Detach " + this.getClass().getSimpleName());

    }

}
