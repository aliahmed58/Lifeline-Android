package com.main.frontend.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletonQueue {

    private static VolleySingletonQueue instance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleySingletonQueue(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingletonQueue getInstance(Context context) {
        if (instance == null) {
            instance = new VolleySingletonQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
