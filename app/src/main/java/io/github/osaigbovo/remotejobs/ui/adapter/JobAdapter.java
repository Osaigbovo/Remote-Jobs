package io.github.osaigbovo.remotejobs.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import io.github.osaigbovo.remotejobs.data.model.Job;
import io.github.osaigbovo.remotejobs.ui.viewholder.JobViewHolder;
import io.github.osaigbovo.remotejobs.utils.GlideApp;

import static io.github.osaigbovo.remotejobs.utils.ViewUtil.formatDayMonth;
import static io.github.osaigbovo.remotejobs.utils.ViewUtil.getDrawableLogo;

public class JobAdapter extends RecyclerView.Adapter<JobViewHolder> {

    @SuppressWarnings("unchecked")
    private final AsyncListDiffer<Job> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);
    private final OnJobClickListener onJobClickListener;
    private final Context context;

    public interface OnJobClickListener {
        void onJobClick(Job job, ImageView imageView, TextView textView);
        void onFavoredClicked(@NonNull final Job job, boolean isFavorite, int position);
    }

    public JobAdapter(Context context, OnJobClickListener onJobClickListener) {
        this.onJobClickListener = onJobClickListener;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public void submitList(List<Job> list) {
        mDiffer.submitList(list);
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder jobViewHolder, int position) {
        final Job currentJob = mDiffer.getCurrentList().get(position);

        jobViewHolder.itemView.setOnClickListener(view -> {
            if (onJobClickListener != null)
                onJobClickListener.onJobClick(currentJob, jobViewHolder.companyLogo, jobViewHolder.jobTitle);
        });

        jobViewHolder.jobTitle.setText(currentJob.getPosition());
        jobViewHolder.companyName.setText(currentJob.getCompany());
        String dateFormat = formatDayMonth(context, currentJob.getDate());
        jobViewHolder.datePosted.setText(dateFormat);

        if (!TextUtils.isEmpty(currentJob.getCompany_logo())) {
            int radius = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
            GlideApp.with(context)
                    .load(currentJob.getCompany_logo())
                    .transform(new RoundedCorners(radius))
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(jobViewHolder.companyLogo);
        } else {
            jobViewHolder.companyLogo.setImageDrawable(getDrawableLogo(currentJob.getCompany()));
        }

        jobViewHolder.favoriteButton.isFavorite(currentJob.isFavorite());
        jobViewHolder.favoriteButton.setOnClickListener(view -> {
            jobViewHolder.favoriteButton.toggleWishlisted();
            onJobClickListener.onFavoredClicked(currentJob, !currentJob.isFavorite(), position);
        });
    }

    private static final DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<Job>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull Job oldUser, @NonNull Job newUser) {
            // User properties may have changed if reloaded from the DB, but ID is fixed
            return oldUser.getId() == newUser.getId();
        }

        @Override
        public boolean areContentsTheSame(
                @NonNull Job oldUser, @NonNull Job newUser) {
            // No need to check the equality for all User fields ;
            // just check the equality for fields that change the display of your item.
            return oldUser.getId() == newUser.getId()
                    && oldUser.isFavorite() == newUser.isFavorite();

        }
    };

}
