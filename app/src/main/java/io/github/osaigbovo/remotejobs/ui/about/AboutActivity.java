package io.github.osaigbovo.remotejobs.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.osaigbovo.remotejobs.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.about_toolbar)
    Toolbar toolbar;
    @BindArray(R.array.library_link)
    String[] library_link;
    @BindString(R.string.error)
    String error;

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

        TextView[] textViews = new TextView[]{
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
        for (TextView textView : textViews) {
            textView.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_retrofit_desc:
                goToURL(library_link[0]);
                break;
            case R.id.text_glide_desc:
                goToURL(library_link[1]);
                break;
            case R.id.text_androidx_desc:
                goToURL(library_link[2]);
                break;
            case R.id.text_lottie_desc:
                goToURL(library_link[3]);
                break;
            case R.id.text_room_desc:
                goToURL(library_link[4]);
                break;
            case R.id.text_rxjava_desc:
                goToURL(library_link[5]);
                break;
            case R.id.text_butter_desc:
                goToURL(library_link[6]);
                break;
            case R.id.text_dagger_desc:
                goToURL(library_link[7]);
                break;
            case R.id.text_gson_desc:
                goToURL(library_link[8]);
                break;
            case R.id.text_stetho_desc:
                goToURL(library_link[9]);
                break;
            case R.id.text_leak_desc:
                goToURL(library_link[10]);
                break;
            case R.id.text_timber_desc:
                goToURL(library_link[11]);
                break;
            case R.id.text_text_drawable_desc:
                goToURL(library_link[12]);
                break;
        }
    }

    private void goToURL(String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

}
