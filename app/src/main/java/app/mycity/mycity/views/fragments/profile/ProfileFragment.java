package app.mycity.mycity.views.fragments.profile;

import android.Manifest;
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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponsePlaces;
import app.mycity.mycity.api.model.ResponseSavePhoto;
import app.mycity.mycity.api.model.ResponseUploadServer;
import app.mycity.mycity.api.model.ResponseUploading;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.FullViewActivity;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import io.reactivex.observers.DisposableObserver;
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

    @BindView(R.id.profileFragConstraintLayoutToolbarContainer)
    ConstraintLayout toolBar;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    @BindView(R.id.profileFragName)
    TextView name;
    @BindView(R.id.profileFragSubscriberTv)
    TextView subscribersCount;

    @BindView(R.id.profileAbout)
    TextView profileAbout;

    @BindView(R.id.profileFragSubscriptionTv)
    TextView subscriptionsCount;

    @BindView(R.id.someOneProfileFragRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.profilePlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.profileNestedScrollView)
    NestedScrollView scrollView;

    @BindView(R.id.profileFragPlacesCount)
    TextView placesCount;

    @BindView(R.id.placeContainer)
    RelativeLayout placeContainer;

    @BindView(R.id.userPlace)
    TextView userPlace;

    CheckinRecyclerAdapter adapter;
    List<Post> postList;
    MainAct activity;
    private RecyclerView.ItemDecoration spaceDecoration;
    RecyclerView.LayoutManager mLayoutManager;
    File file;
    Uri fileUri;
    ProgressDialog progressDialog;
    boolean friendLoad, checkinLoad, infoLoad;
    Storage storage;
    boolean mayRestore;
    Map groups;
    Profile profile;
    private View fragmentView;

    public void showContent(){
        if(friendLoad && checkinLoad && infoLoad){
            placeHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ProfileFragment createInstance(String name) {
        ProfileFragment fragment = new ProfileFragment();
        Log.i("TAG21", "Create Profile " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.super_new_profile_fragment, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.profilePlaces)
    public void places(View v) {
        Log.i("TAG24", "click place!");
        EventBus.getDefault().post(new EventBusMessages.OpenUserPlace(SharedManager.getProperty(Constants.KEY_MY_ID)));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT   -  Chronics");
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;

        placeHolder.setVisibility(View.VISIBLE);

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, 1);
        Util.setUnreadCount(view);


        //      imageView.setShadow(App.dpToPx(getActivity(),10));
        spaceDecoration = new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false);

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(spaceDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if(postList==null){
            postList = new ArrayList<>();
        }

        adapter = new CheckinRecyclerAdapter(postList, groups);
        adapter.setImageClickListener(this);

        recyclerView.setAdapter(adapter);

        getInfo();
        getSubscriberCount();
        getCheckins();

        Log.i("TAG21","My profile - stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3","Profile created");
    }

 /*   @OnClick(R.id.profileFragGridRecyclerLayout)
    public void gridView(View v){
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i("TAG21", "Profile onAttach ");
        activity = (MainAct) context;
        storage = (Storage) context;

        if(storage.getDate(getArguments().get("name") + "_posts")!=null)
        Log.i("TAG24", "attach PROFILE STORAGE Post size  - " + ((List<Post>)storage.getDate(getArguments().get("name") + "_posts")).size());

        postList = (List<Post>)storage.getDate(getArguments().get("name") + "_posts");
        groups = (Map) storage.getDate(getArguments().get("name")+ "_groups");

        if(postList!=null) {
            Log.i("TAG24", "attach post/  Post size " + postList.size());
            mayRestore = true;
        }

        if(groups==null){
            groups = new HashMap();
        } else{
            Log.i("TAG21", "Post null ");
            mayRestore = true;
        }

        String[] mass = (String[]) storage.getDate((String) getArguments().get("name")+"_info");
        if(mass!=null){
            name.setText(mass[0]);
            subscribersCount.setText(mass[1]);
            subscriptionsCount.setText(mass[2]);
            scrollView.setVerticalScrollbarPosition(Integer.valueOf(mass[3]));
            Log.i("TAG21", "Position  restore " + mass[3]);
        }
        //storage.setDate((String) getArguments().get("name")+"_info", mass);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.ShowImage event){
        storage.setDate(getArguments().getString("name") + "checkins", postList);
        storage.setDate(getArguments().getString("name") + "groups", groups);
        storage.setDate(getArguments().getString("name") + "profile", profile);

        if(groups==null){
            Log.d("TAG24", "Save. groups null");
        } else {
            Log.d("TAG24", "Save. groups size - " + groups.size());
            Log.d("TAG24", "Save. groups size - " + postList.size());
        }

        EventBus.getDefault().post(new EventBusMessages.OpenCheckinProfileContent(postList.get(event.getPosition()).getId(), getArguments().getString("name")));
    }



/*    public Observable<ResponseContainer<Profile>> getObservable(){
        return ApiRetrofitFactory.getApi()
                .getUser(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "photo_550,photo_130")
                .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread());
    }*/

    public DisposableObserver<ResponseContainer<Profile>> getObserver(){
        return new DisposableObserver<ResponseContainer<Profile>>() {
            @Override
            public void onNext(ResponseContainer<Profile> response) {
                Log.i("TAG21", response.getResponse().getFirstName());
                Log.i("TAG21", response.getResponse().getLastName());
                Log.i("TAG21", response.getResponse().getPhoto550());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.i("TAG21", "onComplete()");

            }
        };
    }

    private void getInfo(){
        Log.i("TAG21", "Get Info");
        ApiFactory.getApi().getUser(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), "about,photo_550,photo_130,place").enqueue(new retrofit2.Callback<ResponseContainer<Profile>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<Profile>> call, retrofit2.Response<ResponseContainer<Profile>> response) {
                profile = response.body().getResponse();
                if(profile != null){
                    Log.i("TAG", profile.getFirstName());
                    Log.i("TAG", profile.getLastName());
                    Log.i("TAG", profile.getPhoto550());

                    name.setText(profile.getFirstName() + " " + profile.getLastName());

                    infoLoad = true;
                    showContent();
                    Picasso.get().load(profile.getPhoto550()).into(imageView);
                    if(profile.getAbout().length()==0){
                        profileAbout.setVisibility(View.GONE);
                    } else {
                        profileAbout.setText(profile.getAbout());
                    }
                    Log.i("TAG21", "130 " + response.body().getResponse().getPhoto130());
                    SharedManager.addProperty(Constants.KEY_PHOTO_130, response.body().getResponse().getPhoto130());
                    if(progressDialog!=null){
                        progressDialog.hide();
                    }
                    SharedManager.addProperty(Constants.KEY_MY_FULL_NAME, profile.getFirstName() + " " + profile.getLastName());

                } else {

                }

                if(profile.getPlace()!=null){
                    placeContainer.setVisibility(View.VISIBLE);
                    userPlace.setText(profile.getPlace().getName());

                    placeContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new EventBusMessages.OpenPlace(profile.getPlace().getId()));
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<Profile>> call, Throwable t) {

            }
        });
    }

    private void getSubscriberCount(){
        ApiFactory.getApi().getSubscribersCount(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                if(users != null){
                    subscribersCount.setText(String.valueOf(users.getFriends().size()));
                }
                friendLoad = true;
                showContent();
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });

        ApiFactory.getApi().getSubscriptionsCount(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new retrofit2.Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<UsersContainer>> call, retrofit2.Response<ResponseContainer<UsersContainer>> response) {
                UsersContainer users = response.body().getResponse();

                if(users != null){
                    subscriptionsCount.setText(String.valueOf(users.getFriends().size()));
                }
                friendLoad = true;
                showContent();
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });


        ApiFactory.getApi().getPlacesByUserId(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), 0, 552, SharedManager.getProperty(Constants.KEY_MY_ID)).enqueue(new retrofit2.Callback<ResponseContainer<ResponsePlaces>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, retrofit2.Response<ResponseContainer<ResponsePlaces>> response) {
                if(response.body()!=null){
                    placesCount.setText("" + response.body().getResponse().getCount());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<ResponsePlaces>> call, Throwable t) {

            }
        });

    }

    private void getCheckins(){
        Log.i("TAG21", "Get Checkins");

        if(!mayRestore){
            Log.i("TAG24", "Cant restore checkins");
            ApiFactory.getApi().getWallExtended(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), 0, 50, "1").enqueue(new Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {
                    Log.d("TAG21", "resp = " + response.body().getResponse().getCount());

                    postList.addAll(response.body().getResponse().getItems());

                    if(response.body().getResponse().getGroups()!=null){
                        for (Group g:response.body().getResponse().getGroups()
                                ) {
                            Log.i("TAG21", "                    . GROUP - " + g.getName());
                            groups.put(g.getId(), g);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    checkinLoad = true;
                    showContent();
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                    Log.d("TAG21", "fail get wall - " + t.getLocalizedMessage());

                }
            });

        } else {
            Log.i("TAG21", "Restore checkins");
            adapter.notifyDataSetChanged();
            checkinLoad = true;
            showContent();
        }
    }


    @OnClick(R.id.profileFragSettingButtonContainer)
    public void settings(View v){
        Log.d("TAG", "SETTINGS");
        activity.startSettings(0);
    }

    @OnClick(R.id.profileFragSubscribersButton)
    public void subscribers(View v){
        Log.d("TAG21", "Start SUBSCRIBERS");
        //activity.startSubscribers();
        EventBus.getDefault().post(new EventBusMessages.OpenSubscribers(SharedManager.getProperty(Constants.KEY_MY_ID)));
    }

    @OnClick(R.id.profileFragSubscriptionsButton)
    public void subscriptions(View v){
        Log.d("TAG21", "Start SUBSCRIPTIONS");
        EventBus.getDefault().post(new EventBusMessages.OpenSubscriptions(SharedManager.getProperty(Constants.KEY_MY_ID)));
    }

    @OnClick(R.id.profileFragSetImage)
    public void setAvatar(View v){

        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            final Dialog dialog = new Dialog(getActivity());

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
                                    //pickPhoto.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                    getActivity().startActivityForResult(pickPhoto , 122);
                                    dialog.dismiss();
                                }
                            });
                            Button makePhoto = (Button) dialog.findViewById(R.id.makeAvatarPhoto);
                            makePhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    file = new File(Util.getExternalFileName());
                                    Log.d("TAG21", file.getAbsolutePath());
                                    Log.d("TAG21", file.getAbsolutePath());
                                    fileUri = Uri.fromFile(file);

                                  /*  Uri imageUri = FileProvider.getUriForFile(
                                            getContext(),
                                            "app.mycity.mycity.provider", //(use your app signature + ".provider" )
                                            file);*/
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                                    getActivity().startActivityForResult(intent, 123);

                                    dialog.dismiss();
                                }
                            });
                            Button deleteAvatar = (Button) dialog.findViewById(R.id.deleteAvatar);
                            deleteAvatar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ApiFactory.getApi().deletePhoto(App.accessToken()).enqueue(new Callback<ResponseContainer<Success>>() {
                                        @Override
                                        public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                                            getInfo();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {

                                        }
                                    });


                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            Window window = dialog.getWindow();
                            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);

        Log.d("TAG23","activity Result " + requestCode);

        switch(requestCode) {
            case 123:
                if(resultCode == RESULT_OK){

                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Обновление");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    getUploadServer();
                }
                break;
            case 122:
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


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);

        if (cursor == null) return null;
        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        //cursor.close();
        return s;
    }

    void getUploadServer(){
        Log.d("TAG21","get server " + SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN));
        Log.d("TAG23","FILE -  " + file.getAbsolutePath());
        Log.d("TAG23","FILE -  " + file.getPath());
