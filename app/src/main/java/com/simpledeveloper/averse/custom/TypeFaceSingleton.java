package com.simpledeveloper.averse.custom;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFaceSingleton {
    private static TypeFaceSingleton instance = new TypeFaceSingleton();
    private TypeFaceSingleton() {}
    private Typeface mTypeFace = null;

    public static TypeFaceSingleton getInstance() {
        return instance;
    }

    public Typeface getTypeface(Context context) {
        if(mTypeFace == null){
            mTypeFace = Typeface.createFromAsset(context.getResources().getAssets(), "DroidSans.ttf");
        }
        return mTypeFace;
    }
}
