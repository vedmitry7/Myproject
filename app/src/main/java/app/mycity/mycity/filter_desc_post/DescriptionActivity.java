package app.mycity.mycity.filter_desc_post;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponsePostPhoto;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.util.SharedManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static okhttp3.RequestBody.create;

public class DescriptionActivity extends AppCompatActivity {

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.photoDescription)
    EditText photoDescription;

    String path;

    File file;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);


        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Описание");

        path = getIntent().getStringExtra("path");
        file = new File(path);

        Log.d("TAG21","Desc " + path);

        Bitmap bitmap =  BitmapFactory.decodeFile(path, new BitmapFactory.Options());

        imagePreview.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_desc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_publication) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Публикация");
            progressDialog.setCancelable(false);
            progressDialog.show();
            getUploadServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void getUploadServer(){
        Log.d("TAG21","get server " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));

        ApiFactory.getApi().getUploadServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseUploadServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploadServer>> call, Response<ResponseContainer<ResponseUploadServer>> response) {
                ResponseUploadServer uploadServer = response.body().getResponse();
                Log.d("TAG21", "uploadServer" + uploadServer.getServer());
                uploadFile(uploadServer.getBaseUrl());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploadServer>> call, Throwable t) {
                Log.d("TAG21", "failure");
            }
        });
    }

    private void uploadFile(String server) {
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("0", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        final RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "add_photo");
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "3");

        ApiFactory.getmApiUploadServer(server).upload(action, id, filePart).enqueue(new Callback<ResponseContainer<ResponseUploading>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploading>> call, Response<ResponseContainer<ResponseUploading>> response) {

                ResponseUploading uploading = response.body().getResponse();
                JSONArray array = new JSONArray();
                for (int i = 0; i < uploading.getPhotoList().size(); i++) {
                    try {
                        array.put(i, uploading.getPhotoList().get(i).getJson());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                savePhoto(array.toString(), uploading.getServer());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploading>> call, Throwable t) {
                Log.d("TAG21", "fail " + t.getCause());
            }
        });

        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                }).check();
    }

    void savePhoto(String string, String server){

        ApiFactory.getApi().savePost(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), string, "1", server).enqueue(new Callback<ResponseContainer<ResponseSavePhoto>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseSavePhoto>> call, Response<ResponseContainer<ResponseSavePhoto>> response) {

                ResponseSavePhoto savePhoto = response.body().getResponse();
                Log.d("TAG21", "success - " + savePhoto.getSuccess());

                String attachment = "photo3_"+savePhoto.getPhotoId();

                postPicture(attachment);
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseSavePhoto>> call, Throwable t) {
                Log.d("TAG21","save fail");
            }
        });
    }



    private void postPicture(String attachment) {

        Log.d("TAG21", "Registration " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN) +" "+ attachment);

        RequestBody token = create(
                MediaType.parse("text/plain"),
                SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));
        RequestBody ownerId = create(
                MediaType.parse("text/plain"),   "3");
        RequestBody fromgroup = create(
                MediaType.parse("text/plain"), "1");

        RequestBody placeId = create(
                MediaType.parse("text/plain"), "45");
        RequestBody message = create(
                MediaType.parse("text/plain"), photoDescription.getText().toString());
        RequestBody latitude = create(
                MediaType.parse("text/plain"), "123");
        RequestBody longitude = create(
                MediaType.parse("text/plain"), "456");
        RequestBody att = create(
                MediaType.parse("text/plain"), attachment);


        final MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("attachments", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        Log.d("TAG21", "api - ");

        try {
            ApiFactory.getApi().postPicture(token, placeId, message, latitude, longitude, att).enqueue(new Callback<ResponseContainer<ResponsePostPhoto>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponsePostPhoto>> call, Response<ResponseContainer<ResponsePostPhoto>> response) {
                    Log.d("TAG21", "response - ");

                    Log.d("TAG21", "resp != null " + String.valueOf(response != null));
                 //   Log.d("TAG21", "resp resp != null " + String.valueOf(response.body().getResponse() != null));

                    ResponsePostPhoto  responsePostPhoto = response.body().getResponse();
                    Log.d("TAG21", "Post id - " + responsePostPhoto.getPostId());
                    progressDialog.hide();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponsePostPhoto>> call, Throwable t) {
                    Log.d("TAG21", "fail ");

                }
            });
        } catch (Exception e){
            Log.d("TAG21", "e " + e.getLocalizedMessage());
            Log.d("TAG21", "e " + e.getCause());

        }


    }

}
