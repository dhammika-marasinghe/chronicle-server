package com.illusionbox.www.chronicle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class StoryActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    public String playing = "";
    Context context;
    ProgressDialog pdiag;
    String[] stories;
    String[] story_ids;
    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private Button button6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        context = this;

        pdiag = CommonDialogUtils.showPrgressDialog(context, "Please wait", "Loading Stories", new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        pdiag.setCancelable(false);
        new GetStoriesTask().execute("http://172.22.111.136:8080/chronicle-server/liststories.php");

        button6 = (Button)findViewById(R.id.button6);

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(StoryActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
    }

    public void clicked(int position) {
        String item = stories[position];
        String s_id = story_ids[position];
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.22.111.136:8080/chronicle-server/uploads/"+s_id+".3gp"));
        startActivity(myIntent);
        /*Toast.makeText(context, s_id + " = " + item, Toast.LENGTH_LONG).show();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Toast.makeText(context, "WTF!", Toast.LENGTH_LONG).show();
            if (item.equalsIgnoreCase(playing)) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                try {
                    mediaPlayer.setDataSource("http://172.22.111.136:8080/chronicle-server/player.php?file="+s_id+".mp3");
                    mediaPlayer.prepareAsync();
                    pdiag = CommonDialogUtils.showPrgressDialog(context, "Please wait", "Buffering", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (item.equalsIgnoreCase(playing)) {
                mediaPlayer.start();
            } else {
                //mediaPlayer.release();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(this);
                try {
                    mediaPlayer.setDataSource("http://172.22.111.136:8080/chronicle-server/player.php?file="+s_id+".mp3");
                    mediaPlayer.prepareAsync();
                    pdiag = CommonDialogUtils.showPrgressDialog(context, "Please wait", "Buffering", new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    boolean intialStage;

    @Override
    public void onPrepared(MediaPlayer player) {
        pdiag.dismiss();
        player.start();
    }

    class Player extends AsyncTask<String, Void, Boolean>{

        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... strings) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(strings[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        playPause = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();

            intialStage = false;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

    void populateList(String json) {
        try {
            // Spinner Drop down elements
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("stories");
            stories = new String[arr.length()];
            story_ids = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                stories[i] = arr.getJSONObject(i).getString("title");
                story_ids[i] = arr.getJSONObject(i).getString("idstory");
            }
            ListAdapter theAdapter = new MyAdapter(context, stories);

            // We point the ListAdapter to our custom adapter
            // ListAdapter theAdapter = new MyAdapter(this, favoriteTVShows);

            // Get the ListView so we can work with it
            ListView theListView = (ListView) findViewById(R.id.listView);

            // Connect the ListView with the Adapter that acts as a bridge between it and the array
            theListView.setAdapter(theAdapter);
            theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    clicked(i);
                }
            });
            theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(StoryActivity.this,MainActivity.class);
                    intent.putExtra("story_name", stories[i]);
                    intent.putExtra("story_id", story_ids[i]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    return false;
                }
            });
            pdiag.dismiss();
        } catch (JSONException e) {
            //Toast.makeText(context, "Malformed JSON", Toast.LENGTH_LONG).show();
        }
    }

    private class GetStoriesTask extends AsyncTask<String, Integer, String> {

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
            //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            populateList(s);
        }
    }
}
