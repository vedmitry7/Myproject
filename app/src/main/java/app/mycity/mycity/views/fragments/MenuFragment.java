package app.mycity.mycity.views.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class MenuFragment extends Fragment implements TabStacker.TabStackInterface {


/*    @OnClick(R.id.mainActAddBtn)
    public void photo(View v){
        Log.d("TAG21", "PHOTO - ");
        EventBus.getDefault().post(new EventBusMessages.MakeCheckin());
    }*/

    @BindView(R.id.toolBarTitle)
    TextView toolBarTitle;
    @BindView(R.id.cardViewCheckins)
    CardView cardViewCheckins;
    @BindView(R.id.cardViewChronics)
    CardView cardViewChronics;
    @BindView(R.id.cardViewPlaces)
    CardView cardViewPlaces;
    @BindView(R.id.cardViewPeoples)
    CardView cardViewPeoples;
    @BindView(R.id.cardViewEvents)
    CardView cardViewEvents;
    @BindView(R.id.cardViewServices)
    CardView cardViewServices;
    private View fragmentView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;
        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, 0);
        Util.setUnreadCount(view);
        Typeface type = Typeface.createFromAsset(getContext().getAssets(),"abril_fatface_regular.otf");
        toolBarTitle.setTypeface(type);
        Log.d("TAG23", "create ");

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    ((CardView)v).setCardElevation(App.dpToPx(getContext(), 2));
                    ((CardView)v).setScaleX(0.99f);
                    ((CardView)v).setScaleY(0.99f);
                }
                if(event.getAction()== MotionEvent.ACTION_UP){
                    ((CardView)v).setCardElevation(App.dpToPx(getContext(), 5));
                    ((CardView)v).setScaleX(1f);
                    ((CardView)v).setScaleY(1f);
                }
                return false;
            }
        };

        cardViewCheckins.setOnTouchListener(onTouchListener);
        cardViewChronics.setOnTouchListener(onTouchListener);
        cardViewPlaces.setOnTouchListener(onTouchListener);
        cardViewPeoples.setOnTouchListener(onTouchListener);
        cardViewEvents.setOnTouchListener(onTouchListener);
        cardViewServices.setOnTouchListener(onTouchListener);
    }

    @OnClick(R.id.cardViewCheckins)
    public void che(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenFeed());
    }

    @OnClick(R.id.cardViewChronics)
    public void chr(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenChronics());
    }

    @OnClick(R.id.cardViewPlaces)
    public void pl(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenPlaces());
    }

    @OnClick(R.id.cardViewPeoples)
    public void pe(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenPeople());
    }

    @OnClick(R.id.cardViewEvents)
    public void ev(View v){
        EventBus.getDefault().post(new EventBusMessages.OpenEvents());
    }

    @OnClick(R.id.cardViewServices)
    public void se(View v){
      //  EventBus.getDefault().post(new EventBusMessages.OpenEvents());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT   -  MENU");
    }


    @OnClick(R.id.settingsButton)
    public void settingsButton(View v){
        EventBus.getDefault().post(new EventBusMessages.MainSettings());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
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
