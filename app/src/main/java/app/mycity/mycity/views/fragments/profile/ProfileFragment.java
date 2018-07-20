package app.mycity.mycity.views.fragments.profile;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.ExpandableLayout;
import app.mycity.mycity.R;
import app.mycity.mycity.SharedManager;
import app.mycity.mycity.SpacesItemDecoration;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.activities.MainActivity;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    @BindView(R.id.profileFragRoundImage)
    com.lovejjfg.shadowcircle.CircleImageView imageView;

    @BindView(R.id.profileFragProgressBarContainer)
    ConstraintLayout progressBar;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    @BindView(R.id.profileFragName)
    TextView name;
    @BindView(R.id.profileFragFriendsTv)
    TextView friendsCount;

    @BindView(R.id.profileFragCurrentPointContainer)
    RelativeLayout currentPoint;

    @BindView(R.id.someOneProfileFragRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.expandable_layout)
    ExpandableLayout expandableLayout;

    CheckinRecyclerAdapter adapter;

    List<Photo> photoList;

    MainAct activity;

    private RecyclerView.ItemDecoration spaceDecoration;

    RecyclerView.LayoutManager mLayoutManager;


    File file;
    Uri fileUri;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.super_new_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getInfo();
        getFriendsCount();
        getCheckins();
        imageView.setShadow(App.dpToPx(getActivity(),10));

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new SpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        photoList = new ArrayList<>();

        adapter = new CheckinRecyclerAdapter( photoList);
        recyclerView.setAdapter(adapter);

        spaceDecoration = new SpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false);
    }

    @OnClick(R.id.expandable_layout)
    public void onClick(View v){
        Log.i("TAG","toggle ...");
        expandableLayout.toggleExpansion();

        TextView textView = v.findViewById(R.id.labelPersonalInfo);

        if(expandableLayout.isExpanded()){
            textView.setText("Личная информация");
        } else {
            textView.setText("Скрыть");
        }
    }

    @OnClick(R.id.profileFragListRecyclerLayout)
    public void listView(View v){
        Log.i("TAG", "list view");
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        if(recyclerView.getItemDecorationCount()==1)
        recyclerView.removeItemDecorationAt(0);

        adapter.setLayout(CheckinRecyclerAdapter.LINEAR_LAYOUT);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.profileFragGridRecyclerLayout)
    public void gridView(View v){
        Log.i("TAG", "grid view");
        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        if(recyclerView.getItemDecorationCount()==0) {
            recyclerView.addItemDecoration(spaceDecoration);
        }
        recyclerView.setLayoutManager(mLayoutManager);

        adapter.setLayout(CheckinRecyclerAdapter.GRID_LAYOUT);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainAct) context;
    }


    @OnClick(R.id.profileFragBackButtonContainer)
    public void back(View v){
        getActivity().onBackPressed();
    }


    private void getInfo(){
        ApiFactory.getApi().getUser(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_780").enqueue(new retrofit2.Callback<ResponseContainer<User>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<User>> call, retrofit2.Response<ResponseContainer<User>> response) {
                User user = response.body().getResponse();
                if(user != null){
                    Log.i("TAG", user.getFirstName());
                    Log.i("TAG", user.getLastName());
                    Log.i("TAG", user.getPhoto780());

                    progressBar.setVisibility(View.GONE);
                    name.setText(user.getFirstName() + " " + user.getLastName());
                    Picasso.get().load(user.getPhoto780()).into(imageView);
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<User>> call, Throwable t) {

            }
        });
    }

    private void getFriendsCount(){

        ApiFactory.getApi().getUsers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                if(users != null){
                    friendsCount.setText(String.valueOf(users.getFriends().size()));
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

    private void getCheckins(){
        ApiFactory.getApi().getPhotosById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),SharedManager.getProperty(Constants.KEY_MY_ID), "2").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                PhotoContainer photos = response.body().getResponse();

                if(photos != null){
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));         photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));         photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    photoList.add(photos.getPhotos().get(0));
                    Log.d("TAG", "photos size = " + photoList.size());
                    adapter.update(photoList);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {

            }
        });
    }


    @OnClick(R.id.profileFragSettingButtonContainer)
    public void settings(View v){
        Log.d("TAG", "SETTINGS");
        activity.startSettings(0);

    }

    @OnClick(R.id.profileFragFriendsButton)
    public void friends(View v){
        Log.d("TAG", "FRIENDS");
        activity.startFriends();
    }

    @OnClick(R.id.profileFragSetImage)
    public void setAvatar(View v){
        final Dialog dialog = new Dialog(this.getActivity());

        //setting custom layout to dialog
        dialog.setContentView(R.layout.dialog_change_avatar);
        dialog.setTitle("Custom Dialog");

        //adding button click event
        Button chooseFromGallery = (Button) dialog.findViewById(R.id.chooseAvatarFromGallery);
        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
                dialog.dismiss();
            }
        });
        Button makePhoto = (Button) dialog.findViewById(R.id.makeAvatarPhoto);
        makePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File("/storage/emulated/0/"+"test.jpg");
                fileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 0);
                dialog.dismiss();
            }
        });
        Button deleteAvatar = (Button) dialog.findViewById(R.id.deleteAvatar);
        deleteAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);

        if (cursor == null) return null;
        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    void getUploadServer(){
        Log.d("TAG21","get server " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));

        ApiFactory.getApi().getUploadServer(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseUploadServer>>() {
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

        ApiFactory.getmApiUploadServer("http://192.168.0.104/").upload(action, id, filePart).enqueue(new Callback<ResponseContainer<ResponseUploading>>() {
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
