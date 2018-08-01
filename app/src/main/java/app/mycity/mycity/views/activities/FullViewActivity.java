package app.mycity.mycity.views.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import app.mycity.mycity.R;

public class FullViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view);

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);


        String path = getIntent().getStringExtra("path");

        Picasso.get().load(path)
                .into(photoView);
      //  photoView.setImageResource(R.drawable.image);

    }
}
