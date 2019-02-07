package app.mycity.mycity.views.fragments.events;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Likes;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseEventVisitors;
import app.mycity.mycity.api.model.ResponseEvents;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseVisit;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.adapters.CheckinSliderAdapter;
import app.mycity.mycity.views.adapters.EventVisitorsRecyclerAdapter;
import app.mycity.mycity.views.adapters.FeedPlacesCheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventContentFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;

    @BindView(R.id.placesFragmentCheckinRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.commentButton)
    ImageView commentButton;

    @BindView(R.id.likeIcon)
    ImageView likeIcon;

    @BindView(R.id.commentsCount)
    TextView commentsCount;

    @BindView(R.id.likesCount)
    TextView likesCount;

    @BindView(R.id.eventConfirm)
    TextView eventConfirm;

    @BindView(R.id.placeName)
    TextView placeName;

    @BindView(R.id.visitorsCount)
    TextView visitorsCount;

    @BindView(R.id.constraintLayout6)
    ConstraintLayout buttonsContainer;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.numeration)
    TextView numeration;

    @BindView(R.id.eventPhoto)
    PhotoView eventPhoto;

    List<Profile> profileList;
    Post event;

    EventVisitorsRecyclerAdapter adapter;

    String currentPostId;
    String currentOwnerId;
    String currentPlaceId;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;

    boolean clearScreen;

    Profile profile;

    public static EventContentFragment createInstance(String name, String eventId, String ownerId, String placeName, boolean backToPlace) {
        EventContentFragment fragment = new EventContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("eventId", eventId);
        bundle.putBoolean("backToPlace", backToPlace);
        bundle.putString("ownerId", ownerId);
        bundle.putString("placeName", placeName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {

        View.OnClickListener openUserListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenPlace(currentPlaceId));
            }
        };

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenComments(event.getId(), event.getOwnerId(), "event"));
            }
        });

        mLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        profileList= new ArrayList<>();
        adapter = new EventVisitorsRecyclerAdapter(profileList);
        loadVisitors();

        recyclerView.setAdapter(adapter);

        loadContent(0);

        placeName.setText(getArguments().getString("placeName"));

      /*  for (int i = 0; i < postList.size(); i++) {
            if(postList.get(i).getId()==getArguments().getString("postId")){
                setLiked(postList.get(i).getLikes().getUserLikes()==1);
                likesCount.setText(String.valueOf(postList.get(i).getLikes().getCount()));
                currentPostIdPosition = i;
            }
        }*/

        //super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        storage = (Storage) context;

        //postList = (List<Post>) storage.getDate(getArguments().get("storageKey")+ "checkins");
      //  totalCount = postList.size();
      //  groups = (HashMap<String, Group>) storage.getDate(getArguments().getString("storageKey") + "groups");
     //   profile = (Profile) storage.getDate(getArguments().getString("storageKey") + "profile");
    }

    private void loadContent(int offset) {

        if(mayRestore){
            //adapter.update(postList, groups);
        } else {
            ApiFactory.getApi().getEventsById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("eventId")).enqueue(new retrofit2.Callback<ResponseContainer<ResponseEvents>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseEvents>> call, retrofit2.Response<ResponseContainer<ResponseEvents>> response) {

                    if(response!=null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE Events OK");

                        if(response.body().getResponse().getCount()==0){
                            //placeHolderNoEvents.setVisibility(View.VISIBLE);
                        } else {
                           // placeHolderNoEvents.setVisibility(View.GONE);
                        }
                        placeHolder.setVisibility(View.GONE);

                        event = response.body().getResponse().getItems().get(0);
                        show();

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR ");
                    }

                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseEvents>> call, Throwable t) {
                }
            });
        }
    }

    void loadVisitors(){
        ApiFactory.getApi().getVisitors(App.accessToken(), getArguments().getString("eventId"), getArguments().getString("ownerId"), "photo_360").enqueue(new Callback<ResponseContainer<ResponseEventVisitors>>() {
            @Override
            public void onResponse(Call<ResponseContainer<ResponseEventVisitors>> call, Response<ResponseContainer<ResponseEventVisitors>> response) {
                Log.d("TAG21","VISITOR - event: " + getArguments().getString("eventId") + " " + "owner: " + getArguments().getString("ownerId"));

                if(response.body().getResponse().getCount()!=0){
                    profileList.addAll(response.body().getResponse().getItems());
                    adapter.update(profileList);

                    totalCount = response.body().getResponse().getCount();
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseContainer<ResponseEventVisitors>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.backButton)
    public void back(View v){

        if(getArguments().getBoolean("backToPlace")) {
            Log.d("TAG26","backToPlace");
            Log.d("TAG26","post event");
            EventBusMessages.OpenPlace openPlace = new EventBusMessages.OpenPlace(getArguments().getString("ownerId"));
            openPlace.setCloseCurrent(true);
            EventBus.getDefault().post(openPlace);
            //  getActivity().onBackPressed();
        } else {
            Log.d("TAG26","usual back");
            getActivity().onBackPressed();
        }
    }

    public void show(){
        Picasso.get().load(event.getAttachments().get(0).getPhoto780()).into(eventPhoto);
        commentsCount.setText(String.valueOf(event.getComments().getCount()));
        setLiked();
        setConfirmation();
    }

    @OnClick(R.id.eventPhoto)
    public void clickImage(View v) {

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

    void setLiked(){
        if(event.getLikes().getUserLikes()==1){
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_vector_white));
        } else {
            likeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_outline_vector_white));
        }
        likesCount.setText(String.valueOf(event.getLikes().getCount()));
    }

    void setConfirmation(){
        if(event.getVisits().getUserVisit()==1){
            eventConfirm.setBackgroundResource(R.drawable.confirm_event_on);
            eventConfirm.setTextColor(Color.WHITE);
        } else {
            eventConfirm.setBackgroundResource(R.drawable.confirm_event_off);
            eventConfirm.setTextColor(Color.parseColor("#009688"));
        }
        visitorsCount.setText(String.valueOf(event.getVisits().getCount()));
    }

    @OnClick(R.id.likeIcon)
    public void like(View view) {
       // Log.d("TAG21", "Like itemId - " + event.getItemId() + " owner - " +  postList.get(event.getAdapterPosition()).getOwnerId().toString());

        if (event.getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "event",
                    event.getId().toString(),
                    event.getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                       event.getLikes().setCount(response.body().getResponse().getLikes());
                        event.getLikes().setUserLikes(0);
                        setLiked();
                       // adapter.notifyItemChanged(event.getAdapterPosition());
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (event.getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "event",
                    event.getId().toString(),
                    event.getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseLike>> call, retrofit2.Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
                        event.getLikes().setCount(response.body().getResponse().getLikes());
                        event.getLikes().setUserLikes(1);
                        setLiked();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseLike>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }

    @OnClick(R.id.eventConfirm)
    public void confirmation(View view) {
        if (event.getVisits().getUserVisit() == 1) {
            Log.d("TAG21", "unvisit");
            ApiFactory.getApi().removeVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    event.getId().toString(),
                    event.getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        event.getVisits().setCount(response.body().getResponse().getVisitors());
                        event.getVisits().setUserVisit(0);
                        setConfirmation();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }

        if (event.getVisits().getUserVisit() == 0) {
            Log.d("TAG21", "Visit");
            ApiFactory.getApi().addVisit(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    event.getId().toString(),
                    event.getOwnerId().toString()
            ).enqueue(new retrofit2.Callback<ResponseContainer<ResponseVisit>>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseContainer<ResponseVisit>> call, retrofit2.Response<ResponseContainer<ResponseVisit>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getVisitors());
                    if (response != null && response.body() != null) {
                        event.getVisits().setCount(response.body().getResponse().getVisitors());
                        event.getVisits().setUserVisit(1);
                        setConfirmation();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<ResponseContainer<ResponseVisit>> call, Throwable t) {
                    Log.i("TAG21", "fail");
                }
            });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
  //      EventBus.getDefault().unregister(this);
        Log.d("TAG21", "Stop CHECKIN FRAGMENT Save " + getArguments().getString("name"));
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onTabFragmentPresented(TabStacker.PresentReason presentReason) {

    }

    @Override
    public void onTabFragmentDismissed(TabStacker.DismissReason dismissReason) {
        if(dismissReason == TabStacker.DismissReason.REPLACED){
          //  storage.setDate(getArguments().get("name") + "_postList", postList);
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
