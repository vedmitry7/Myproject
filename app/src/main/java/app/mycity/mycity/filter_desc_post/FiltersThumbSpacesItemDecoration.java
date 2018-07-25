package app.mycity.mycity.filter_desc_post;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class FiltersThumbSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public FiltersThumbSpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
            outRect.left = space;
            outRect.right = 0;
        }else{
            outRect.right = space;
            outRect.left = 0;
        }
    }
}