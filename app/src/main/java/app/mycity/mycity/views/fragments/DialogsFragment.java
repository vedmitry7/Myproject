package app.mycity.mycity.views.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import app.mycity.mycity.api.model.Dialog;
import app.mycity.mycity.api.model.DialogsContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.DialogsRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DialogsFragment extends Fragment {

    @BindView(R.id.dialogsFragRecyclerView)
    RecyclerView recyclerView;

    DialogsRecyclerAdapter adapter;

    List<Dialog> dialogList;

    MainAct activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogList = new ArrayList<>();

        adapter = new DialogsRecyclerAdapter(dialogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        loadDialogs();
    }

    private void loadDialogs() {
        ApiFactory.getApi().getDialogs(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), 0).enqueue(new Callback<ResponseContainer<DialogsContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<DialogsContainer>> call, Response<ResponseContainer<DialogsContainer>> response) {

                // !!!!!!!!!
                // NullPointerException: Attempt to invoke virtual method 'java.lang.Object app.mycity.mycity.api.model.ResponseContainer.getResponse()' on a null object reference

                DialogsContainer dialogs = response.body().getResponse();

                if(dialogs != null){
                    dialogList = dialogs.getDialogs();
                    Log.d("TAG", "Size list = " + dialogList.size());
                    adapter.update(dialogList);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<DialogsContainer>> call, Throwable t) {

            }
        });
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


}
