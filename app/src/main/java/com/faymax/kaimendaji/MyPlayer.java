package com.faymax.kaimendaji;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by HPF on 2016/7/15.
 */
public class MyPlayer {

    private static MediaPlayer[] mTonePlayer = new MediaPlayer[Const.tone.length];

    private static MediaPlayer mMusicPlayer;

    /**
     * 播放歌曲
     * @param context
     * @param fileName
     */
    public static void playSong(Context context, String fileName){
        Log.i("name", fileName);
        if (mMusicPlayer == null) {
            mMusicPlayer = new MediaPlayer();
        }

        mMusicPlayer.reset();
        AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd("song/"+fileName);
            mMusicPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mMusicPlayer.prepare();
            mMusicPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopSong(Context context) {
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
        }
    }

    public static void playTone(Context context, int index) {

        AssetManager assetManager = context.getAssets();
        if (mTonePlayer[index] == null) {
            mTonePlayer[index] = new MediaPlayer();
            try {
                AssetFileDescriptor fileDescriptor = assetManager.openFd(Const.tone[index]);
                mTonePlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mTonePlayer[index].prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mTonePlayer[index].start();



    }
}
