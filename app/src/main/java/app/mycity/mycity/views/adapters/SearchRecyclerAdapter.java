package app.mycity.mycity.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.util.EventBusMessages;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> {

    private List<String> names;
    private List<String> images;

    // data is passed into the constructor
    public SearchRecyclerAdapter(List<String> data) {
        this.names = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = names.get(position);
        holder.myTextView.setText(animal);

        if(images.get(position)!=null)
        Picasso.get().load(images.get(position)).into(holder.image);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return names.size();
    }

    public void update(List<String> list) {
        this.names = list;
        notifyDataSetChanged();
    }

    public void update2(List<String> searchResult, List<String> groupImage) {
        names = searchResult;
        images = groupImage;
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.placeImage)
        CircleImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            myTextView = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusMessages.ClickItem(getAdapterPosition()));
                }
            });
        }

        TextView myTextView;

    }
}