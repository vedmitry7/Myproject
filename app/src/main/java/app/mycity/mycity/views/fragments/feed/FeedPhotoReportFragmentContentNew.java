package app.mycity.mycity.views.fragments.feed;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.On;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Photo;
import app.mycity.mycity.api.model.PhotoContainer;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.AlbumSliderAdapter;
import app.mycity.mycity.views.adapters.CheckinSliderAdapter;
import app.mycity.mycity.views.adapters.FeedPhotoReportContentAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedPhotoReportFragmentContentNew extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placesFragmentCheckinRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.feedPhoto)
    ImageView photo;

    @BindView(R.id.pager)
    ViewPager viewPager;

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

    @BindView(R.id.numeration)
    TextView numeration;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.constraintLayout6)
    ConstraintLayout buttonsContainer;

    List<Photo> photoList;

    Group group;

    FeedPhotoReportContentAdapter adapter;
   // Map<String, Profile> profiles = new HashMap<String, Profile>();


    String placeId;

    String currentPostId;
   // String currentOwnerId;
   // String currentUserId;
    int currentPostIdPosition;

  //  EventBusMessages.OpenPlacePhoto2 event;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;
    boolean clearScreen;


    public static FeedPhotoReportFragmentContentNew createInstance(String name, int tabPos, String placeId, String albumId, String albumName, Long albumDate, int position) {
        FeedPhotoReportFragmentContentNew fragment = new FeedPhotoReportFragmentContentNew();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("tabPos", tabPos);
        bundle.putString("placeId", placeId);
        bundle.putString("albumId", albumId);
        bundle.putString("albumName", albumName);
        bundle.putLong("albumDate", albumDate);
        bundle.putInt("position", position);

        fragment.setArguments(bundle);
        return fragment;
    }

    @OnClick(R.id.backButton)
    public void backButton(View v){
        getActivity().onBackPressed();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_photo_report_content_new, container, false);

        placeId = getArguments().getString("placeId");

        ButterKnife.bind(this, view);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clickImage(EventBusMessages.ClickOnSliderImage event) {

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

//        Util.setNawBarClickListener(view);
  //      Util.setNawBarIconColor(getContext(), view, -1);


        time.setText(Util.getDatePretty(getArguments().getLong("albumDate")));
        placeName.setText(getArguments().getString("albumName"));

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);

        adapter = new FeedPhotoReportContentAdapter(photoList);
        recyclerView.setAdapter(adapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleItems = mLayoutManager.findLastVisibleItemPosition();

                if (!isLoading) {
                    if ( lastVisibleItems >= totalItemCount -10 ) {
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + photoList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= photoList.size()){
                            Log.d("TAG21", "load feed ");
                            loadMedia(photoList.size());
                        }
                    }
                }
            }
        };
        loadMedia(photoList.size());


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenComments(currentPostId, photoList.get(currentPostIdPosition).getOwnerId(), "photo"));
            }
        });
    }

    private void loadMedia(final int offset) {

        if(mayRestore){
            Log.d("TAG21", "RESTORE PLACE CHECKIN");
            adapter.update(photoList);
        } else {
            Log.d("TAG21", "Can not RESTORE PLACE CHECKIN ");
            ApiFactory.getApi().getAlbum(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "12321", getArguments().getString("albumId"), "1").enqueue(new Callback<ResponseContainer<PhotoContainer>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(Call<ResponseContainer<PhotoContainer>> call, Response<ResponseContainer<PhotoContainer>> response) {
                    if(response.body().getResponse()!=null){
                        Log.d("TAG24", "Album " + " size - " + response.body().getResponse().getPhotos().size());
                        photoList.addAll(response.body().getResponse().getPhotos());
                        adapter.update(photoList);
                        recyclerView.scrollToPosition(getArguments().getInt("position"));
                        placeHolder.setVisibility(View.GONE);

                        totalCount = response.body().getResponse().getCount();

                        AlbumSliderAdapter albumSliderAdapter = new AlbumSliderAdapter(getContext(), photoList);
                        viewPager.setAdapter(albumSliderAdapter);

                        //Picasso.get().load(photoList.get(getArguments().getInt("position")).getPhoto780()).into(image);

                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                setNumeration(position);
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {
                            }
                        });

                        if(response.body().getResponse().getGroups()!=null){
                            Log.d("TAG24", "Groups GET");
                            group = response.body().getResponse().getGroups().get(0);
                            name.setText(group.getName());
                            Picasso.get().load(group.getPhoto130()).into(photo);
                        } else {
                            Log.d("TAG24", "Groups NOT GET");
                        }


                        setInfo(new EventBusMessages.ShowImage(getArguments().getInt("position")));

                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<PhotoContainer>> call, Throwable t) {
                }
            });
        }

    }

    @OnClick(R.id.likeIcon)
    public void like(View v) {
        Log.d("TAG21", "Like ");
        final Photo photo = photoList.get(currentPostIdPosition);

        if (photo.getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "photo",
                    photo.getId().toString(),
                    photo.getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        photo.getLikes().setCount(response.body().getResponse().getLikes());
                        photo.getLikes().setUserLikes(0);
                        setLiked(false);
                        likesCount.setText(String.valueOf(photo.getLikes().getCount()));
                     //   setRightValue(photo);
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (photo.getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "photo",
                    photo.getId().toString(),
                    photo.getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        photo.getLikes().setCount(response.body().getResponse().getLikes());
                        photo.getLikes().setUserLikes(1);
                        setLiked(true);
                        likesCount.setText(String.valueOf(photo.getLikes().getCount()));
                    //    setRightValue(photo);

                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }

    void setLiked(boolean b){
        if(b){
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_vector_white));
          //  likeIcon.setColorFilter(getResources().getColor(R.color.colorAccentRed));
           // likesCount.setTextColor(getResources().getColor(R.color.colorAccentRed));
        } else {
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline_vector_white));
           // likeIcon.setColorFilter(getResources().getColor(R.color.grey600));
          //  likesCount.setTextColor(getResources().getColor(R.color.black_67percent));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        storage = (Storage) context;

       // photoList = (List<Photo>) storage.getDate(getArguments().get("name")+ "_postList");

        if(photoList ==null){
            Log.d("TAG21", "restore null");
            photoList = new ArrayList<>();
        } else {
            Log.d("TAG21", "restore ok - " + photoList.size());
            mayRestore = true;
        }
    }

    private void setNumeration(int current){
        numeration.setText((current+1)+"/"+totalCount);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setInfo(EventBusMessages.ShowImage event){

        setNumeration(event.getPosition());

        currentPostId = photoList.get(event.getPosition()).getId();
        viewPager.setCurrentItem(event.getPosition());
       //Picasso.get().load(photoList.get(event.getPosition()).getPhoto780()).into(image);
        currentPostIdPosition = event.getPosition();
       likesCount.setText(String.valueOf(photoList.get(event.getPosition()).getLikes().getCount()));
       if(photoList.get(event.getPosition()).getLikes().getUserLikes()==1){
           setLiked(true);
       } else {
           setLiked(false);
       }

       commentsCount.setText(String.valueOf(photoList.get(event.getPosition()).getComments().getCount()));
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        Log.d("TAG21", "Stop CHECKIN FRAGMENT Save " + getArguments().getString("name"));
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
