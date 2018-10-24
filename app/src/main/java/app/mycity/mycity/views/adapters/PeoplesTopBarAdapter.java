package app.mycity.mycity.views.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import app.mycity.mycity.R;
import app.mycity.mycity.api.model.PlaceCategory;
import app.mycity.mycity.util.EventBusMessages;

public class PeoplesTopBarAdapter extends RecyclerView.Adapter<PeoplesTopBarAdapter.ViewHolder> {

    private List<PlaceCategory> placeCategoties;
    int selectedItem = 0;

    // data is passed into the constructor
    public PeoplesTopBarAdapter(List<PlaceCategory> data) {
        this.placeCategoties = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_top_bar_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PlaceCategory placeCategoty = placeCategoties.get(position);
        holder.myTextView.setText(placeCategoty.getTitle());

        if(position == selectedItem){
            holder.myTextView.setBackgroundResource(R.drawable.places_top_bar_bg_choosen);
            holder.myTextView.setTextColor(Color.WHITE);
        } else {
            holder.myTextView.setBackgroundResource(R.drawable.places_top_bar_bg);
            holder.myTextView.setTextColor(Color.parseColor("#009688"));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return placeCategoties.size();
    }

    public int getCategoryId() {
        return placeCategoties.get(selectedItem).getId();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem = getAdapterPosition();
                    notifyDataSetChanged();
                    EventBus.getDefault().post(new EventBusMessages.SortPeople(selectedItem));
                }
            });
        }

    }


    public void update(List<PlaceCategory> categoties){
        placeCategoties = categoties;
        notifyDataSetChanged();
    }
}