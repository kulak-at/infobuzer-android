package at.kulak.infobuzer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.google.gson.Gson;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import at.kulak.infobuzer.model.Entry;

public class ListActivity extends ActionBarActivity {

    final String URL = "http://infobuzer.pl/voucher/json";
    Entry[] data = null;
    boolean isLoading = false; // if data is currently loading - prevent double downloading on orientation change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(data == null && !isLoading) // prevent reloading on orientation change.
            getDataFromServer();
        else
            loadDataIntoView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("data", data);
        outState.putBoolean("isLoading", isLoading);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        data = (Entry[])savedInstanceState.getParcelableArray("data");
        isLoading = savedInstanceState.getBoolean("isLoading");
    }

    private void getDataFromServer() {

        // download begining
        DataTask task = new DataTask() {
            private ProgressDialog progress = null;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(ListActivity.this);
                progress.setTitle(getString(R.string.progress_title));
                progress.setMessage(getString(R.string.progress_message));
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected void onPostExecute(Void stream) {
                super.onPostExecute(stream);

                // onPostExecute is on UI Thread

                progress.dismiss();
                isLoading = false;
                ListActivity.this.loadDataIntoView();
            }
        };
        isLoading = true;
        task.execute();
    }

    private void showExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String message = getString(R.string.close_dialog_message);
        final String button_str = getString(R.string.close_dialog_button);
        builder.setMessage(message).setCancelable(false).setPositiveButton(button_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListActivity.this.finish();
            }
        }).show();
    }

    private void loadDataIntoView() {

        if(isLoading && this.data == null)
            return;

        if(this.data == null || this.data.length <= 0) {
            this.showExitAlert();
            return;
        }

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

        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getDataFromServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class DataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // get data from the server
                HttpClient client = new DefaultHttpClient();
                HttpGet req = new HttpGet();
                req.setURI(new URI(ListActivity.this.URL));
                InputStream stream = client.execute(req).getEntity().getContent();


                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                Gson gson = new Gson();
                Entry[] entries = gson.fromJson(reader, Entry[].class);
                ListActivity.this.data = entries;
                return null;
            } catch(Exception e) {
                return null;
            }
        }
    }

    private class EntriesAdapter extends ArrayAdapter<Entry> {
        private int resource;
        public EntriesAdapter(Context context, int resource, Entry[] entries) {
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
            tv_categories.setText(entry.getCategoriesString(2));
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
