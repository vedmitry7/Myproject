package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import app.mycity.mycity.App;
import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Group;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.Profile;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewFeedRecyclerAdapter extends RecyclerView.Adapter<NewFeedRecyclerAdapter.ViewHolder> {

    List<Post> postList;
    Map<String, Profile> profiles;
    Map<String, Group> groups;

    int layout;
    Context context;
    ImageClickListener imageClickListener;

    public interface ImageClickListener{
        void onClick(int position);
    }

    public void setImageClickListener(ImageClickListener imageClickListener){
        this.imageClickListener = imageClickListener;
    }

    public NewFeedRecyclerAdapter(List<Post> postList, Map profiles, Map groups) {
        this.postList = postList;
        this.profiles = profiles;
        this.groups = groups;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(context==null)
            context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_feed_checkin_item, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if(postList.get(position).getAttachments().get(0).getType().equals("video")){
            holder.indicatorVideo.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorVideo.setVisibility(View.GONE);
        }
        Picasso.get()
                .load(postList.get(position).getAttachments()
                .get(0).getPhoto550())
              //  .resize(App.dpToPx(context, 360), App.dpToPx(context, 360))
                .placeholder(R.drawable.logo)
             //   .centerCrop()
                .into(holder.photo);

        for (Map.Entry item : groups.entrySet())
        {
            Log.d("TAG", position + " Bind - " + item.getKey() + " " + ((Group)item.getValue()).getName());
        }

        if(groups.containsKey(postList.get(position).getPlaceId())){
            String name = (groups.get(postList.get(position).getPlaceId())).getName();
            Log.d("TAG", "groups contains " + postList.get(position).getPlaceId() + " so write " + name);
            holder.place.setText((groups.get(postList.get(position).getPlaceId())).getName());
        } else {
            Log.d("TAG", "groups doesnt contains " + postList.get(position).getPlaceId());
            holder.place.setText("albumName absent");
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feedImage)
        ImageView photo;

        @BindView(R.id.indicatorVideo)
        ImageView indicatorVideo;

        @BindView(R.id.placeLabel)
        TextView place;

        @Nullable
        @BindView(R.id.likesCount)
        TextView likesCount;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Log.d("TAG21", "click albumName - " + getAdapterPosition());
                   if(getAdapterPosition()!=-1){
                       if(groups.containsKey(postList.get(getAdapterPosition()).getPlaceId())){
                           Log.d("TAG21", "click albumName contains " );
                           EventBus.getDefault().post(new EventBusMessages.OpenPlacePhoto(
                                   groups.get(postList.get(getAdapterPosition()).getPlaceId()).getId(),
                                   postList.get(getAdapterPosition()).getId())
                           );
                           EventBus.getDefault().postSticky(new EventBusMessages.OpenPlacePhoto2(
                                   groups.get(postList.get(getAdapterPosition()).getPlaceId()).getId(),
                                   postList.get(getAdapterPosition()),
                                   groups.get(postList.get(getAdapterPosition()).getPlaceId()),
                                   profiles.get(postList.get(getAdapterPosition()).getOwnerId())
                           ));
                       } else {
                           Log.d("TAG21", "click albumName not contains" );
                       }
                   } else {
                       Log.d("TAG21", "click albumName -1  - " + getAdapterPosition());
                   }
               }
           });

        }
    }

    public void update(List<Post> posts, Map profiles, Map groups){
        postList = posts;
        this.profiles = profiles;
        this.groups = groups;

        notifyDataSetChanged();
        Log.d("TAG", "update Photo recycler - " + groups.size());
    }
}
