package com.google.zxing.client.android;

import android.graphics.Bitmap;
import android.os.Parcelable;

import com.google.zxing.Result;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xujuan on 2017/5/17.
 */

public class SerializableMap implements Serializable {

    private float scaleFactor;
    private Result result;
    private Bitmap bmp;
    private Map<String, Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }
}
