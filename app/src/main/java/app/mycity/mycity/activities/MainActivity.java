package app.mycity.mycity.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import app.mycity.mycity.R;
import app.mycity.mycity.adapters.PagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    int currentSelectedItem = 2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_top);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_places);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);

        tabLayout.getTabAt(3).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_tape);

        viewPager.setCurrentItem(currentSelectedItem);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("TAG", "Selected tab - " + tab.getPosition());
                if(tab.getPosition()==2){
                    viewPager.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            viewPager.setCurrentItem(currentSelectedItem);
                        }
                    }, 100);
                    Log.i("TAG", "2! Select CURRENT - " + currentSelectedItem);
                } else {
                    currentSelectedItem = tab.getPosition();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}