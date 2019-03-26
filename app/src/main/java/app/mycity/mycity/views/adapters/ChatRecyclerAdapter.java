package app.mycity.mycity.views.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Message;
import app.mycity.mycity.util.EventBusMessages;
import app.mycity.mycity.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ViewHolder> {

    List<Message> messages;
    private Context context;

    public ChatRecyclerAdapter(List<Message> messages) {
        this.messages = messages;
        Log.d("TAG", "rec created");
    }

    String imageUrl = "";

    public void setImageUrl(String imageUrl) {
        Log.d("TAG25", "URL IMAGE - " + imageUrl);
        this.imageUrl = imageUrl;
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

        holder.messageTime.setText(Util.getTime(messages.get(position).getTime()));

        holder.message.setText(messages.get(position).getText());
        if(holder.indicator!=null && messages.get(position).getOut()==1){
            if(!messages.get(position).isWasSended()){
                holder.indicator.setImageResource(R.drawable.ic_check);
            } else {
                holder.indicator.setImageResource(R.drawable.ic_check_double);
                if (messages.get(position).isWasRead()){
                    holder.indicator.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                } else {
                    holder.indicator.setColorFilter(context.getResources().getColor(R.color.white_50percent));
                }
            }
        }

        if(position == 0){
            holder.messageContentBottomPadding.setVisibility(View.VISIBLE);
        } else {
            holder.messageContentBottomPadding.setVisibility(View.GONE);
        }

        if(position>0){
            String tCur = Util.getDate_ddMMyyyy(messages.get(position).getTime());
            String tNext = Util.getDate_ddMMyyyy(messages.get(position-1).getTime());

            Log.d("chat22", tCur + "|" + tNext);

            if(!tCur.equals(tNext)) {
                holder.lineDeliver.setVisibility(View.VISIBLE);
                holder.dateDeliver.setVisibility(View.VISIBLE);
                holder.dateDeliver.setText(Util.getDate_ddMMyyyy(messages.get(position - 1).getTime()));
            } else {
                holder.lineDeliver.setVisibility(View.GONE);
                holder.dateDeliver.setVisibility(View.GONE);
            }
        }


        //   Log.d("TAG25", "Bind - " + messages.get(position).getText() + " id - " + messages.get(position).getId() +  " was read - " + messages.get(position).isWasRead());

        if(holder.icon!=null){
            if(!imageUrl.equals("")){
                holder.icon.setVisibility(View.VISIBLE);
                Picasso.get().load(imageUrl).into(holder.icon);
            } else {
                holder.icon.setVisibility(View.GONE);
            }
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

        @BindView(R.id.messageDeliverLine)
        View lineDeliver;
        @Nullable
        @BindView(R.id.icon)
        ImageView icon;

        @BindView(R.id.messageContentBottomPadding)
        View messageContentBottomPadding;

        @BindView(R.id.messageContent)
        CardView messageContent;

        @BindView(R.id.dateDeliver)
        TextView dateDeliver;


  /*
        @BindView(R.id.dialogsRowImage)
        CircleImageView image;

        @BindView(R.id.dialog_row_time)
                TextView time;

        @BindView(R.id.dialog_row_message)
        TextView lastMessage;*/

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
          /*  itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", userList.get(getAdapterPosition()).getFirstName() + " open profile");
                    EventBus.getDefault().post(new EventBusMessages.OpenUser(userList.get(getAdapterPosition()).getId()));
                }
            });*/

            messageContent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("TAG25", "loooooooooooooooooong click");
                    final PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
                    popupMenu.getMenuInflater().inflate(R.menu.popupmenu_message, popupMenu.getMenu());

                    MenuItem delete = popupMenu.getMenu().findItem(R.id.deleteMessage);
                    if(messages.get(getAdapterPosition()).getOut()==1){
                        delete.setVisible(true);
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                // Handle the non group menu items here

                                case R.id.copyMessage:
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("", messages.get(getAdapterPosition()).getText());
                                    clipboard.setPrimaryClip(clip);
                                    break;
                                case R.id.deleteMessage:
                                    EventBus.getDefault().post(new EventBusMessages.DeleteChatMessage(messages.get(getAdapterPosition()).getId()));
                                    break;
                            }
                            return true;
                        }
                    });

                    popupMenu.show();
                    return true;
                }
            });
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


    public void update(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
