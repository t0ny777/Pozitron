package com.example.e_commerce;

import android.graphics.Bitmap;
import android.view.View;

class ScreenShot {

    public static Bitmap TakeScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;

    }
    public static Bitmap TakeScreenShotRootView(View v)
    {
        return TakeScreenShot(v.getRootView());
    }

}