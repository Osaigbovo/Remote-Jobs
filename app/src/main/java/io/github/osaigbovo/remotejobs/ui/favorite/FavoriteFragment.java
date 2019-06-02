package io.github.osaigbovo.remotejobs.ui.favorite;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.di.Injectable;
import io.github.osaigbovo.remotejobs.ui.adapter.FavoriteAdapter;
import io.github.osaigbovo.remotejobs.ui.views.CustomItemAnimator;
import io.github.osaigbovo.remotejobs.ui.views.CustomItemTouchHelperCallback;

import static io.github.osaigbovo.remotejobs.utils.ViewUtil.convertDrawableToBitmaps;

public class FavoriteFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @BindView(R.id.favorite_container) ConstraintLayout constraintLayout;
    @BindView(R.id.recycler_favorites_list) RecyclerView favoriteRecyclerView;
    @BindView(R.id.item_favorite_state) View noFavorites;
    @BindDrawable(R.drawable.ic_delete) Drawable deleteIcon;
    @BindString(R.string.favorite_job_removed) String favoriteJobRemoved;
    @BindString(R.string.undo) String undo;
    @BindColor(R.color.colorWhite) int white;
    @BindColor(R.color.colorRed) int red;
    @BindDimen(R.dimen.icon_size) float icon_size;

    private FavoriteViewModel favoriteViewModel;
    private FavoriteAdapter.OnFavoriteJobClickListener onFavoriteJobClickListener;
    private FavoriteAdapter favoriteAdapter;
    private Unbinder unbinder;

    static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    void setOnFavoriteJobClickListener(Activity activity) {
        AndroidSupportInjection.inject(this);
        onFavoriteJobClickListener = (FavoriteAdapter.OnFavoriteJobClickListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(FavoriteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setupLayoutManager_ItemDecoration();
        setupItemTouchHelper();
        setupAdapter();
        setupItemAnimation();

        return rootView;
    }

    private void setupLayoutManager_ItemDecoration() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        favoriteRecyclerView.setLayoutManager(linearLayoutManager);
        favoriteRecyclerView.addItemDecoration(
                new DividerItemDecoration(Objects.requireNonNull(this.getActivity()), LinearLayout.VERTICAL));
    }

    private void setupAdapter() {
        favoriteAdapter = new FavoriteAdapter(getActivity(), onFavoriteJobClickListener);
        favoriteViewModel.favoriteJobLiveData.observe(this, favoriteJobs -> {
            if (favoriteJobs != null && favoriteJobs.size() > 0) {
                noFavorites.setVisibility(View.GONE);
                favoriteAdapter.submitList(favoriteJobs);
            } else {
                favoriteRecyclerView.setVisibility(View.GONE);
                noFavorites.setVisibility(View.VISIBLE);
            }
        });
        favoriteRecyclerView.setAdapter(favoriteAdapter);
    }

    private void setupItemAnimation() {
        CustomItemAnimator animator = new CustomItemAnimator(newHolder ->
                favoriteAdapter.removeItem(newHolder.getAdapterPosition()));
        favoriteRecyclerView.setItemAnimator(animator);
    }

    private void setupItemTouchHelper() {
        deleteIcon.mutate();
        deleteIcon.setColorFilter(white, PorterDuff.Mode.SRC_IN);

        CustomItemTouchHelperCallback customItemTouchHelperCallback
                = new CustomItemTouchHelperCallback.Builder()
                .iconSize(dpToPx(icon_size))
                .leftBackgroundColor(red)
                .leftIcon(convertDrawableToBitmaps(deleteIcon))
                .rightBackgroundColor(red)
                .rightIcon(convertDrawableToBitmaps(deleteIcon))
                .onSwipeListener(new CustomItemTouchHelperCallback.OnSwipeListener() {
                    @Override
                    public void onSwipeRight(RecyclerView.ViewHolder vh) {
                        int position = vh.getAdapterPosition();
                        displaySnackbar(position);
                    }

                    @Override
                    public void onSwipeLeft(RecyclerView.ViewHolder vh) {
                        int position = vh.getAdapterPosition();
                        displaySnackbar(position);
                    }
                })
                .build();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(customItemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(favoriteRecyclerView);
    }

    private float dpToPx(float dp) {
        return TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void displaySnackbar(int position) {
        final FavoriteJob favoriteJob = favoriteAdapter.removeItem(position);
        favoriteViewModel.removeFavorite(favoriteJob);
        favoriteAdapter.notifyItemRemoved(position);
        favoriteAdapter.notifyDataSetChanged();

        Snackbar snackbar = Snackbar.make(constraintLayout,
                favoriteJob.getPosition() + favoriteJobRemoved,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(undo, view -> {
            // Undo is selected, restore the deleted item
            favoriteViewModel.addFavorite(favoriteJob);
            favoriteAdapter.restoreItem(favoriteJob, position);
            if (favoriteAdapter.getItemCount() != 0) {
                favoriteRecyclerView.setVisibility(View.VISIBLE);
                noFavorites.setVisibility(View.GONE);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    void clickFav(FavoriteJob favoriteJob,  boolean isFavorite){
        favoriteViewModel.setJobFavored(favoriteJob, isFavorite);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFavoriteJobClickListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
