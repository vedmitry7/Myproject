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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Dialog;
import app.mycity.mycity.api.model.DialogsContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.DialogsRecyclerAdapter;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DialogsFragment extends Fragment implements TabStacker.TabStackInterface {

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

    public static DialogsFragment createInstance(String name, int tabPos) {
        DialogsFragment fragment = new DialogsFragment();
        Log.i("TAG21", "Create DIALOGS " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("tabPos", tabPos);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("TAG21","onViewCreated " + getArguments().getInt("tabPos"));

        Util.indicateTabImageView(getContext(), view, getArguments().getInt("tabPos"));
        Util.setOnTabClick(view);

        dialogList = new ArrayList<>();

        adapter = new DialogsRecyclerAdapter(dialogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        loadDialogs();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void loadDialogs() {
        ApiFactory.getApi().getDialogs(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), 0).enqueue(new Callback<ResponseContainer<DialogsContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<DialogsContainer>> call, Response<ResponseContainer<DialogsContainer>> response) {

                // !!!!!!!!!
                // NullPointerException: Attempt to invoke virtual method 'java.lang.Object app.mycity.mycity.api.model.ResponseContainer.getResponse()' on a null object reference

                DialogsContainer dialogs = response.body().getResponse();

                if(response != null && response.body().getResponse() != null){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UpdateDialog event){
        Log.d("TAG21", "Update dialog");
        for (Dialog dialog: dialogList
             ) {
            if(dialog.getId()==event.getId()){
                dialog.setText(event.getMessage());
            }
        }

        adapter.notifyDataSetChanged();
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
