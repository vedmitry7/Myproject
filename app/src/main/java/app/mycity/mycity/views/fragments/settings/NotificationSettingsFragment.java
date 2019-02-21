package app.mycity.mycity.views.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.SettingsResponse;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSettingsFragment extends Fragment implements TabStacker.TabStackInterface {


    @BindView(R.id.swichMessage)
    Switch switchMessage;
    @BindView(R.id.swichLikeCh)
    Switch switchLikeCh;
    @BindView(R.id.swichLikeComment)
    Switch switchLikeComment;
    @BindView(R.id.swichFollow)
    Switch switchFollow;
    @BindView(R.id.swichCommentCh)
    Switch switchCommentCh;
    @BindView(R.id.swichEvents)
    Switch switchEvents;
    @BindView(R.id.swichActions)
    Switch switchActions;

    @BindView(R.id.loadPlaceHolder)
    ConstraintLayout placeHolder;

    CompoundButton.OnCheckedChangeListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.backButton)
    public void backButton(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        loadSettings();

        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sendSettings();
            }
        };

        super.onViewCreated(view, savedInstanceState);
    }

    private void sendSettings() {
        ApiFactory.getApi().sendSettings(App.accessToken(),
                switchMessage.isChecked() ? 1 : 0,
                switchLikeCh.isChecked() ? 1 : 0,
                switchLikeComment.isChecked() ? 1 : 0,
                switchFollow.isChecked() ? 1 : 0,
                switchCommentCh.isChecked() ? 1 : 0,
                switchEvents.isChecked() ? 1 : 0,
                switchActions.isChecked() ? 1 : 0)

                .enqueue(new Callback<ResponseContainer<SettingsResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<SettingsResponse>> call, Response<ResponseContainer<SettingsResponse>> response) {

            }

            @Override
            public void onFailure(Call<ResponseContainer<SettingsResponse>> call, Throwable t) {

            }
        });
    }

    private void loadSettings() {
        ApiFactory.getApi().getSettings(App.accessToken()).enqueue(new Callback<ResponseContainer<SettingsResponse>>() {
            @Override
            public void onResponse(Call<ResponseContainer<SettingsResponse>> call, Response<ResponseContainer<SettingsResponse>> response) {

                if(response.body().getResponse()!=null){
                    if(response.body().getResponse().getNewMessage()==1){
                        switchMessage.setChecked(true);
                    }
                    if(response.body().getResponse().getLikePost()==1){
                        switchLikeCh.setChecked(true);
                    }
                    if(response.body().getResponse().getLikeComment()==1){
                        switchLikeComment.setChecked(true);
                    }
                    if(response.body().getResponse().getFollow()==1){
                        switchFollow.setChecked(true);
                    }
                    if(response.body().getResponse().getCommentPost()==1){
                        switchCommentCh.setChecked(true);
                    }
                    if(response.body().getResponse().getPlaceEvent()==1){
                        switchEvents.setChecked(true);
                    }
                    if(response.body().getResponse().getPlaceAction()==1){
                        switchActions.setChecked(true);
                    }
                    placeHolder.setVisibility(View.GONE);
                    switchMessage.setOnCheckedChangeListener(listener);
                    switchLikeCh.setOnCheckedChangeListener(listener);
                    switchLikeComment.setOnCheckedChangeListener(listener);
                    switchFollow.setOnCheckedChangeListener(listener);
                    switchCommentCh.setOnCheckedChangeListener(listener);
                    switchEvents.setOnCheckedChangeListener(listener);
                    switchActions.setOnCheckedChangeListener(listener);
                }

            }

            @Override
            public void onFailure(Call<ResponseContainer<SettingsResponse>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
