package app.mycity.mycity.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.views.adapters.MyRecyclerViewAdapter;
import app.mycity.mycity.views.fragments.profile.ProfileCheckinContentOne;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;

public class LoseConnectionFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lose_connection_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    public static LoseConnectionFragment createInstance(String name) {
        LoseConnectionFragment fragment = new LoseConnectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.tryConnection)
    public void tryAgain(View v){
        EventBus.getDefault().post(new EventBusMessages.TryConnection());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
