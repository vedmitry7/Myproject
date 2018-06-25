package app.mycity.mycity.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailFragment extends Fragment {

    DataStore dataStore;

    @BindView(R.id.emailFragEmailEt)
    EditText email;

    @BindView(R.id.emailFragProgressBarContainer)
    ConstraintLayout progressBarContainer;

    @BindView(R.id.emailFragmentTextViewInfo)
    TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBarContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataStore = (DataStore) context;
    }

    @OnClick(R.id.passwordFragNext)
    public void confirmEmail(View view){
        dataStore.setEmail(email.getText().toString());
        progressBarContainer.setVisibility(View.VISIBLE);
        dataStore.checkEmail();
    }

    public void emailExist() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBarContainer.setVisibility(View.GONE);
                info.setTextColor(Color.parseColor("#ff0000"));
                info.setText("Данный code уже занят, выберите другой или восстановите доступ");
            }
        });

    }
}
