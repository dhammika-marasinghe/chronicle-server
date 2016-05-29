package com.illusionbox.www.chronicle;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class UploadActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    List<String> users;
    private ProgressBar uploadProgress;
    private Context context;
    private String recordingPath = null, title = "Chronicle", user;
    private Spinner spinner;
    private ImageButton upload;
    private EditText title_txt;
    private String story_id;
    private Button button6, button5, button7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = this;
        recordingPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";
        uploadProgress = (ProgressBar) findViewById(R.id.uplaodProgress);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        upload = (ImageButton) findViewById(R.id.upload_done);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UploadAudioTask().execute("url", recordingPath);
            }
        });
        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UploadActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        /*button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UploadActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });*/
        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UploadActivity.this, StoryActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        title_txt = (EditText) findViewById(R.id.text_name);
        Intent past = getIntent();
        if (past.getStringExtra("title") != null) {
            title = past.getStringExtra("title");
            title_txt.setText(past.getStringExtra("title"));
            title_txt.setEnabled(false);
        }
        story_id = past.getStringExtra("id");
        title_txt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (textView.getText().toString() != null && textView.getText().toString() != "") {
                    title = textView.getText().toString();
                    if (user != null && user != "") {
                        upload.setEnabled(true);
                        upload.setBackgroundResource(R.drawable.doneactive);
                    }
                } else {
                    upload.setEnabled(false);
                    upload.setBackgroundResource(R.drawable.doneinactive);
                }
                return false;
            }
        });
        new GetUsersTask().execute("http://172.22.111.136:8080/chronicle-server/listusers.php");
    }

    void setResponse(String str) {
        Intent story = new Intent(UploadActivity.this, StoryActivity.class);
        startActivity(story);
        finish();
        //TextView txt = (TextView) findViewById(R.id.textView);
        //txt.setText(str);
    }

    void setProgressPercent(int progress) {
        uploadProgress.setProgress(progress);
    }

    void populateList(String json) {
        try {
            // Spinner Drop down elements
            users = new ArrayList<String>();
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("users");
            for (int i = 0; i < arr.length(); i++) {
                users.add(arr.getJSONObject(i).getString("email"));
            }
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, users);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            spinner.setAdapter(dataAdapter);
        } catch (JSONException e) {
            //Toast.makeText(context, "Malformed JSON", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        user = users.get(position);

        if (title != null && title != "") {
            upload.setEnabled(true);
            upload.setBackgroundResource(R.drawable.doneactive);
        }

        //title_txt.setText(user);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        upload.setEnabled(false);
        upload.setBackgroundResource(R.drawable.doneinactive);
    }

    private class UploadAudioTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strings) {
            publishProgress(25);
            File file = new File(recordingPath);
            String result = "Failed";
            try {
                URL url = new URL("http://172.22.111.136:8080/chronicle-server/upload.php");
                HashMap<String, String> map = new HashMap<>();
                map.put("title", title);
                map.put("next", user);
                map.put("email", ChroniclePreferences.getPreference(context, "username"));
                if (story_id != null) {
                    url = new URL("http://172.22.111.136:8080/chronicle-server/contributeToStory.php");
                    map.put("title", title);
                    map.put("idstory", story_id);
                }
                result = new HttpMultipartUpload().upload(url, file, "uploadedFile", map);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            publishProgress(100);
            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            setResponse(result);
            Toast.makeText(context, "Done!", Toast.LENGTH_LONG).show();
        }
    }

    private class GetUsersTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    InputStream it = new BufferedInputStream(conn.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder dta = new StringBuilder();
                    String chunks;
                    while ((chunks = buff.readLine()) != null) {
                        dta.append(chunks);
                    }
                    responseString = dta.toString();

                } else {
                    responseString = "FAILED"; // See documentation for more info on response handling
                }
                conn.disconnect();
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            populateList(s);
        }
    }
}
