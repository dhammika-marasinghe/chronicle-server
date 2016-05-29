package com.illusionbox.www.chronicle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ImageButton record, upload, cancel, effect;
    Button button2;
    Context context;
    View.OnClickListener next_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(story_id != null) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://172.22.111.136:8080/chronicle-server/uploads/" + story_id + ".3gp"));
                startActivity(myIntent);
            }
            //Intent i = new Intent(MainActivity.this, PlaybackActivity.class);
            //startActivity(i);
        }
    };
    private MediaRecorder myAudioRecorder;
    private String outputFile = null, title = "Chronicle";
    View.OnClickListener feed_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, StoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };
    View.OnClickListener upload_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //new UploadAudioTask().execute("url", outputFile);
            upload.setEnabled(false);
            cancel.setEnabled(false);
            effect.setEnabled(false);
            upload.setBackgroundResource(R.drawable.doneinactive);
            cancel.setBackgroundResource(R.drawable.cancelinactive);
            effect.setBackgroundResource(R.drawable.effectsinactive);
            Intent upload_intent = new Intent(MainActivity.this, UploadActivity.class);
            if(story!=null){
                upload_intent.putExtra("title",story);
                upload_intent.putExtra("id",story_id);
            }
            upload_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(upload_intent);
            finish();
        }
    };
    private Chronometer myChronometer;
    View.OnClickListener stop_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;

            record.setImageDrawable(getResources().getDrawable(R.drawable.playbutton));
            record.setOnClickListener(play_click);

            myChronometer.stop();


            upload.setEnabled(true);
            cancel.setEnabled(true);
            effect.setEnabled(true);
            cancel.setBackgroundResource(R.drawable.cancelactive);
            effect.setBackgroundResource(R.drawable.effectsactive);
            upload.setBackgroundResource(R.drawable.doneactive);

            recordProgress.getProgressDrawable().setColorFilter(
                    Color.rgb(0, 169, 157), android.graphics.PorterDuff.Mode.SRC_IN);

            Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
        }
    };
    View.OnClickListener record_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            record.setOnClickListener(stop_click);
            record.setImageDrawable(getResources().getDrawable(R.drawable.recordactive));

            myChronometer.setBase(SystemClock.elapsedRealtime());
            myChronometer.start();

            Toast.makeText(getApplicationContext(), "Recording...", Toast.LENGTH_LONG).show();
        }
    };
    private MediaPlayer m;
    private ProgressBar progressBar, recordProgress;
    private boolean isPlaying = false;
    View.OnClickListener play_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
            if (m == null) {
                m = new MediaPlayer();

                try {
                    m.setDataSource(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            myChronometer.setBase(SystemClock.elapsedRealtime() - m.getCurrentPosition());
            myChronometer.start();

            record.setImageDrawable(getResources().getDrawable(R.drawable.pausebutton));
            record.setOnClickListener(pause_click);

            isPlaying = true;

            m.start();
            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    record.setImageDrawable(getResources().getDrawable(R.drawable.playbutton));
                    record.setOnClickListener(play_click);
                    myChronometer.stop();
                    isPlaying = false;
                }
            });
            //Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
        }
    };
    View.OnClickListener pause_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (m.isPlaying()) {
                m.pause();
                record.setImageDrawable(getResources().getDrawable(R.drawable.playbutton));
                record.setOnClickListener(play_click);
                myChronometer.stop();
                isPlaying = false;
            } else {
                m.stop();
                record.setImageDrawable(getResources().getDrawable(R.drawable.playbutton));
                record.setOnClickListener(play_click);
                myChronometer.stop();
                isPlaying = false;
            }
        }
    };

    String story, story_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        record = (ImageButton) findViewById(R.id.btn_record);
        upload = (ImageButton) findViewById(R.id.btn_upload);
        cancel = (ImageButton) findViewById(R.id.btn_cancel);
        effect = (ImageButton) findViewById(R.id.btn_fx);
        button2 = (Button) findViewById(R.id.button2);
        myChronometer = (Chronometer) findViewById(R.id.chronometer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recordProgress = (ProgressBar) findViewById(R.id.progressBar2);

        button2.setOnClickListener(feed_click);

        Intent past = getIntent();
        story = past.getStringExtra("story_name");
        story_id = past.getStringExtra("story_id");

        upload.setEnabled(false);
        upload.setBackgroundResource(R.drawable.doneinactive);
        cancel.setOnClickListener(cancel_click);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.mp3";

        upload.setOnClickListener(upload_click);
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myAudioRecorder.setOutputFile(outputFile);

        myChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();
                recordProgress.setProgress((int) timeElapsed / 15);
                if (timeElapsed >= 20000 && !isPlaying) {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;

                    record.setImageDrawable(getResources().getDrawable(R.drawable.playbutton));
                    record.setOnClickListener(play_click);

                    recordProgress.getProgressDrawable().setColorFilter(
                            Color.rgb(0, 169, 157), android.graphics.PorterDuff.Mode.SRC_IN);
                    myChronometer.stop();

                    upload.setEnabled(true);
                    cancel.setEnabled(true);
                    effect.setEnabled(true);
                    cancel.setBackgroundResource(R.drawable.cancelactive);
                    effect.setBackgroundResource(R.drawable.effectsactive);
                    upload.setBackgroundResource(R.drawable.doneactive);

                    Toast.makeText(getApplicationContext(), "Audio recorded", Toast.LENGTH_LONG).show();
                }
            }
        });

        record.setOnClickListener(record_click);
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(next_click);
        if(story != null) {
            next.setText(story);
        } else {
            next.setEnabled(false);
        }
    }

    View.OnClickListener cancel_click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent cancel_intent = new Intent(MainActivity.this, MainActivity.class);
            cancel_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cancel_intent);
            finish();
        }
    };

    void setResponse(String str) {
        TextView txt = (TextView) findViewById(R.id.textView);
        txt.setText(str);
    }

    void setProgressPercent(int progress) {
        progressBar.setProgress(progress);
    }

    private class UploadAudioTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strings) {
            publishProgress(25);
            File file = new File(outputFile);
            String result = "Failed";
            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("title", title);
                map.put("email", ChroniclePreferences.getPreference(context, "username"));
                URL url = new URL("http://172.22.111.136:8080/chronicle-server/upload.php");
                if(story != null) {
                    url = new URL("http://172.22.111.136:8080/chronicle-server/contributeToStory.php");
                    map.put("story_name",story);
                    map.put("story_id",story_id);
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
            //Toast.makeText(context, "Done " + result, Toast.LENGTH_LONG).show();
        }
    }
}