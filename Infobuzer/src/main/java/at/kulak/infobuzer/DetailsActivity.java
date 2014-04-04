package at.kulak.infobuzer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;

import java.net.URI;

import at.kulak.infobuzer.model.Entry;

public class DetailsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        SmartImageView image = (SmartImageView)findViewById(R.id.image);
        final Entry entry = (Entry)getIntent().getParcelableExtra("entry");

        // building boundaries for smartimage.
        Rect r = new Rect();
        r.left = image.getLeft();
        r.right = image.getRight();
        r.top = image.getTop();
        r.bottom = image.getBottom();
        image.setImageUrl(entry.getImageUrl(), r);

        // texts
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(entry.getTitle());

        TextView description = (TextView)findViewById(R.id.description);
        description.setText(Html.fromHtml(entry.getDescription()));

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getUrl()));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(DetailsActivity.this, getString(R.string.no_browser_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
