package app.mycity.mycity.activities;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;
import app.mycity.mycity.fragments.DateFragment;
import app.mycity.mycity.fragments.EmailFragment;

public class RegistrationActivity extends AppCompatActivity implements DataStore {


    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_authorization);

        DateFragment fragment = new DateFragment();

        fragmentManager = getFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setFirstName(String name) {

    }

    @Override
    public void setSecondName(String name) {

    }

    @SuppressLint("ResourceType")
    @Override
    public void nextStep() {
        EmailFragment emailFragment = new EmailFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
        transaction.replace(R.id.fragmentContainer, emailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}