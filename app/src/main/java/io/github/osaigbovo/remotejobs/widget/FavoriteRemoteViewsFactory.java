package io.github.osaigbovo.remotejobs.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.List;

import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.data.repository.JobRepository;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static io.github.osaigbovo.remotejobs.ui.jobdetail.DetailActivity.ARG_DETAIL_JOB;
import static io.github.osaigbovo.remotejobs.utils.ViewUtil.drawableToBitmap;
import static io.github.osaigbovo.remotejobs.utils.ViewUtil.getDrawableLogo;

class FavoriteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final JobRepository jobRepository;
    private List<FavoriteJob> favoriteJobs;
    private CompositeDisposable compositeDisposable;
    private final Context mContext;

    FavoriteRemoteViewsFactory(Context context, Intent intent, JobRepository jobRepository) {
        mContext = context;
        int mAppWidgetId = intent
                .getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.jobRepository = jobRepository;
    }

    @Override
    public void onCreate() {
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        favoriteJobs = jobRepository.getFavoriteListForWidget().blockingLast();
        Timber.e(String.valueOf(favoriteJobs.size()));

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        favoriteJobs = null;
        compositeDisposable.clear();
    }

    @Override
    public int getCount() {
        return favoriteJobs == null ? 0 : favoriteJobs.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION || favoriteJobs == null || favoriteJobs.get(position) == null)
            return null;

        Timber.e("{ Data: %s, %s, %s, %s }", position, favoriteJobs.get(position).getId(),
                favoriteJobs.get(position).getPosition() != null ? favoriteJobs.get(position).getPosition() : "---",
                favoriteJobs.get(position).getCompany() != null ? favoriteJobs.get(position).getCompany() : "---");

        // Bind data to remoteViews
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        remoteViews.setTextViewText(R.id.widget_position, favoriteJobs.get(position).getPosition());
        remoteViews.setTextViewText(R.id.widget_company, favoriteJobs.get(position).getCompany());

        try {
            if (!TextUtils.isEmpty(favoriteJobs.get(position).getCompany_logo())) {
                int radius = mContext.getResources().getDimensionPixelSize(R.dimen.corner_radius);
                Bitmap bitmap = Glide.with(mContext.getApplicationContext())
                        .asBitmap()
                        .transform(new RoundedCorners(radius))
                        .placeholder(R.color.textPrimary)
                        .load(favoriteJobs.get(position).getCompany_logo())
                        .submit(512, 512)
                        .get();
                remoteViews.setImageViewBitmap(R.id.widget_company_logo, bitmap);
            } else {
                remoteViews.setImageViewBitmap(R.id.widget_company_logo,
                        drawableToBitmap(getDrawableLogo(favoriteJobs.get(position).getCompany())));
            }

        } catch (Exception e) {
            Timber.e(e);
        }

        Bundle extras = new Bundle();
        extras.putParcelable(ARG_DETAIL_JOB, favoriteJobs.get(position));

        Intent intent = new Intent();
        intent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.widget_list_item, intent);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
