package com.example.pavan.tess2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Pavan on 14-01-2016.
 */
public class MyService extends Service {
    String phrase;
    protected static SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    Context c;
    protected AudioManager mAudioManager;
    String sphrase;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sphrase  = intent.getExtras().getString("phrase");
        return START_NOT_STICKY;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        c= getApplicationContext();
        Log.d("tag", "service" + phrase);
        //Shared preferences retreival

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
             sphrase = prefs.getString("name", "No name defined");//"No name defined" is the default value.

        }


        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        SpeechRecognitionListener h = new SpeechRecognitionListener();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(h);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        Log.d("avail", " " + mSpeechRecognizer.isRecognitionAvailable(this));
        if (mSpeechRecognizer.isRecognitionAvailable(this))
            Log.d("created", "onBeginingOfSpeech");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(5000, 5000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            onCreate();

        }
    };


    class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {

            mNoSpeechCountDown.start();
            Log.d("onReady", "service");
        }

        @Override
        public void onBeginningOfSpeech() {

            mNoSpeechCountDown.cancel();
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {
            Log.d("ERROR","ERROR");
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            onCreate();
        }

        @Override
        public void onResults(Bundle resultsBundle) {
            Boolean check = false;
            Log.d("Results", "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = resultsBundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String s = "";
            for (String result : matches) {
                if (result.equals(sphrase)) {
                    MediaPlayer mp = MediaPlayer.create(c, R.raw.naruto);
                    mp.start();
                    s += result + "\n";
                    check = true;
                }
                s += result + "\n";
            }
             if(!check){
                 mSpeechRecognizer.stopListening();
                 mSpeechRecognizer.destroy();

                 onCreate();

             }


            Log.d("Results", s);

          // stopSelf();

        }

        @Override
        public void onPartialResults(Bundle bundle) {

            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            onCreate();

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    }

}