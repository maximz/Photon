package com.comparty.photon.spritz;

/**
 * Created by Andrew on 3/15/14.
 */
public class SpritzProgressEvent {
    private final int mProgress;

    public SpritzProgressEvent(int progress){
        mProgress = progress;
    }

    public int getProgress(){
        return mProgress;
    }
}