package app.mycity.mycity.views.fragments.feed;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.BitmapUtils;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.CheckinFragmentStateSliderAdapter;
import app.mycity.mycity.views.adapters.FeedPlacesCheckinRecyclerAdapter;
import app.mycity.mycity.views.fragments.VideoContentFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedPlacesCheckinFragmentNew2 extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placesFragmentCheckinRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.feedPhoto)
    ImageView photo;

    @BindView(R.id.placePhoto)
    ImageView placePhoto;


    @BindView(R.id.commentButton)
    ImageView commentButton;

    @BindView(R.id.likeIcon)
    ImageView likeIcon;

    @BindView(R.id.feedName)
    TextView name;

    @BindView(R.id.feedPostTime)
    TextView time;

    @BindView(R.id.commentsCount)
    TextView commentsCount;

    @BindView(R.id.likesCount)
    TextView likesCount;

    @BindView(R.id.placeName)
    TextView placeName;

    @BindView(R.id.constraintLayout6)
    ConstraintLayout buttonsContainer;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;


    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.numeration)
    TextView numeration;

    List<Post> postList;

    FeedPlacesCheckinRecyclerAdapter adapter;
    Map<String, Profile> profiles = new HashMap<String, Profile>();


    String placeId;

    String currentPostId;
    String currentOwnerId;
    String currentUserId;
    int currentPostIdPosition = -1;

    EventBusMessages.OpenPlacePhoto2 event;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;

    boolean clearScreen;

    CheckinFragmentStateSliderAdapter checkinSliderAdapter;

    public static FeedPlacesCheckinFragmentNew2 createInstance(String name, String placeId, String postId) {
        FeedPlacesCheckinFragmentNew2 fragment = new FeedPlacesCheckinFragmentNew2();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("placeId", placeId);
        bundle.putString("postId", postId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment_places_checkin_new2, container, false);

        placeId = getArguments().getString("placeId");

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        EventBus.getDefault().post(new EventBusMessages.BlackStatusBar());
        placeHolder.setVisibility(View.VISIBLE);
        View.OnClickListener openUserListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenUser(currentUserId));
            }
        };
        name.setOnClickListener(openUserListener);
        photo.setOnClickListener(openUserListener);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenComments(currentPostId, currentOwnerId, "post"));
            }
        });


        mLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new FeedPlacesCheckinRecyclerAdapter(postList);
        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG23", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= postList.size()){
                            Log.d("TAG24", "load feed from scroll listener");
                            loadMedia(postList.size());
                        }
                    }
                }
            }
        };
        Log.d("TAG24", "load feed from create");
        loadMedia(postList.size());

        recyclerView.addOnScrollListener(scrollListener);

        //super.onViewCreated(view, savedInstanceState);
    }


    private void setNumeration(int current){
        numeration.setText((current+1)+"/"+totalCount);
        recyclerView.smoothScrollToPosition(current);
    }


    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    protected void loadMedia(final int offset) {

        if(mayRestore){
            Log.d("TAG21", "RESTORE PLACE CHECKIN");
            placeHolder.setVisibility(View.GONE);
            Log.d("TAG24", "init pager from mayRestore");
            initPagerAdapter(postList);
            adapter.update(postList);
            openPlace(new EventBusMessages.ShowImage(currentPostIdPosition));
        } else {
            Log.d("TAG21", "Can not RESTORE PLACE CHECKIN ");
            ApiFactory.getApi().getGroupWallById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), placeId, "checkin", "1", "photo_130", offset, 100).enqueue(new Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseWall>> call, final Response<ResponseContainer<ResponseWall>> response) {


                    if(response.body()!= null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE FEED OK");
                        Log.d("TAG23", "RESPONSE FEED OK");

                        totalCount = response.body().getResponse().getCount();
                        Log.d("TAG23", "total count - " + totalCount);

                        if(response.body().getResponse().getGroups()!=null){
                            final Group group = response.body().getResponse().getGroups().get(0);

                            placeName.setText(response.body().getResponse().getGroups().get(0).getName());
                            Picasso.get().load(group.getPhoto130()).into(placePhoto);

                            placePhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(group.getVerified()==1){
                                        EventBus.getDefault().post(new EventBusMessages.OpenPlace(group.getId()));
                                    } else {
                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setMessage("Данная локация не имеет официальной страницы");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();
                                    }
                                }
                            });
                        }

                        postList.addAll(response.body().getResponse().getItems());

                        if(response.body().getResponse().getProfiles()!=null
                                )
                            for (Profile p: response.body().getResponse().getProfiles()
                                    ) {
                                profiles.put(p.getId(), p);
                            }

                        Log.d("TAG23", "post size - " + postList.size());


                        for (int i = 0; i < postList.size(); i++) {
                            if(postList.get(i).getId()==getArguments().getString("postId")){
                                setLiked(postList.get(i).getLikes().getUserLikes()==1);
                                likesCount.setText(String.valueOf(postList.get(i).getLikes().getCount()));
                                currentPostIdPosition = i;
                            }
                        }

                        Log.d("TAG24", "init pager from load");
                        initPagerAdapter(postList);

                        if(offset==0){
                            for (int i = 0; i < postList.size(); i++) {
                                if(postList.get(i).getId().equals(getArguments().getString("postId")))
                                    openPlace(new EventBusMessages.ShowImage(i));
                                // viewPager.setCurrentItem(i);
                            }
                        }

                        isLoading = false;

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR MAY BE NULL");
                    }

                    placeHolder.setVisibility(View.GONE);
                    adapter.update(postList);
                    //   adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                }
            });
        }
    }


    int mCurrentItem;
    private void initPagerAdapter(final List<Post> postList) {
        Log.d("TAG24", "initPagerAdapter " + postList.size());

        checkinSliderAdapter = new CheckinFragmentStateSliderAdapter(getFragmentManager(), getContext(), postList);
        viewPager.setAdapter(checkinSliderAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPostIdPosition = position;
                setNumeration(position);

                VideoContentFragment cachedFragmentLeaving = checkinSliderAdapter.getCachedItem(mCurrentItem);
                if (cachedFragmentLeaving != null) {
                    cachedFragmentLeaving.losingVisibility();
                }
                mCurrentItem = position;
                VideoContentFragment cachedFragmentEntering = checkinSliderAdapter.getCachedItem(mCurrentItem);
                if (cachedFragmentEntering != null) {
                    cachedFragmentEntering.gainVisibility();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickImage(EventBusMessages.ClickOnSliderImage event) {

        Log.d("TAG24", "click slider image");
        if(clearScreen){
            //    recyclerView.setVisibility(View.VISIBLE);
            buttonsContainer.animate().setDuration(200).translationYBy(-recyclerView.getLayoutParams().height);
            recyclerView.animate().setDuration(200).translationYBy(-recyclerView.getHeight());
            clearScreen = false;
        } else {

            buttonsContainer.animate().setDuration(200).translationY(recyclerView.getLayoutParams().height);
            recyclerView.animate().setDuration(200).translationY(recyclerView.getHeight());
            //recyclerView.setVisibility(View.GONE);
            clearScreen = true;
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.ShowImage event){

        Log.d("TAG24", "Open image " +  event.getPosition());

        currentPostIdPosition = event.getPosition();

        /**
         *         Picasso.get().load(postList.get(event.getPosition()).getAttachments().get(0).getPhoto780()).into(image);
         */

        viewPager.setCurrentItem(event.getPosition());


        Profile p = profiles.get(postList.get(event.getPosition()).getOwnerId());

        for (String s:profiles.keySet()
                ) {
            Log.d("TAG21", "pr Key - " +  s);
        }
        for (Profile profile:profiles.values()
                ) {
            Log.d("TAG21", "pr id - " +  profile.getId());
        }
        if(p==null){

            Log.d("TAG21", "post list size" +  postList.size());
            Log.d("TAG21", "profiles  size" +  profiles.size());
            Log.d("TAG21", "Profile -  " +  postList.get(event.getPosition()).getOwnerId() + " null");
        }

        Log.d("TAG21", "link 130 - " + p.getPhoto130());

        Picasso.get().load(p.getPhoto130()).into(photo);

        name.setText(p.getFirstName() + " " + p.getLastName());
        time.setText(Util.getDatePretty(postList.get(event.getPosition()).getDate()));
        likesCount.setText(String.valueOf(postList.get(event.getPosition()).getLikes().getCount()));

        currentOwnerId = postList.get(event.getPosition()).getOwnerId();
        currentPostId = postList.get(event.getPosition()).getId();
        currentUserId = postList.get(event.getPosition()).getOwnerId();

        setLiked(postList.get(event.getPosition()).getLikes().getUserLikes()==1);
        commentsCount.setText(String.valueOf(postList.get(event.getPosition()).getComments().getCount()));
    }

    void setLiked(boolean b){
        if(getContext() == null){
            return;
        }
        if(b){
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_vector_white));
        } else {
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline_vector_white));
        }
    }

    @OnClick(R.id.menuButton)
    public void menu(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);

        popupMenu.inflate(R.menu.content_menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.complain:

                                return true;
                            case R.id.copy:

                                return true;
                            case R.id.share:

                                return true;
                            case R.id.save:
                                BitmapUtils.downloadFile(postList.get(currentPostIdPosition).getAttachments().get(0).getPhotoOrig(), getContext());
                                return true;
                        }
                        return true;
                    }
                });

        popupMenu.show();
    }

    @OnClick(R.id.likeIcon)
    public void like(View v) {
        Log.d("TAG21", "Like ");
        final Post post;

        if(currentPostIdPosition == -1){
            post = event.getPost();
        } else {
            post = postList.get(currentPostIdPosition);
        }

        if (post.getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    post.getId().toString(),
                    post.getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        post.getLikes().setCount(response.body().getResponse().getLikes());
                        post.getLikes().setUserLikes(0);
                        setLiked(false);
                        likesCount.setText(String.valueOf(post.getLikes().getCount()));
                        setRightValue(post);
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (post.getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    post.getId().toString(),
                    post.getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        post.getLikes().setCount(response.body().getResponse().getLikes());
                        post.getLikes().setUserLikes(1);
                        setLiked(true);
                        likesCount.setText(String.valueOf(post.getLikes().getCount()));
                        setRightValue(post);

                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }

    private void setRightValue(Post post) {
        if(currentPostIdPosition == -1){
            for (Post p:postList
                    ) {
                if(p.getId() == post.getId()){
                    Likes likes = new Likes();
                    likes.setCount(post.getLikes().getCount());

                    p.getLikes().setCount(post.getLikes().getCount());
                    p.getLikes().setUserLikes(post.getLikes().getUserLikes());
                }
            }
            adapter.update(postList);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        storage = (Storage) context;

        postList = (List<Post>) storage.getDate(getArguments().get("name")+ "_postList");
        profiles = (Map) storage.getDate(getArguments().get("name")+ "_profiles");

        if(postList==null){
            Log.d("TAG21", "restore null");
            postList = new ArrayList<>();
            profiles = new HashMap();
        } else {
            Log.d("TAG21", "restore ok - " + postList.size());
            currentPostIdPosition = (int) storage.getDate(getArguments().get("name") + "_currentPostIdPosition");
            mayRestore = true;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d("TAG21", "Stop CHECKIN FRAGMENT Save " + getArguments().getString("name"));

      //  checkinSliderAdapter.releaseAllPlayers();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {
        if(dismissReason == TabStacker.DismissReason.REPLACED){
            storage.setDate(getArguments().get("name") + "_postList", postList);
            storage.setDate(getArguments().get("name") + "_profiles", profiles);
            storage.setDate(getArguments().get("name") + "_currentPostIdPosition", currentPostIdPosition);
        }
        if(dismissReason == TabStacker.DismissReason.BACK){
            // delete shit
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
