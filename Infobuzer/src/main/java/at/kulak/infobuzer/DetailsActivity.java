package at.kulak.infobuzer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
        description.setText(entry.getDescription());

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getUrl()));
                try {
                    startActivity(intent);
                } catch(ActivityNotFoundException e) {
                    Toast.makeText(DetailsActivity.this, "You must have a web browser to open this link.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
