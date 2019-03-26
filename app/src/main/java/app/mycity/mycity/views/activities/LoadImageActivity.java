package app.mycity.mycity.views.activities;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.util.SharedManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadImageActivity extends AppCompatActivity {

    File file;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    getUploadServer();
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    file = new File(getPath(selectedImage));
                    getUploadServer();
                }
                break;
        }
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor =managedQuery(uri, projection, null, null, null);

        if (cursor == null) return null;
        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public void uploadFromDevice(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
    }

    public void uploadFromCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File("/storage/emulated/0/"+"test.jpg");
        fileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 0);
    }

    void getUploadServer(){
        Log.d("TAG21","get server " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));

        ApiFactory.getApi().getUploadPhotoServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseUploadServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploadServer>> call, Response<ResponseContainer<ResponseUploadServer>> response) {
                ResponseUploadServer uploadServer = response.body().getResponse();
                uploadFile();
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploadServer>> call, Throwable t) {
                Log.d("TAG21", "failure");
            }
        });
    }

    private void uploadFile() {
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("0", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));


        final RequestBody action = RequestBody.create(MediaType.parse("text/plain"), "add_photo");
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "3");

        ApiFactory.getmApiUploadServer("http://192.168.0.104/").uploadPhoto(action, id, filePart).enqueue(new Callback<ResponseContainer<ResponseUploading>>() {
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

        ApiFactory.getApi().savePhoto(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), string, server).enqueue(new Callback<ResponseContainer<ResponseSavePhoto>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseSavePhoto>> call, Response<ResponseContainer<ResponseSavePhoto>> response) {

                ResponseSavePhoto savePhoto = response.body().getResponse();
                Log.d("TAG21", "success - " + savePhoto.getSuccess());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseSavePhoto>> call, Throwable t) {
                Log.d("TAG21","save fail");
            }
        });
    }
}
