package io.github.osaigbovo.remotejobs.ui.jobdetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.Objects;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.osaigbovo.remotejobs.R;
import io.github.osaigbovo.remotejobs.data.local.entity.FavoriteJob;
import io.github.osaigbovo.remotejobs.data.model.Job;
import io.github.osaigbovo.remotejobs.utils.AppConstants;
import io.github.osaigbovo.remotejobs.utils.GlideApp;
import io.github.osaigbovo.remotejobs.utils.ViewUtil;

import static io.github.osaigbovo.remotejobs.utils.ViewUtil.getDrawableLogo;

public class DetailActivity extends AppCompatActivity {

    public static final String ARG_DETAIL_JOB = "detail_job";

    @BindView(R.id.details_toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_position)
    TextView jobTitle;
    @BindView(R.id.text_company)
    TextView companyName;
    @BindView(R.id.image_detail_logo)
    ImageView companyLogo;
    @BindView(R.id.text_date)
    TextView date;
    @BindView(R.id.text_description)
    TextView description;
    @BindString(R.string.no_description_job)
    String no_description;

    private Job job;
    private FavoriteJob favoriteJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> {
                DetailActivity.this.onBackPressed();
                finishAfterTransition();
            });
        }

        Intent intent = getIntent();
        if (intent.hasExtra(ARG_DETAIL_JOB)) {
            if (Objects.requireNonNull(intent.getExtras()).getParcelable(ARG_DETAIL_JOB) instanceof Job) {
                this.job = intent.getExtras().getParcelable(ARG_DETAIL_JOB);
            } else if (intent.getExtras().getParcelable(ARG_DETAIL_JOB) instanceof FavoriteJob) {
                this.favoriteJob = intent.getExtras().getParcelable(ARG_DETAIL_JOB);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final Transition sharedElementEnterTransition =
                    getWindow().getSharedElementEnterTransition();
            if (sharedElementEnterTransition != null) {
                sharedElementEnterTransition.addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(Transition transition) {
                        loadImage();
                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        transition.removeListener(this);
                    }
                });
            }
            final Transition sharedElementReturnTransition =
                    getWindow().getSharedElementReturnTransition();
            if (sharedElementReturnTransition != null) {
                sharedElementReturnTransition.addListener(new TransitionListenerAdapter() {
                    @Override
                    public void onTransitionStart(final Transition transition) {
                        loadImage();
                    }
                });
            }
        } else{
            loadImage();
        }

        setJobDetails();
    }

    private void setJobDetails() {
        jobTitle.setText(job != null ? job.getPosition() : favoriteJob.getPosition());
        companyName.setText(job != null ? job.getCompany() : favoriteJob.getCompany());

        date.setText(job != null ? ViewUtil.formatDayMonth(this, job.getDate())
                : ViewUtil.formatDayMonth(this, favoriteJob.getDate()));

        description.setText(job != null ? !TextUtils.isEmpty(job.getDescription())
                ? HtmlCompat.fromHtml(job.getDescription(), 0) : no_description :
                !TextUtils.isEmpty(favoriteJob.getDescription())
                ? HtmlCompat.fromHtml(favoriteJob.getDescription(), 0) : no_description);
    }

    private void loadImage() {
        int radius = getResources().getDimensionPixelSize(R.dimen.corner_radius);
        if(job != null){
            if (!TextUtils.isEmpty(job.getCompany_logo())) {
                GlideApp.with(this)
                        .load(job.getCompany_logo())
                        .transform(new RoundedCorners(radius))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(companyLogo);
            } else {
                companyLogo.setImageDrawable(getDrawableLogo(job.getCompany()));
            }
        } else{
            if (!TextUtils.isEmpty(favoriteJob.getCompany_logo())) {
                GlideApp.with(this)
                        .load(favoriteJob.getCompany_logo())
                        .transform(new RoundedCorners(radius))
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(companyLogo);
            } else {
                companyLogo.setImageDrawable(getDrawableLogo(favoriteJob.getCompany()));
            }
        }
    }

    @OnClick(R.id.btn_apply)
    void clickAppy() {
        final String jobURL = AppConstants.REMOTEOK_URL + job.getId();

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorAccent));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(jobURL));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

}
