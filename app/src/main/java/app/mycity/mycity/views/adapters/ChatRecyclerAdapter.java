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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.views.activities.ChatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {

    RealmResults<Message> messages;
    private Context context;

    public ChatRecyclerAdapter(RealmResults<Message> messages) {
        this.messages = messages;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(context==null){
            context = parent.getContext();
        }
        View view = null;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.someome_message_row, parent, false);
        }
        if ((viewType == 1)){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message_row, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormat format = new SimpleDateFormat("HH:mm");

      /*  try {
            holder.messageTime.setText((CharSequence) format.parse(String.valueOf(messages.get(position).getTime()+"000")));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
     //   holder.messageTime.setText(String messages.get(position).getTime());

        holder.message.setText(messages.get(position).getText());
        if(holder.indicator!=null && messages.get(position).getOut()==1){
            if (messages.get(position).isWasRead()){
                holder.indicator.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            } else {
                holder.indicator.setColorFilter(context.getResources().getColor(R.color.main_grey_color));
            }
        }

        if(holder.avatar!=null){
            Picasso.get().load(ChatActivity.imageUrl).into(holder.avatar);
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

     @BindView(R.id.message_text)
     TextView message;
     @BindView(R.id.messageTime)
     TextView messageTime;
     @Nullable
     @BindView(R.id.message_indicator)
     ImageView indicator;

     @Nullable
     @BindView(R.id.icon)
     ImageView avatar;


  /*
        @BindView(R.id.dialogsRowImage)
        CircleImageView image;

        @BindView(R.id.dialog_row_time)
                TextView time;

        @BindView(R.id.dialog_row_message)
        TextView lastMessage;*/

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", userList.get(getAdapterPosition()).getFirstName() + " open profile");
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(userList.get(getAdapterPosition()).getId()));
                }
            });*/
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getOut()==1){
            return 1;
        }
        if(messages.get(position).getOut()==0){
            return 0;
        }

        return super.getItemViewType(position);
    }


    public void update(RealmResults<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
