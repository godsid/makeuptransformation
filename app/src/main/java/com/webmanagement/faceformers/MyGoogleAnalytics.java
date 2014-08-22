package com.webmanagement.faceformers;


import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;


/**
 * Created by Demo on 3/22/14 AD.
 */
public class MyGoogleAnalytics extends GoogleAnalytics {

    public static Tracker tracker;
    protected MyGoogleAnalytics(Context context) {
        super(context);
        tracker = getTracker(context.getResources().getString(R.string.ga_trackingId));
    }
    public static MyGoogleAnalytics getInstance(Context context){
        return new MyGoogleAnalytics(context);
    }
    public void trackPage(String pageName){
        tracker.send(MapBuilder.createAppView()
                        .set(Fields.SCREEN_NAME, pageName)
                        .build()
        );
    }
    public void trackEvent(String category,String action,String label,Long value){
        tracker.send(MapBuilder.createEvent(category,action,label,value)
                .build());

    }



}
