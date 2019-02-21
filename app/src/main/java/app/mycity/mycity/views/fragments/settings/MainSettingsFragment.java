package app.mycity.mycity.views.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.LocationService;
import app.mycity.mycity.R;
import app.mycity.mycity.SocketService;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.LoginActivity;
import app.mycity.mycity.views.activities.MainActivity3;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSettingsFragment extends Fragment implements TabStacker.TabStackInterface {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.notificationsButton)
    public void notif(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenNotificationSettings());
    }

    @OnClick(R.id.profileButton)
    public void notif2(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenProfileSettings());
    }

    @OnClick(R.id.logoutButton)
    public void logout(View v){
        unregisterDevice();

        SharedManager.addBooleanProperty("login", false);
        getActivity().stopService(new Intent(getContext(), SocketService.class));
        getActivity().stopService(new Intent(getContext(), LocationService.class));
        FirebaseMessaging.getInstance().unsubscribeFromTopic("profile" + SharedManager.getProperty(Constants.KEY_MY_ID));
    }

    @OnClick(R.id.backButton)
    public void backButton(View v){
        getActivity().onBackPressed();
    }

    void unregisterDevice(){
        String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i("TAG21", "android Id " + androidID);
        Log.i("TAG21", "t " + App.accessToken());


        ApiFactory.getApi().unregisterDevice(App.accessToken(), androidID).enqueue(new Callback<ResponseContainer<Success>>() {
            @Override
            public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                if(response.body().getResponse().getSuccess()==1){
                    SharedManager.addBooleanProperty("deviceRegister", false);

                    SharedManager.addProperty(Constants.KEY_ACCESS_TOKEN, "null");
                    SharedManager.addBooleanProperty("deviceRegister", false);
                    getActivity().finish();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
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
