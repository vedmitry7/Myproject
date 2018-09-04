package app.mycity.mycity.views.fragments.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.filter_desc_post.ExpandableLayout;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.FullViewActivity;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.api.model.User;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements CheckinRecyclerAdapter.ImageClickListener, TabStacker.TabStackInterface{

    @BindView(R.id.profileFragRoundImage)
    de.hdodenhof.circleimageview.CircleImageView imageView;

    @BindView(R.id.profileFragProgressBarContainer)
    ConstraintLayout progressBar;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    @BindView(R.id.profileFragName)
    TextView name;
    @BindView(R.id.profileFragFriendsTv)
    TextView friendsCount;

    @BindView(R.id.checkinCount)
    TextView checkinCount;

    @BindView(R.id.profileFragCurrentPointContainer)
    RelativeLayout currentPoint;

    @BindView(R.id.someOneProfileFragRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.expandable_layout)
    ExpandableLayout expandableLayout;

    @BindView(R.id.profilePlaceHolder)
            ConstraintLayout placeHolder;

    CheckinRecyclerAdapter adapter;

    List<Post> postList;

    MainAct activity;

    private RecyclerView.ItemDecoration spaceDecoration;

    RecyclerView.LayoutManager mLayoutManager;

    File file;
    Uri fileUri;

    ProgressDialog progressDialog;

    boolean friendLoad, checkinLoad, infoLoad;

    public void showContent(){

        if(friendLoad && checkinLoad && infoLoad){
            placeHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep the fragment and all its data across screen rotation
        //setRetainInstance(true);
    }

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
        placeHolder.setVisibility(View.VISIBLE);

  //      imageView.setShadow(App.dpToPx(getActivity(),10));

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        postList = new ArrayList<>();

        adapter = new CheckinRecyclerAdapter(postList);
        adapter.setImageClickListener(this);
        recyclerView.setAdapter(adapter);

        spaceDecoration = new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false);


        getInfo();
        getFriendsCount();
        getCheckins();


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
            }
        }, 100);


        Log.i("TAG21","My profile - stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3","Profile created");
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
        Log.i("TAG21", "Get Info");
        ApiFactory.getApi().getUser(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_780,photo_130").enqueue(new retrofit2.Callback<ResponseContainer<User>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<User>> call, retrofit2.Response<ResponseContainer<User>> response) {
                User user = response.body().getResponse();
                if(user != null){
                    Log.i("TAG", user.getFirstName());
                    Log.i("TAG", user.getLastName());
                    Log.i("TAG", user.getPhoto780());

                    name.setText(user.getFirstName() + " " + user.getLastName());

                    infoLoad = true;
                    showContent();
                    Picasso.get().load(user.getPhoto780()).into(imageView);
                    Log.i("TAG21", "130 " + response.body().getResponse().getPhoto130());
                    SharedManager.addProperty(Constants.KEY_PHOTO_130, response.body().getResponse().getPhoto130());
                    if(progressDialog!=null){
                        progressDialog.hide();
                    }
                    SharedManager.addProperty(Constants.KEY_MY_FULL_NAME, user.getFirstName() + " " + user.getLastName());

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

                friendLoad = true;

                showContent();

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });
    }

    private void getCheckins(){

        ApiFactory.getApi().getWall(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseWall>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {
                Log.d("TAG21", "resp = " + response.body().getResponse().getCount());

                postList.addAll(response.body().getResponse().getItems());

                checkinCount.setText(String.valueOf(response.body().getResponse().getCount()));


           /*     for (Post p:response.body().getResponse().getItems()
                     ) {
                    photoList.add(p.getAttachments().get(0));
                    likeList.add(p.getLikes());
                }*/
                adapter.notifyDataSetChanged();
                checkinLoad = true;
                showContent();
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                Log.d("TAG21", "fail get wall");
            }
        });


        /*      ApiFactory.getApi().getPhotosById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),SharedManager.getProperty(Constants.KEY_MY_ID), "4").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                PhotoContainer photos = response.body().getResponse();
                Log.d("TAG21", "ph count = " + photos.getCount());

                if(photos != null){
                    photoList.addAll(photos.getPhotos());
                    Log.d("TAG21", "photos size = " + photoList.size());
                    adapter.update(photoList);
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {

            }
        });*/
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

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Обновление");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    getUploadServer();
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    file = new File(getPath(selectedImage));
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Обновление");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
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

                if(savePhoto.getSuccess()== 1){
                    getInfo();
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseSavePhoto>> call, Throwable t) {
                Log.d("TAG21","save fail");
            }
        });
    }

    @Override
    public void onClick(int position) {
        Log.i("TAG21", "post id " + postList.get(position).getId());
        Log.i("TAG21", "owner id " + postList.get(position).getOwnerId());
        Intent intent = new Intent(getActivity(), FullViewActivity.class);
        intent.putExtra("path", postList.get(position).getAttachments().get(0).getPhotoOrig());
        intent.putExtra("postId", postList.get(position).getId().toString());
        intent.putExtra("ownerId", postList.get(position).getOwnerId());
        getActivity().startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG21", "Profile Fragment resume");
        Log.i("TAG3","Profile resume");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event){
        Log.d("TAG21","Like " + event.getItemId());

        if(postList.get(event.getAdapterPosition()).getLikes().getUserLikes()==1){
            Log.d("TAG21","unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if(response != null && response.body()!=null){
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(0);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }

                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if(postList.get(event.getAdapterPosition()).getLikes().getUserLikes()==0) {
            Log.d("TAG21","Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if(response != null && response.body()!=null){
                        postList.get(event.getAdapterPosition()).getLikes().setCount(response.body().getResponse().getLikes());
                        postList.get(event.getAdapterPosition()).getLikes().setUserLikes(1);
                        adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

/*
        RequestBody body = new FormBody.Builder()
                .add("access_token", SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN))
                .add("type", "post")
                .add("item_id", postList.get(event.getAdapterPosition()).getId().toString())
                .add("owner_id", postList.get(event.getAdapterPosition()).getOwnerId().toString())
                .build();

        Request request = new Request.Builder().url("http://192.168.0.104/api/likes.add")
                .post(body)
                .build();

        okhttp3.Response response = null;
        OkHttpClientFactory.getClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                Log.i("TAG21", responseString);
                String responseClear = responseString.substring(1,responseString.length()-1);
                Log.i("TAG21", responseClear);
            }
        });*/

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {

    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }
}
