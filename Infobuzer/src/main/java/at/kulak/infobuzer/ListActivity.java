package at.kulak.infobuzer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.kulak.infobuzer.model.Entry;

public class ListActivity extends ActionBarActivity {

    final String URL = "http://infobuzer.pl/voucher/json";
    ArrayList<Entry> data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getDataFromServer();

    }

    private void getDataFromServer() {

        // download begining
        DataTask task = new DataTask() {
            private ProgressDialog progress = null;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(ListActivity.this);
                progress.setTitle("Loading");
                progress.setMessage("Downloading new messages.");
                progress.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONArray ar = new JSONArray(s);
                    ArrayList<Entry> entries = new ArrayList<Entry>();
                    for(int i=0; i<ar.length(); i++) {
                        try {
                            entries.add(new Entry(ar.getJSONObject(i)));
                        } catch(Exception e) {
                            // ignore if single entry is unreadable
                        }
                    }

                    ListActivity.this.data = entries;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            ListActivity.this.loadDataIntoView();
                        }
                    });


                } catch(JSONException e) {
                    // TODO: something here
                }
            }
        };
        task.execute();
    }

    private void loadDataIntoView() {
        final ListView list = (ListView)findViewById(R.id.list);
        ListAdapter adapter = new EntriesAdapter(this, R.layout.list_element, this.data);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Entry entry = (Entry)list.getAdapter().getItem(position);
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                intent.putExtra("entry", entry);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
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

    private class DataTask extends AsyncTask<Void, Void, String> {

        protected String convertInputStreamToString(InputStream stream) throws IOException {

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";
            StringBuilder result = new StringBuilder();
            while((line = reader.readLine()) != null ) {
                result.append(line);
            }
            return result.toString();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // get data from the server
                HttpClient client = new DefaultHttpClient();
                HttpGet req = new HttpGet();
                req.setURI(new URI(ListActivity.this.URL));
                InputStream stream = client.execute(req).getEntity().getContent();
                String result = convertInputStreamToString(stream);
                return result;

            } catch(Exception e) {
                // fail
                return "[]";
            }
        }
    }

    private class EntriesAdapter extends ArrayAdapter<Entry> {
        private int resource;
        public EntriesAdapter(Context context, int resource, List<Entry> entries) {
            super(context, resource, entries);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Entry entry = getItem(position);
            View view = null;
            if(convertView==null) {
                // creating new view
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(this.resource, null);
            } else {
                view = convertView;
            }

            // setting data
            TextView tv_title = (TextView)view.findViewById(R.id.title);
            TextView tv_categories = (TextView)view.findViewById(R.id.categories);
            TextView tv_date = (TextView)view.findViewById(R.id.date);

            String shortTitle = entry.getShortTitle();
            tv_title.setText(shortTitle.substring(0, Math.min(shortTitle.length(), 30))); // first 30 chars.
            tv_categories.setText(entry.getCategories());
            try {
                Date date = entry.getStartDate();
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                tv_date.setText(df.format(date));
            } catch(Exception e) {
                tv_date.setText(""); // unable to get valid date.
            }
            return view;
        }
    }

}
