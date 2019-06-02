package io.github.osaigbovo.remotejobs.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


 // Custom item decoration for JobActivity RecyclerView. Adds a
 // small amount of padding to the bottom of grid items.
 class JobListItemDecoration extends RecyclerView.ItemDecoration {

    private int smallPadding;

     public JobListItemDecoration(int smallPadding) {
         this.smallPadding = smallPadding;
     }

     @Override
     public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
         outRect.bottom = smallPadding;
     }

}
