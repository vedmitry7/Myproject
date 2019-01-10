package app.mycity.mycity.views.fragments.feed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.FeedPlacesCheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedPlacesCheckinFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placesFragmentCheckinRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.feedPhoto)
    ImageView photo;

    @BindView(R.id.feedImage)
    ImageView image;

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

    List<Post> postList;

    FeedPlacesCheckinRecyclerAdapter adapter;
    Map<String, Profile> profiles = new HashMap<String, Profile>();


    String placeId;

    String currentPostId;
    String currentOwnerId;
    String currentUserId;
    int currentPostIdPosition;

    EventBusMessages.OpenPlacePhoto2 event;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;


    public static FeedPlacesCheckinFragment createInstance(String name, int tabPos, String placeId) {
        FeedPlacesCheckinFragment fragment = new FeedPlacesCheckinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("tabPos", tabPos);
        bundle.putString("placeId", placeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feed_fragment_places_checkin, container, false);

        placeId = getArguments().getString("placeId");

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);

        event =  EventBus.getDefault().getStickyEvent((EventBusMessages.OpenPlacePhoto2.class));

        Log.d("TAG21", "Event " + event.getProfile().getPhoto130());
        if(event.getGroup()==null)
        Log.d("TAG21", "GROUP NULL");

        Log.d("TAG21", "Event " + event.getGroup().getName());

        placeName.setText(event.getGroup().getName());
        currentPostId = String.valueOf(event.getPost().getId());
        currentPostIdPosition = -1;
        currentUserId = event.getProfile().getId();
        currentOwnerId = event.getPost().getOwnerId();


        Picasso.get().load(event.getPost().getAttachments().get(0).getPhoto780()).into(image);
        Picasso.get().load(event.getProfile().getPhoto130()).into(photo);


        name.setText(event.getProfile().getFirstName() + " " + event.getProfile().getLastName());
        likesCount.setText(String.valueOf(event.getPost().getLikes().getCount()));
        time.setText(Util.getDatePretty(event.getPost().getDate()));

        commentsCount.setText(String.valueOf(event.getPost().getComments().getCount()));


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

        setLiked(event.getPost().getLikes().getUserLikes()==1);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);

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
                        Log.d("TAG21", "ЗАГРУЗКА ДАННЫХ " + postList.size());
                        isLoading = true;
                        // load if we don't load all
                        if(totalCount >= postList.size()){
                            Log.d("TAG21", "load feed ");
                            loadMedia(postList.size());
                        }
                    }
                }
            }
        };
        loadMedia(postList.size());

        //super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    private void loadMedia(final int offset) {

        if(mayRestore){
            Log.d("TAG21", "RESTORE PLACE CHECKIN");
            adapter.update(postList);
        } else {
            Log.d("TAG21", "Can not RESTORE PLACE CHECKIN ");
            ApiFactory.getApi().getGroupWallById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), placeId, "checkin", "1", "photo_130", offset).enqueue(new Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {


                    if(response.body()!= null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE FEED OK");

                        totalCount = response.body().getResponse().getCount();

                        postList.addAll(response.body().getResponse().getItems());

                        if(response.body().getResponse().getProfiles()!=null
                                )
                            for (Profile p: response.body().getResponse().getProfiles()
                                    ) {
                                profiles.put(p.getId(), p);
                            }

                        Log.d("TAG21", "post size - " + postList.size());

                        for (Post p :postList
                             ) {
                            if(p.getId()==event.getPost().getId()){
                                setLiked(p.getLikes().getUserLikes()==1);
                                likesCount.setText(String.valueOf(p.getLikes().getCount()));
                            } else {

                            }
                        }

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR MAY BE NULL");
                    }

                    adapter.update(postList);
                    //   adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.ShowImage event){
        currentPostIdPosition = event.getPosition();
        Picasso.get().load(postList.get(event.getPosition()).getAttachments().get(0).getPhoto780()).into(image);
        Profile p = profiles.get(postList.get(event.getPosition()).getOwnerId());
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
        if(b){
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_heart_vector_red));
         //   likeIcon.setColorFilter(getResources().getColor(R.color.colorAccentRed));
          //  likesCount.setTextColor(getResources().getColor(R.color.colorAccentRed));
        } else {
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_heart_vector_grey));
         //   likeIcon.setColorFilter(getResources().getColor(R.color.grey600));
          //  likesCount.setTextColor(getResources().getColor(R.color.black_67percent));
        }
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
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
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
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
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
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
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
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
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
            mayRestore = true;
        }

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
            storage.setDate(getArguments().get("name") + "_postList", postList);
            storage.setDate(getArguments().get("name") + "_profiles", profiles);
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