//        Log.d("TAG23","FILE -  " + fileUri.getPath());

        ApiFactory.getApi().getUploadServerAvatar(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN)).enqueue(new Callback<ResponseContainer<ResponseUploadServer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseUploadServer>> call, Response<ResponseContainer<ResponseUploadServer>> response) {
                ResponseUploadServer uploadServer = response.body().getResponse();

                uploadFile(response.body().getResponse().getBaseUrl(),
                        response.body().getResponse().getAction(),
                        response.body().getResponse().getUserId());
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseUploadServer>> call, Throwable t) {
                Log.d("TAG21", "failure");
            }
        });
    }

    private void uploadFile(String uploadServer, String uploadAction, String userId) {
        final MultipartBody.Part filePart = MultipartBody.Part.createFormData("0", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        final RequestBody action = RequestBody.create(MediaType.parse("text/plain"), uploadAction);
        final RequestBody id = RequestBody.create(MediaType.parse("text/plain"), userId);

        ApiFactory.getmApiUploadServer(uploadServer).uploadPhoto(action, id, filePart).enqueue(new Callback<ResponseContainer<ResponseUploading>>() {
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
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Log.i("TAG23", "Stop profile fragment");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("TAG21", "Profile destroy");
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {
        Log.i("TAG21", "Fragment Presented");
    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {
        Log.i("TAG21", "Fragment Dismissed Save Data - " + getArguments().get("name"));
        Log.i("TAG21", "Scroll - " +         scrollView.getScrollY());

        if(dismissReason != TabStacker.DismissReason.BACK){
            storage.setDate( getArguments().get("name")+"_posts", postList);
            storage.setDate( getArguments().get("name")+"_groups", groups);
            String[] mass = new String[4];
            mass[0] = name.getText().toString();
            mass[1] = subscribersCount.getText().toString();
            mass[2] = subscriptionsCount.getText().toString();
            mass[3] = String.valueOf(scrollView.getScrollY());
            storage.setDate(getArguments().get("name_")+"info", mass);
        } else {
            Log.i("TAG21", "Profile Fragment Delete Data");
            storage.remove( getArguments().get("name")+"_posts");
            storage.remove(getArguments().get("name")+"_info");

            storage.remove(getArguments().getString("name") + "checkins");
            storage.remove(getArguments().getString("name") + "groups");
            storage.remove(getArguments().getString("name") + "profile");
        }

    }

    @Override
    public View onSaveTabFragmentInstance(Bundle bundle) {
        return null;
    }

    @Override
    public void onRestoreTabFragmentInstance(Bundle bundle) {

    }
}
