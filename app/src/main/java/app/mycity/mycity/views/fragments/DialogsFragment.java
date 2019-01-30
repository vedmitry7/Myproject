package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.RealmUser;
import app.mycity.mycity.api.model.SuccessResponceNumber;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.RealmUtil;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Dialog;
import app.mycity.mycity.api.model.DialogsContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.DialogsRecyclerAdapter;
import app.mycity.mycity.views.decoration.TopPaddingDecoration;
import app.mycity.mycity.views.fragments.profile.ProfileFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DialogsFragment extends Fragment implements TabStacker.TabStackInterface {

    @BindView(R.id.dialogsFragRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.placesProgressBar)
    ConstraintLayout placesProgressBar;

    DialogsRecyclerAdapter adapter;

    List<Dialog> dialogList;

    MainAct activity;

    View fragmentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogs, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static DialogsFragment createInstance(String name) {
        DialogsFragment fragment = new DialogsFragment();
        Log.i("TAG21", "Create DIALOGS " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("TAG21","onViewCreated Dialogs" + getArguments().getInt("tabPos"));

        fragmentView = view;

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, 2);
        Util.setUnreadCount(view);

        dialogList = new ArrayList<>();

        adapter = new DialogsRecyclerAdapter(dialogList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new TopPaddingDecoration(App.dpToPx(getActivity(), 4)));
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
        Log.i("TAG25", "stop idalog frag");
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

                    placesProgressBar.setVisibility(View.GONE);
                    for (Dialog d:dialogList
                         ) {
                        //Realm mRealm = Realm.getDefaultInstance();

                        RealmUtil.createOrUpdateRealmUser(d.getId(), d.getTitle(), d.getPhoto130());
 /*
                        RealmUser user = mRealm.where(RealmUser.class).equalTo("id", d.getId()).findFirst();
                       if(user==null){
                            Log.d("TAG21", "user  " + d.getTitle() + " null, add to db");
                            mRealm.beginTransaction();
                            RealmUser user1 = mRealm.createObject(RealmUser.class, d.getId());
                            user1.setFirstName(d.getTitle());
                            user1.setPhoto130(d.getPhoto130());
                          //  user1.setId(d.getId());
                            mRealm.commitTransaction();
                        } else {
                            RealmUser realmUser = new RealmUser();
                            realmUser.setId(d.getId());
                            realmUser.setFirstName(d.getTitle());
                            realmUser.setPhoto130(d.getPhoto130());
                            mRealm.beginTransaction();
                            mRealm.copyToRealmOrUpdate(realmUser);
                            mRealm.commitTransaction();
                            Log.d("TAG21", "user  " + d.getTitle() + " exist");
                        }*/
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<DialogsContainer>> call, Throwable t) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.DeleteDialog event){
        Log.d("TAG25", "message delete");

        ApiFactory.getApi().deleteDialogs(App.accessToken(), event.getId()).enqueue(new Callback<ResponseContainer<SuccessResponceNumber>>() {
            @Override
            public void onResponse(Call<ResponseContainer<SuccessResponceNumber>> call, Response<ResponseContainer<SuccessResponceNumber>> response) {
                if(response.body()!=null && response.body().getResponse()!=null){
                    if(response.body().getResponse().getSuccess()==1){
                        for (int i = 0; i < dialogList.size(); i++) {
                            if(dialogList.get(i).getId().equals(event.getId())){
                                dialogList.remove(i);
                                adapter.notifyItemRemoved(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<SuccessResponceNumber>> call, Throwable t) {
            }
        });
        //adapter.update(results);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UpdateDialog event){
        Log.d("TAG21", "Update dialog");
        for (Dialog dialog: dialogList
             ) {
            if(dialog.getId().equals(event.getId())){
                dialog.setText(event.getMessage());
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.DialogUpdate event){
        Log.i("TAG25", "!!U" + event.getDialogId() + " " + event.getMessageText() + " " + event.getUnreadCount() );

      /*  for (Dialog d:dialogList
             ) {
            if(d.getId().equals(event.getDialogId())){
                d.setText(event.getMessageText());
                d.setCountUnread(event.getUnreadCount());
                d.setDate(event.getTime());
            }
        }*/

        for (int i = 0; i < dialogList.size(); i++) {
            if(dialogList.get(i).getId().equals(event.getDialogId())){
                dialogList.get(i).setText(event.getMessageText());
                dialogList.get(i).setCountUnread(event.getUnreadCount());
                dialogList.get(i).setDate(event.getTime());
                Dialog dialog = dialogList.get(i);
                dialogList.remove(i);
                dialogList.add(0, dialog);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainAct) context;
        Log.i("TAG21", "attach DIALOGS " );
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
