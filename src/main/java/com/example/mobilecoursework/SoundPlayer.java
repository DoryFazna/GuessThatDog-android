package com.example.mobilecoursework;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {

    private static SoundPool soundPool;
    private static int correctSound;
    private static int wrongSound;
    private static int exitSound;


    public SoundPlayer(Context context){
        //SoundPool(int maxStreams, int streamType, int srcQuality)
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);

        correctSound = soundPool.load(context,R.raw.correct,1);
        wrongSound = soundPool.load(context,R.raw.wrong,1);
        exitSound = soundPool.load(context,R.raw.exit,1);
    }


    public void playCorrectSound(){
        soundPool.play(correctSound,1,1,1,0,1.0f);
    }

    public void playWrongSound(){
        soundPool.play(wrongSound,1,1,1,0,1.0f);
    }
    public void playExitSound(){
        soundPool.play(exitSound,1,1,1,0,1.0f);
    }




}
