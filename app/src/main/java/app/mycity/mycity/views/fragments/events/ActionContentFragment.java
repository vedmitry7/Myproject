package app.mycity.mycity.views.fragments.events;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.ResponseEvents;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.views.activities.Storage;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.arnaudguyon.tabstacker.TabStacker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionContentFragment extends android.support.v4.app.Fragment implements TabStacker.TabStackInterface {

    LinearLayoutManager mLayoutManager;


    @BindView(R.id.placeName)
    TextView placeName;

    @BindView(R.id.progressBarPlaceHolder)
    ConstraintLayout placeHolder;

    @BindView(R.id.numeration)
    TextView numeration;

    @BindView(R.id.eventPhoto)
    PhotoView eventPhoto;

    List<Profile> profileList;
    Post event;

    String currentPostId;
    String currentOwnerId;
    String currentPlaceId;

    boolean isLoading;
    int totalCount;

    Storage storage;
    private boolean mayRestore;

    boolean clearScreen;

    Profile profile;

    public static ActionContentFragment createInstance(String name, String eventId, String ownerId, boolean backToPlace) {
        ActionContentFragment fragment = new ActionContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("eventId", eventId);
        bundle.putBoolean("backToPlace", backToPlace);
        bundle.putString("ownerId", ownerId);

        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.action_content, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Log.d("TAG21", "ACTION CONTENT");

        EventBus.getDefault().post(new EventBusMessages.BlackStatusBar());

        View.OnClickListener openUserListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusMessages.OpenPlace(currentPlaceId));
            }
        };

        loadContent(0);

     //   placeName.setText(getArguments().getString("placeName"));

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
            ApiFactory.getApi().getActionById(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), getArguments().getString("eventId"), "1").enqueue(new Callback<ResponseContainer<ResponseEvents>>() {
                @Override
                public void onResponse(Call<ResponseContainer<ResponseEvents>> call, Response<ResponseContainer<ResponseEvents>> response) {

                    if(response!=null && response.body().getResponse()!=null){
                        Log.d("TAG21", "RESPONSE Events OK");

                        if(response.body().getResponse().getCount()==0){
                            //placeHolderNoEvents.setVisibility(View.VISIBLE);
                        } else {
                           // placeHolderNoEvents.setVisibility(View.GONE);
                        }
                        placeHolder.setVisibility(View.GONE);

                        event = response.body().getResponse().getItems().get(0);

                        if(response.body().getResponse().getGroups()!=null)
                            placeName.setText(response.body().getResponse().getGroups().get(0).getName());

                        Picasso.get().load(response.body().getResponse().getItems().get(0).getAttachments().get(0).getPhoto780()).into(eventPhoto);

                    } else {
                        Log.d("TAG21", "RESPONSE ERROR ");
                    }

                }

                @Override
                public void onFailure(Call<ResponseContainer<ResponseEvents>> call, Throwable t) {
                }
            });
        }
    }

    @OnClick(R.id.backButton)
    public void back(View v){
        if(getArguments().getBoolean("backToPlace")) {
            EventBusMessages.OpenPlace openPlace = new EventBusMessages.OpenPlace(getArguments().getString("ownerId"));
            openPlace.setCloseCurrent(true);
            openPlace.setTabPos(4);
            EventBus.getDefault().post(openPlace);
        } else {
            getActivity().onBackPressed();
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
