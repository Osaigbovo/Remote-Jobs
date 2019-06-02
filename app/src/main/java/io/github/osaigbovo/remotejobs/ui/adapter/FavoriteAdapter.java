package io.github.osaigbovo.remotejobs.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.ui.viewholder.FavoriteViewHolder;
import io.github.osaigbovo.remotejobs.utils.GlideApp;

import static io.github.osaigbovo.remotejobs.utils.ViewUtil.formatDayMonth;
import static io.github.osaigbovo.remotejobs.utils.ViewUtil.getDrawableLogo;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteViewHolder> {

    private final AsyncListDiffer<FavoriteJob> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);
    private final Context context;
    private final OnFavoriteJobClickListener onFavoriteJobClickListener;
    private List<FavoriteJob> favoriteJobs;

    public interface OnFavoriteJobClickListener {
        void onJobClick(FavoriteJob favoriteJob, ImageView imageView, TextView textView);
        void onFavoredClicked(@NonNull final FavoriteJob favoriteJob, boolean isFavorite, int position);
    }

    public FavoriteAdapter(Context context, OnFavoriteJobClickListener onFavoriteJobClickListener) {
        this.context = context;
        this.onFavoriteJobClickListener = onFavoriteJobClickListener;
    }

    public void submitList(List<FavoriteJob> list) {
        this.favoriteJobs = list;
        mDiffer.submitList(list);
    }

    public FavoriteJob removeItem(int position) {
        return favoriteJobs.get(position);
    }

    public void restoreItem(FavoriteJob favoriteJob, int position) {
        favoriteJobs.add(position, favoriteJob);
        // notify item added by position
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FavoriteViewHolder(inflater.inflate(R.layout.item_favorites, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder favoriteViewHolder, int position) {
        final FavoriteJob currentJob = mDiffer.getCurrentList().get(position);

        favoriteViewHolder.itemView.setOnClickListener(view -> {
            if (onFavoriteJobClickListener != null)
                onFavoriteJobClickListener
                        .onJobClick(currentJob, favoriteViewHolder.companyLogo, favoriteViewHolder.jobTitle);
        });

        favoriteViewHolder.jobTitle.setText(currentJob.getPosition());
        favoriteViewHolder.companyName.setText(currentJob.getCompany());
        String dateFormat = formatDayMonth(context, currentJob.getDate());
        favoriteViewHolder.datePosted.setText(dateFormat);

        if (!TextUtils.isEmpty(currentJob.getCompany_logo())) {
            int radius = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
            GlideApp.with(context)
                    .load(currentJob.getCompany_logo())
                    .transform(new RoundedCorners(radius))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(favoriteViewHolder.companyLogo);
        } else {
            favoriteViewHolder.companyLogo.setImageDrawable(getDrawableLogo(currentJob.getCompany()));
        }

        favoriteViewHolder.favoriteButton.isFavorite(currentJob.isFavorite());
        favoriteViewHolder.favoriteButton.setOnClickListener(view -> {
            favoriteViewHolder.favoriteButton.toggleWishlisted();
            onFavoriteJobClickListener.onFavoredClicked(currentJob, !currentJob.isFavorite(), position);
        });
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    private static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteJob>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull FavoriteJob oldFavoriteJob, @NonNull FavoriteJob newFavoriteJob) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldFavoriteJob.getId() == newFavoriteJob.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull FavoriteJob oldFavoriteJob, @NonNull FavoriteJob newFavoriteJob) {
            // No need to check the equality for all User fields ;
            // just check the equality for fields that change the display of your item.
            return oldFavoriteJob.getId() == newFavoriteJob.getId()
                    && oldFavoriteJob.isFavorite() == newFavoriteJob.isFavorite();

        }
    };
}
