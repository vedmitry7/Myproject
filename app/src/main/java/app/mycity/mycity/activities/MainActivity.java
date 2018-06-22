package app.mycity.mycity.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.mycity.mycity.DataStore;
import app.mycity.mycity.R;

public class MainActivity extends AppCompatActivity implements DataStore {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setFirstName(String name) {

    }

    @Override
    public void setSecondName(String name) {

    }

    @Override
    public void nextStep() {

    }
}
