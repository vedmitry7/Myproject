package app.mycity.mycity.filter_desc_post;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import app.mycity.mycity.App;
import app.mycity.mycity.PublicationService;
import app.mycity.mycity.PublicationVideoService;
import app.mycity.mycity.R;
import app.mycity.mycity.util.BitmapUtils;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getSimpleName();

    @BindView(R.id.videoView)
    PlayerView playerView;

    File file;
    Uri fileUri;

    SimpleExoPlayer player;

    String path;

    private static final int REQUEST_CODE = 1;
    private int REQUEST_ACTIVITY_DESCRIPTION = 3;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private String saveImagePath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        Log.d("TAG21", "MAin OnCreate");

        if(savedInstanceState==null){
            makeCheckin();
        } else {
            finish();
            path = getIntent().getStringExtra("path");
        }
    }


    private void initializePlayer() {
        Log.d("TAG21", "init player " + fileUri.getPath());
/*


        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),

                new DefaultLoadControl());


        DataSpec dataSpec = new DataSpec(Uri.fromFile(file));
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);
        player.setPlayWhenReady(true);
        //audioSource = buildMediaSource(Uri.fromFile(file));
        player.prepare(audioSource);
*/

        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(),

                new DefaultLoadControl());

        playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        //  player.seekTo(currentWindow, playbackPosition);

       // Uri uri = Uri.parse("http://front.itmacros.beget.tech/video/1.mp4");
        MediaSource mediaSource = buildMediaSource(Uri.fromFile(file));
        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultDataSourceFactory(getApplicationContext(), "exoplayer-codelab")).
                createMediaSource(uri);
    }

    @Override
    protected void onResume() {
        Log.d("Test2", "RESUME FILTER" );
        super.onResume();

    }

    public void makeCheckin() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        file = new File(Util.getExternalVideoFileName());
        Log.d("TAG21", file.getAbsolutePath());
        Log.d("TAG21", file.getAbsolutePath());
        fileUri = Uri.fromFile(file);

       /* Uri imageUri = FileProvider.getUriForFile(
                this,
                "app.mycity.mycity.provider", //(use your app signature + ".provider" )
                file);*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

       intent.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 720);
        intent.putExtra(MediaStore.Video.Thumbnails.WIDTH, 720);

        startActivityForResult(intent, REQUEST_CODE);

    }

    // opening image in default image viewer app
    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            path = file.getAbsolutePath();
            Log.d("TAG21", "VIDEO PATH - " + path);
            initializePlayer();
            // clear bitmap memory\

            if (resultCode == RESULT_OK && requestCode == REQUEST_ACTIVITY_DESCRIPTION) {
                finish();
            }

            if (resultCode == RESULT_CANCELED && path.equals("")) {
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @OnClick(R.id.makeCheckinAgain)
    public void checkinAgain(View v){
        makeCheckin();
    }

    @OnClick(R.id.nextButton)
    public void publish(View v){

        if(App.isOnline(this)){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Публикация");
            progressDialog.setCancelable(false);
            progressDialog.show();
            post(path);

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Отсутствует интернет подключение");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_open) {
            // openImageFromGallery();
            makeCheckin();
            return true;
        }

        if (id == R.id.action_save) {
            Log.d("TAG21", "click  ");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void publicationComplete(EventBusMessages.PublicationComplete event){
        Log.i("Test2", "Event finish dfgdfs");
        EventBus.getDefault().removeStickyEvent(event);
        Log.i("Test2", "hide dialog");
        progressDialog.hide();
        this.finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void publicationError(EventBusMessages.PublicationError event){
        Log.d("TAG23", "publication error" );

        progressDialog.hide();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage("Во время публикации чекина произошла ошибка");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        EventBus.getDefault().removeStickyEvent(event);
    }

/*
    void startDescriptionActivity(String path){
        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, REQUEST_ACTIVITY_DESCRIPTION);
    }
*/

    public void post(String saveImagePath){
        Log.i("Test2", "start service");
        Intent serviceIntent = new Intent(this, PublicationVideoService.class);
        serviceIntent.putExtra("path", saveImagePath);
        startService(serviceIntent);
    }

    @Override
    public void onStart() {
        Log.i("Test2", "start filters");
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Log.i("Test2", "Stop filters");
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Log.i("Test2", "back click");

        //??
        if(progressDialog!=null && progressDialog.isShowing()){
            Log.i("Test2", "progressDialog.isShowing ");
        } else {
            Log.i("Test2", "progressDialog.is not Showing ");
        }
        super.onBackPressed();
    }
}