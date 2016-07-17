package com.faymax.kaimendaji;

/**
 * Created by HPF on 2016/7/9.
 */
public class Song {

    private String mSongName;
    private String mFileName;
    private int mNameLength;
    public char[] getNameCharacters() {
        return mSongName.toCharArray();
    }

    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getSongName() {

        return mSongName;
    }

    public String getFileName() {
        return mFileName;
    }

    public int getNameLength() {
        mNameLength = mSongName.length();
        return mNameLength;
    }
}
