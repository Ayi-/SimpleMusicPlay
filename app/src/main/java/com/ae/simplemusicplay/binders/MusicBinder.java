package com.ae.simplemusicplay.binders;

import android.os.Binder;

/**
 * Created by AE on 2015/12/30.
 */
public abstract class MusicBinder extends Binder{
    public abstract void Play();
    public abstract void pause();
    public abstract void previous();
    public abstract void next();
    public abstract void stop();
}
