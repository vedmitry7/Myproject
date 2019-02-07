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
import app.mycity.mycity.R;
import app.mycity.mycity.util.BitmapUtils;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FilterImageActivity extends AppCompatActivity implements FiltersListFragment.FiltersListFragmentListener, FilterEditImageFragment.EditImageFragmentListener {

    private static final String TAG = FilterImageActivity.class.getSimpleName();

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    Filter selectedFilter;
    Filter editFilter;

    Bitmap outFinalImage;
    Bitmap superOriginalImage;
    Bitmap outFilteredImage;

    Bitmap originalImage;
    Bitmap filteredImage;
    Bitmap finalImage;

    FiltersListFragment filtersListFragment;
    FilterEditImageFragment editImageFragment;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;

    File file;
    Uri fileUri;

    String path;

    long startTime;
    long finishTime;

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
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        Log.d("TAG21", "MAin OnCreate");

/*        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.activity_title_main));*/


/*        path = "/storage/emulated/0/test.jpg";
        //path = getIntent().getStringExtra("path");
        loadImage();
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);*/

        if(savedInstanceState==null){
            makeCheckin();
        } else {
            finish();
            path = getIntent().getStringExtra("path");
            loadImage();
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    protected void onResume() {
        Log.d("Test2", "RESUME FILTER" );
        super.onResume();

    }

    @OnClick(R.id.image_preview)
    public void clickImage(View v){
       ImageView.ScaleType scaleType = imagePreview.getScaleType();
        Log.d("TAG21", "ckick " + scaleType);
    }


    private void loadImage() {
        Log.d("TAG21", "path - " + path);

        Bitmap rotatedBitmap = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
        Bitmap bitmap =  null;
        Log.d("TAG21", "come - " + rotatedBitmap.getWidth() + " : " + rotatedBitmap.getHeight());


        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
            Log.d("TAG21", "e good ");
        } catch (IOException e) {
            Log.d("TAG21", "e - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Log.d("TAG21", "or - " + orientation);
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                Log.d("TAG21", "ORIENTATION 90");
                bitmap = rotateImage(rotatedBitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                Log.d("TAG21", "ORIENTATION 180");
                bitmap = rotateImage(rotatedBitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                Log.d("TAG21", "ORIENTATION 270");
                bitmap = rotateImage(rotatedBitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                bitmap = rotatedBitmap;
                Log.d("TAG21", "DEFAULT " + (bitmap==null));

        }

        originalImage = Bitmap.createScaledBitmap(bitmap,(int) bitmap.getWidth()/4, (int) bitmap.getHeight()/4, false);
        Log.d("TAG21", "S will using = " + originalImage.getWidth() + " : " + originalImage.getHeight());
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imagePreview.setImageBitmap(originalImage);


        final int maxSize = 1920;
        float outWidth;
        float outHeight;
        float inWidth = bitmap.getWidth();
        float inHeight = bitmap.getHeight();

        if(inWidth < inHeight){
            outHeight = maxSize;
            outWidth = inWidth / (inHeight/maxSize);
        } else {
            outWidth = maxSize;
            outHeight = inHeight / (inWidth/maxSize);
        }

        superOriginalImage = Bitmap.createScaledBitmap(bitmap,(int) outWidth/2, (int) outHeight/2, false);
        Log.d("TAG21", "S  super orig = " + superOriginalImage.getWidth() + " : " + superOriginalImage.getHeight());
        outFinalImage = superOriginalImage.copy(Bitmap.Config.ARGB_8888, true);
        outFilteredImage = superOriginalImage.copy(Bitmap.Config.ARGB_8888, true);
    }



    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        FilterActViewPagerAdapter adapter = new FilterActViewPagerAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FiltersListFragment();
        filtersListFragment.addImage(originalImage);
        filtersListFragment.setListener(this);

        // adding edit image fragmentp
        editImageFragment = new FilterEditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));
        adapter.addFragment(editImageFragment, getString(R.string.tab_edit));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls
        resetControls();
        //save for out image
        selectedFilter = filter;

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imagePreview.setImageBitmap(filter.processFilter(filteredImage));
        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    public void onBrightnessChanged(final int brightness) {
        brightnessFinal = brightness;
    }

    @Override
    public void onSaturationChanged(final float saturation) {
        saturationFinal = saturation;
    }

    @Override
    public void onContrastChanged(final float contrast) {
        contrastFinal = contrast;
    }

    @Override
    public void onEditStarted() {
    }

    @Override
    public void onEditCompleted() {
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
        Filter editFilter = new Filter();
        editFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        editFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        editFilter.addSubFilter(new SaturationSubfilter(saturationFinal));

        this.editFilter = editFilter;
        finalImage = editFilter.processFilter(bitmap);
        imagePreview.setImageBitmap(finalImage);

    }

    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;

        editFilter = null;

    }

    public void makeCheckin() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Util.getExternalFileName());
        Log.d("TAG21", file.getAbsolutePath());
        Log.d("TAG21", file.getAbsolutePath());
        fileUri = Uri.fromFile(file);

       /* Uri imageUri = FileProvider.getUriForFile(
                this,
                "app.mycity.mycity.provider", //(use your app signature + ".provider" )
                file);*/
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private void saveImageToGallery() {

        outFilteredImage = superOriginalImage.copy(Bitmap.Config.ARGB_8888, true);
        if(selectedFilter!=null)
            selectedFilter.processFilter(outFilteredImage);
        outFinalImage = outFilteredImage.copy(Bitmap.Config.ARGB_8888, true);

        final Bitmap bitmap = outFilteredImage.copy(Bitmap.Config.ARGB_8888, true);
        if(editFilter!=null)
        outFinalImage = editFilter.processFilter(bitmap);

        saveImagePath = Util.getExternalFileName();
        BitmapUtils.storeImage(outFinalImage, saveImagePath);

        Log.d("TAG23", "photo saved" + (System.currentTimeMillis() - startTime));
        //startDescriptionActivity(saveImagePath);
        post(saveImagePath);
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

            // clear bitmap memory\
            if (originalImage!=null){
                originalImage.recycle();
                finalImage.recycle();
                finalImage.recycle();
            }

            loadImage();
            selectedFilter = null;
            resetControls();
            if(filtersListFragment!=null){
                filtersListFragment.reloadIndicator();
                filtersListFragment.prepareThumbnail(originalImage);
    /*            TabLayout.Tab tab = tabLayout.getTabAt(0);
                tab.select();*/
            } else {
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            }
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_ACTIVITY_DESCRIPTION) {
            finish();
        }

        if(resultCode == RESULT_CANCELED && path.equals("") ){
            finish();
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

            startTime = System.currentTimeMillis();
            saveImageToGallery();
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
            saveImageToGallery();
            Log.d("TAG21", "click  ");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void publicationComplete(EventBusMessages.PublicationComplete event){
        Log.i("Test2", "Event finish dfgdfs");
        Log.d("TAG23", "load finished" + (System.currentTimeMillis() - startTime));
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
        Intent serviceIntent = new Intent(this, PublicationService.class);
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