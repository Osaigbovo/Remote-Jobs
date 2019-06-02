package io.github.osaigbovo.remotejobs.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.osaigbovo.remotejobs.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.about_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }

        TextView[] containers = new TextView[]{
                findViewById(R.id.text_retrofit_desc),
                findViewById(R.id.text_glide_desc),
                findViewById(R.id.text_androidx_desc),
                findViewById(R.id.text_lottie_desc),
                findViewById(R.id.text_room_desc),
                findViewById(R.id.text_rxjava_desc),
                findViewById(R.id.text_butter_desc),
                findViewById(R.id.text_dagger_desc),
                findViewById(R.id.text_stetho_desc),
                findViewById(R.id.text_leak_desc),
                findViewById(R.id.text_timber_desc),
                findViewById(R.id.text_text_drawable_desc),
                findViewById(R.id.text_gson_desc)};
        for (TextView r : containers) {
            r.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_retrofit_desc:
                goToURL("https://github.com/square/retrofit");
                break;

//            case R.id.container_about_library2:
//                goToURL("https://github.com/bumptech/glide");
//                break;
//
//            case R.id.container_about_library3:
//                goToURL("https://developer.android.com/jetpack/androidx");
//                break;
//
//            case R.id.container_about_library4:
//                goToURL("https://github.com/airbnb/lottie-android");
//                break;
//
//            case R.id.container_about_library5:
//                goToURL("https://developer.android.com/topic/libraries/architecture/room");
//                break;
//
//            case R.id.container_about_library6:
//                goToURL("https://github.com/ReactiveX/RxJava");
//                break;
//
//            case R.id.container_about_library7:
//                goToURL("https://github.com/JakeWharton/butterknife");
//                break;
//
//            case R.id.container_about_library9:
//                goToURL("https://github.com/google/dagger");
//                break;
//
//            case R.id.container_about_library10:
//                goToURL("https://github.com/google/gson");
//                break;
//
//            case R.id.container_about_library11:
//                goToURL("https://github.com/facebook/stetho");
//                break;
//
//            case R.id.container_about_library12:
//                goToURL("https://github.com/square/leakcanary");
//                break;
//
//            case R.id.container_about_library13:
//                goToURL("https://github.com/JakeWharton/timber");
//                break;
//
//            case R.id.container_about_library14:
//                goToURL("https://github.com/amulyakhare/TextDrawable");
//                break;
        }
    }

    public void goToURL(String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

}
