package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.Constants;
import app.mycity.mycity.R;
import app.mycity.mycity.api.ApiFactory;
import app.mycity.mycity.api.model.Notification;
import app.mycity.mycity.api.model.Post;
import app.mycity.mycity.api.model.ResponseContainer;
import app.mycity.mycity.api.model.Success;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.SharedManager;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder> {

    List<Notification> notifications;
    private Context context;

    public NotificationRecyclerAdapter(List<Notification> notifications) {
        this.notifications = notifications;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null){
            context =parent.getContext();
        }
        View view = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_follow_row, parent, false);
                break;
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_like_post_row, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_like_comment_row, parent, false);
                break;
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_comment_post_row, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_comment_post_row, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Notification notification = notifications.get(position);

        holder.time.setText(Util.getDatePretty(notification.getDate()));
        holder.name.setText(notification.getFeedback().getFirstName() + " " + notification.getFeedback().getLastName());
        Picasso.get().load(notification.getFeedback().getPhoto130()).into(holder.photo);

        if(notification.getType().equals("follow")){
            if(notifications.get(position).getFeedback().getIsSubscription()==1){
                holder.subscribeButton.setText("Вы подписаны");
                holder.subscribeButton.setClickable(false);
            } else {
                holder.subscribeButton.setText("Подписаться");
            }
        }

        if(notification.getType().equals("like_post")){
            Picasso.get().load(notification.getParents().get(0).getAttachments().get(0).getPhoto360()).into(holder.contentImage);
        }
        if(notification.getType().equals("like_comment")){
            holder.commentText.setText(notification.getParents().get(0).getText());
            holder.mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenComments(notifications.get(position).getParents().get(0).getId(), notifications.get(position).getParents().get(0).getOwnerId(), "post"));
                }
            });
        }
        if(notification.getType().equals("comment_post")){
            Picasso.get().load(notification.getParents().get(0).getAttachments().get(0).getPhoto360()).into(holder.contentImage);
            holder.commentText.setText(notification.getFeedback().getComment().getText());
            Log.d("TAG21", "Comment post - " + notification.getFeedback().getComment().getText());
            holder.mainContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenComments(notifications.get(position).getParents().get(0).getId(), notifications.get(position).getParents().get(0).getOwnerId(), "post"));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (notifications.get(position).getType()){
            case "follow":
                return 0;
            case "like_post":
                return 1;
            case "like_comment":
                return 2;
            case "comment_post":
                return 3;
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.mainContainer)
        ConstraintLayout mainContainer;

        @Nullable
        @BindView(R.id.userName)
        TextView name;
        @Nullable
        @BindView(R.id.eventTime)
        TextView time;
        @Nullable
        @BindView(R.id.commentText)
        TextView commentText;
        @Nullable
        @BindView(R.id.userPhoto)
        ImageView photo;
        @Nullable
        @BindView(R.id.contentImage)
        ImageView contentImage;
        @Nullable
        @BindView(R.id.subscribeButton)
        Button subscribeButton;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            View.OnClickListener openUser = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(notifications.get(getAdapterPosition()).getFeedback().getId()));
                }
            };

            photo.setOnClickListener(openUser);
            name.setOnClickListener(openUser);

            if(subscribeButton!=null){
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ApiFactory.getApi().addSubscription(SharedManager.getProperty(Constants.KEY_ACCESS_TOKEN), notifications.get(getAdapterPosition()).getFeedback().getId()).enqueue(new Callback<ResponseContainer<Success>>() {
                            @Override
                            public void onResponse(Call<ResponseContainer<Success>> call, Response<ResponseContainer<Success>> response) {
                                Log.d("TAG21", "Add friends RESP");

                                if(response.body()!=null){
                                    if(response.body().getResponse().getSuccess()==1){
                                        subscribeButton.setText("Вы подписаны");
                                        subscribeButton.setClickable(false);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseContainer<Success>> call, Throwable t) {
                                Log.d("TAG21", "Add friends FAIL");
                            }
                        });
                    }
                });
            }
        }
    }

    public void update(List<Notification> dialogs){
        notifications = dialogs;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
