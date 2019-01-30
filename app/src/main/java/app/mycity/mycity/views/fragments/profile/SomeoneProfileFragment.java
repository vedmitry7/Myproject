package app.mycity.mycity.views.fragments.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseLike;
import app.mycity.mycity.api.model.ResponseWall;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import app.mycity.mycity.views.activities.ChatActivity;
import app.mycity.mycity.views.activities.FullViewActivity;
import app.mycity.mycity.views.activities.Storage;
import app.mycity.mycity.views.decoration.ImagesSpacesItemDecoration;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.UsersContainer;
import app.mycity.mycity.views.activities.MainAct;
import app.mycity.mycity.views.adapters.CheckinRecyclerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SomeoneProfileFragment extends Fragment implements CheckinRecyclerAdapter.ImageClickListener, TabStacker.TabStackInterface {

    @BindView(R.id.profileFragRoundImage)
    de.hdodenhof.circleimageview.CircleImageView imageView;

    @BindView(R.id.profileFragProgressBarContainer)
    ConstraintLayout progressBar;

    @BindView(R.id.profileFragToolbarTitle)
    TextView title;

    @BindView(R.id.profileFragName)
    TextView name;

    @BindView(R.id.someoneFragAdd)
    FloatingActionButton fab;

    @BindView(R.id.profileFragCurrentPointContainer)
    RelativeLayout currentPoint;

    @BindView(R.id.someOneProfileFragRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.profilePlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.profileFragSubscriberTv)
    TextView subscribersCount;

    @BindView(R.id.profileFragSubscriptionTv)
    TextView subscriptionsCount;

    @BindView(R.id.profileNestedScrollView)
    NestedScrollView scrollView;

    CheckinRecyclerAdapter adapter;

    List<Post> postList;

    MainAct activity;

    private RecyclerView.ItemDecoration spaceDecoration;

    RecyclerView.LayoutManager mLayoutManager;

    File file;
    Uri fileUri;

    ProgressDialog progressDialog;

    boolean friendLoad, checkinLoad, infoLoad, isSubscription;

    String userId;

    Storage storage;
    boolean mayRestore;

    Profile profile;
    Map groups;
    private View fragmentView;


    public void showContent() {

        if (friendLoad && checkinLoad && infoLoad) {
            placeHolder.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // keep the fragment and all its data across screen rotation
        //setRetainInstance(true);
    }



    public static SomeoneProfileFragment createInstance(String name, String userId) {
        SomeoneProfileFragment fragment = new SomeoneProfileFragment();
        Log.i("TAG21", "Create SomeoneProfileFragment " + name);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.someone_profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;

        placeHolder.setVisibility(View.VISIBLE);

        userId = getArguments().getString("userId");
        Log.i("TAG21", "USER ID " + userId);

        Util.setNawBarClickListener(view);
        Util.setNawBarIconColor(getContext(), view, -1);
        Util.setUnreadCount(view);

        //      imageView.setShadow(App.dpToPx(getActivity(),10));
        spaceDecoration = new ImagesSpacesItemDecoration(3, App.dpToPx(getActivity(), 4), false);

        mLayoutManager = new GridLayoutManager(this.getActivity(), 3);
        recyclerView.addItemDecoration(spaceDecoration);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if(postList == null){
            postList = new ArrayList<>();
        }

        adapter = new CheckinRecyclerAdapter(postList, groups);
        adapter.setImageClickListener(this);

        recyclerView.setAdapter(adapter);

        getInfo();
        getSubscriberCount();
        getCheckins();

        Log.i("TAG21", "My profile - stack count - " + getActivity().getFragmentManager().getBackStackEntryCount());
        Log.i("TAG3", "Profile created");
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusMessages.UnreadCountUpdate event){
        Util.setUnreadCount(fragmentView);
        Log.d("TAG25", "Update TOTAL UNREAD COUNT   -  Chronics");
    }

    @OnClick(R.id.backButton)
    public void back(View v){
        getActivity().onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainAct) context;

        storage = (Storage) context;
        postList = (List<Post>)storage.getDate((String) getArguments().get("name") + "_posts");
        groups = (Map) storage.getDate(getArguments().get("name")+ "_groups");


        if(postList!=null) {
            Log.i("TAG21", "Post size " + postList.size());
            mayRestore = true;
        }
        else{
            Log.i("TAG21", "Post null ");
            mayRestore = false;
        }

        if(groups==null){
            groups = new HashMap();
        }

        String[] mass = (String[]) storage.getDate((String) getArguments().get("name")+"_info");
        if(mass!=null){
            Log.i("TAG21", "Info not null");
            name.setText(mass[0]);
            subscribersCount.setText(mass[1]);
            subscriptionsCount.setText(mass[2]);
            scrollView.setVerticalScrollbarPosition(Integer.valueOf(mass[3]));
            Log.i("TAG21", "Position  restore " + mass[3]);
        } else {
            Log.i("TAG21", "Info null");
        }
        //storage.setDate((String) getArguments().get("name")+"_info", mass);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void openPlace(EventBusMessages.ShowImage event){
        storage.setDate(getArguments().getString("name") + "checkins", postList);
        storage.setDate(getArguments().getString("name") + "groups", groups);
        storage.setDate(getArguments().getString("name") + "profile", profile);

        EventBus.getDefault().post(new EventBusMessages.OpenCheckinProfileContent(postList.get(event.getPosition()).getId(), getArguments().getString("name")));
    }


    @OnClick(R.id.someoneFragAdd)
    public void addToFriends(View v) {

        if(isSubscription){
            Log.d("TAG21", "Delete subscription");
            ApiFactory.getApi().deleteSubscription(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId).enqueue(new Callback<ResponseContainer<Success>>() {
                @Override
                public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                    Log.d("TAG21", "Add friends RESP");
                    if(response.body()!=null){
                        if(response.body().getResponse().getSuccess()==1){
                            isSubscription = false;
                            fab.setImageResource(R.drawable.ic_add_subscription);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {
                    Log.d("TAG21", "Add friends FAIL");
                }
            });
        } else {
            Log.d("TAG21", "Add subscription");
            ApiFactory.getApi().addSubscription(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId).enqueue(new Callback<ResponseContainer<Success>>() {
                @Override
                public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                    Log.d("TAG21", "Add friends RESP");

                    if(response.body()!=null){
                        if(response.body().getResponse().getSuccess()==1){
                            isSubscription = true;
                            fab.setImageResource(R.drawable.ic_delete_subscription);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {
                    Log.d("TAG21", "Add friends FAIL");
                }
            });
        }


        // activity.startSettings(0);
    }

    private void getInfo() {
        Log.i("TAG21", "Get Info");
        ApiFactory.getApi().getUserById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, "photo_550,photo_130,is_subscription,is_subscriber").enqueue(new retrofit2.Callback<ResponseContainer<Profile>>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseContainer<Profile>> call, retrofit2.Response<ResponseContainer<Profile>> response) {
                profile = response.body().getResponse();
                if (profile != null) {
                    Log.i("TAG21", profile.getFirstName());
                    Log.i("TAG21", profile.getLastName());
                    Log.i("TAG21", profile.getPhoto550());

                    if(profile.getIsSubscription()==1){
                        isSubscription = true;
                        fab.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_delete_subscription));
                    } else {
                        fab.setImageResource(R.drawable.ic_add_subscription);
                    }

                    name.setText(profile.getFirstName() + " " + profile.getLastName());

                    infoLoad = true;
                    showContent();
                    Picasso.get().load(profile.getPhoto550()).into(imageView);
                    if (progressDialog != null) {
                        progressDialog.hide();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseContainer<Profile>> call, Throwable t) {

            }
        });
    }

    private void getSubscriberCount(){

        ApiFactory.getApi().getSubscribers(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("userId"), 0, "").enqueue(new Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<UsersContainer>> call, Response<ResponseContainer<UsersContainer>> response) {

                if(response.body()!= null && response.body().getResponse()!= null){
                    Log.i("TAG24", "1 count - " + response.body().getResponse().getCount());
                    subscribersCount.setText("" + response.body().getResponse().getCount());
                }
                friendLoad = true;
                showContent();
            }

            @Override
            public void onFailure(Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });

        ApiFactory.getApi().getSubscriptions(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("userId"), 0, "").enqueue(new Callback<ResponseContainer<UsersContainer>>() {
            @Override
            public void onResponse(Call<ResponseContainer<UsersContainer>> call, Response<ResponseContainer<UsersContainer>> response) {

                if(response.body()!= null && response.body().getResponse()!= null){
                    Log.i("TAG24", "2 count - " + response.body().getResponse().getCount());
                    subscriptionsCount.setText("" + response.body().getResponse().getCount());
                }
                friendLoad = true;
                showContent();
            }

            @Override
            public void onFailure(Call<ResponseContainer<UsersContainer>> call, Throwable t) {

            }
        });



    }

    private void getCheckins() {


        if(!mayRestore){
            Log.i("TAG21", "Cant restore checkins");
            ApiFactory.getApi().getWallById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), userId, "1").enqueue(new Callback<ResponseContainer<ResponseWall>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseWall>> call, Response<ResponseContainer<ResponseWall>> response) {
                    Log.d("TAG21", "resp = " + response.body().getResponse().getCount());

                    postList.addAll(response.body().getResponse().getItems());

                    if(response.body().getResponse().getGroups()!=null)
                    for (Group g:response.body().getResponse().getGroups()
                            ) {
                        Log.i("TAG21", "                    . GROUP - " + g.getName());
                        groups.put(g.getId(), g);
                    }

                    adapter.notifyDataSetChanged();
                    checkinLoad = true;
                    showContent();
                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseWall>> call, Throwable t) {
                    Log.d("TAG21", "fail get wall");
                }
            });
        } else {
            Log.i("TAG21", "Restore checkins ) ");
            adapter.notifyDataSetChanged();
            checkinLoad = true;
            showContent();
        }
    }


    @OnClick(R.id.someoneFragChat)
    public void chat(View v) {
        Log.d("TAG", "Chat ");
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("user_id",  userId);
        intent.putExtra("image", profile.getPhoto130());
        intent.putExtra("name", profile.getFirstName() + " " + profile.getLastName());
        getContext().startActivity(intent);
    }


    @OnClick(R.id.profileFragSubscribersButton)
    public void subscribers(View v){
        Log.d("TAG24", "Start SUBSCRIBERS");
        //   activity.startSubscribers();
        EventBus.getDefault().post(new EventBusMessages.OpenSubscribers(userId));

    }

    @OnClick(R.id.profileFragSubscriptionsButton)
    public void subscriptions(View v){
        Log.d("TAG24", "Start SUBSCRIPTIONS");
        EventBus.getDefault().post(new EventBusMessages.OpenSubscriptions(getArguments().getString("userId")));
    }

    @OnClick(R.id.profilePlaces)
    public void places(View v) {
        Log.i("TAG24", "click place!");
        EventBus.getDefault().post(new EventBusMessages.OpenUserPlace(userId));
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG21", "Profile Fragment resume");
        Log.i("TAG3", "Profile resume");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventBusMessages.LikePost event) {
        Log.d("TAG21", "Like " + event.getItemId());

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 1) {
            Log.d("TAG21", "unlike");
            ApiFactory.getApi().unlike(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
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

        if (postList.get(event.getAdapterPosition()).getLikes().getUserLikes() == 0) {
            Log.d("TAG21", "Like");
            ApiFactory.getApi().like(
                    SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN),
                    "post",
                    postList.get(event.getAdapterPosition()).getId().toString(),
                    postList.get(event.getAdapterPosition()).getOwnerId().toString()
            ).enqueue(new Callback<ResponseContainer<ResponseLike>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseLike>> call, Response<ResponseContainer<ResponseLike>> response) {
                    Log.i("TAG21", "resp like - " + response.body().getResponse().getLikes());
                    if (response != null && response.body() != null) {
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
    public void onClick(int position) {
        Intent intent = new Intent(getActivity(), FullViewActivity.class);
        intent.putExtra("path", postList.get(position).getAttachments().get(0).getPhotoOrig());
        intent.putExtra("postId", postList.get(position).getId().toString());
        intent.putExtra("ownerId", postList.get(position).getOwnerId());
        getActivity().startActivity(intent);
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

        Log.i("TAG21", "Fragment Dismissed Save Data " + getArguments().get("name"));
        Log.i("TAG21", "Scroll - " +         scrollView.getScrollY());

        if(dismissReason != TabStacker.DismissReason.BACK){
            storage.setDate((String) getArguments().get("name")+"_posts", postList);
            storage.setDate((String) getArguments().get("name")+"_groups", groups);
            String[] mass = new String[4];
            mass[0] = name.getText().toString();
            mass[1] = subscribersCount.getText().toString();
            mass[2] = subscriptionsCount.getText().toString();
            mass[3] = String.valueOf(scrollView.getScrollY());
            storage.setDate((String) getArguments().get("name")+"_info", mass);
        } else {
            Log.i("TAG21", "Profile Fragment Delete Data");
            storage.remove((String) getArguments().get("name")+"_posts");
            storage.remove((String) getArguments().get("name")+"_info");

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