package com.luciofm.droidcon.ifican;

import android.app.Application;
import android.graphics.Typeface;

import com.squareup.otto.Bus;

import java.util.HashMap;

/**
 * Created by luciofm on 5/23/14.
 */
public class IfICan extends Application {

    /* scan codes from my presenter controller */
    public static final int BUTTON_NEXT = 104;
    public static final int BUTTON_PREV = 109;

    public static final Bus bus = new Bus();

    private HashMap<String, Typeface> typefaces = new HashMap<>();

    public Typeface getTypeface(String typeface) {
        Typeface tf = typefaces.get(typeface);
        if (tf == null) {
            tf = Typeface.createFromAsset(getAssets(), "fonts/" + typeface);

            if (tf == null)
                tf = Typeface.DEFAULT;

            typefaces.put(typeface, tf);
        }

        return tf;
    }

    public static Bus getBusInstance() {
        return bus;
    }
}
