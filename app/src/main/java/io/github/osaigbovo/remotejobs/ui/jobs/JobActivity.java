package io.github.osaigbovo.remotejobs.ui.jobs;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.model.Job;
import io.github.osaigbovo.remotejobs.ui.about.AboutActivity;
import io.github.osaigbovo.remotejobs.ui.adapter.JobAdapter;
import io.github.osaigbovo.remotejobs.ui.favorite.FavoriteActivity;
import io.github.osaigbovo.remotejobs.ui.jobdetail.DetailActivity;
import io.github.osaigbovo.remotejobs.utils.DbUtil;

import static io.github.osaigbovo.remotejobs.ui.jobdetail.DetailActivity.ARG_DETAIL_JOB;

public class JobActivity extends AppCompatActivity implements JobAdapter.OnJobClickListener {

    private static FirebaseAnalytics firebaseAnalytics;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private JobViewModel jobViewModel;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView_main)
    RecyclerView mainRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindDrawable(R.mipmap.ic_launcher)
    Drawable logo;

    private JobAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        jobViewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(JobViewModel.class);

        setupRecyclerView();

    }

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(layoutManager);

        mainRecyclerView.addItemDecoration(
                new DividerItemDecoration(Objects.requireNonNull(this), LinearLayout.VERTICAL));
        mainRecyclerView.setHasFixedSize(true);

        mAdapter = new JobAdapter(this, this);
        jobViewModel.jobsLiveData.observe(this, resource -> {
            if (resource.data != null) {
                setFirebaseAnalytics(resource.data);
                progressBar.setVisibility(View.GONE);
                mAdapter.submitList(resource.data);
            }
        });
        initSwipeToRefresh();
        mainRecyclerView.setAdapter(mAdapter);
    }

    private void setFirebaseAnalytics(List<Job> jobList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEM_LIST, new ArrayList<>(jobList));
        bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(jobList.size()));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    private void initSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            jobViewModel.refresh();
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                this.startActivity(new Intent(JobActivity.this, AboutActivity.class));
                break;
            case R.id.favorites:
                this.startActivity(new Intent(JobActivity.this, FavoriteActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onJobClick(Job job, ImageView imageView, TextView textView) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(ARG_DETAIL_JOB, job);

        //noinspection unchecked
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                new Pair<>(imageView, getResources().getString(R.string.transition_image_name)),
                new Pair<>(textView, getResources().getString(R.string.transition_title_name)));
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    @Override
    public void onFavoredClicked(@NonNull Job job, boolean isFavorite, int position) {
        jobViewModel.setJobFavored(job, isFavorite);
        DbUtil.updateWidgets(this);
    }


}