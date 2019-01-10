package app.mycity.mycity.views.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TopPaddingDecoration extends RecyclerView.ItemDecoration {

    private int spacing;

    public TopPaddingDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if(position == 0){
         outRect.top = spacing;
        }
    }
}