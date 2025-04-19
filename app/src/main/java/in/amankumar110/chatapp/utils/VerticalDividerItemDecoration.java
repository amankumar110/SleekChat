package in.amankumar110.chatapp.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalDividerItemDecoration extends RecyclerView.ItemDecoration {

    private int verticalSpace;
    private boolean spaceLastItem;

    public VerticalDividerItemDecoration(int verticalSpace,boolean spaceLastItem) {
        this.verticalSpace = verticalSpace;
        this.spaceLastItem = spaceLastItem;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        if(parent.getChildAdapterPosition(view) == state.getItemCount() - 1 && spaceLastItem)
            outRect.bottom = verticalSpace;
        else
            outRect.bottom = 0;
    }
}
