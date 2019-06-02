package io.github.osaigbovo.remotejobs.ui.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.ui.adapter.FavoriteAdapter;
import io.github.osaigbovo.remotejobs.ui.jobdetail.DetailActivity;

import static io.github.osaigbovo.remotejobs.ui.jobdetail.DetailActivity.ARG_DETAIL_JOB;

public class FavoriteActivity extends AppCompatActivity implements HasSupportFragmentInjector,
        FavoriteAdapter.OnFavoriteJobClickListener {

    @Inject DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @BindView(R.id.favorite_toolbar) Toolbar toolbar;

    private FavoriteFragment favoriteFragment;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.favorite_container, FavoriteFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof FavoriteFragment) {
            favoriteFragment = (FavoriteFragment) fragment;
            favoriteFragment.setOnFavoriteJobClickListener(this);
        }
    }

    @Override
    public void onJobClick(FavoriteJob favoriteJob, ImageView imageView, TextView textView) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(ARG_DETAIL_JOB, favoriteJob);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                new Pair<>(imageView, getResources().getString(R.string.transition_image_name)),
                new Pair<>(textView, getResources().getString(R.string.transition_title_name)));

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    @Override
    public void onFavoredClicked(@NonNull FavoriteJob favoriteJob, boolean isFavorite, int position) {
        favoriteFragment.clickFav(favoriteJob, isFavorite);
    }

}
