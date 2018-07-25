package app.mycity.mycity.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.Dialog;
import app.mycity.mycity.views.activities.ChatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DialogsRecyclerAdapter extends RecyclerView.Adapter<DialogsRecyclerAdapter.ViewHolder> {

    List<Dialog> dialogList;
    private Context context;

    public DialogsRecyclerAdapter(List<Dialog> dialogList) {
        this.dialogList = dialogList;
        Log.d("TAG", "rec created");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null){
            context =parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = dialogList.get(position).getTitle();
        DateFormat format = new SimpleDateFormat("dd.MM.yy:HH:mm");

     /*   try {
            holder.time.setText((CharSequence) format.parse(dialogList.get(position).getDate().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        holder.name.setText(name);
        holder.lastMessage.setText(dialogList.get(position).getText());

        Picasso.get().load(dialogList.get(position).getPhoto130()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dialogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dialog_row_name)
        TextView name;

        @BindView(R.id.dialogsRowImage)
        CircleImageView image;

        @BindView(R.id.dialog_row_time)
                TextView time;

        @BindView(R.id.dialog_row_message)
        TextView lastMessage;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("user_id", dialogList.get(getAdapterPosition()).getId());
                    intent.putExtra("image", dialogList.get(getAdapterPosition()).getPhoto780());
                    intent.putExtra("name", dialogList.get(getAdapterPosition()).getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void update(List<Dialog> dialogs){
        dialogList = dialogs;
        notifyDataSetChanged();
        Log.d("TAG", "update rec");
    }
}
