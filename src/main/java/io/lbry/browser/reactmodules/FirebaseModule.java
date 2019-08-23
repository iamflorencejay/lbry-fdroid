package io.lbry.browser.reactmodules;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.lbry.browser.BuildConfig;
import io.lbry.browser.MainActivity;
import io.lbry.browser.Utils;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONException;

public class FirebaseModule extends ReactContextBaseJavaModule {

    private Context context;

    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public String getName() {
        return "Firebase";
    }
    
    @ReactMethod
    public void setCurrentScreen(String name) {
        if (firebaseAnalytics != null) {
            firebaseAnalytics.setCurrentScreen(MainActivity.getActivity(), name, Utils.capitalizeAndStrip(name));
        }
    }
    
    @ReactMethod
    public void track(String name, ReadableMap payload) {
        Bundle bundle = new Bundle();
        if (payload != null) {
            HashMap<String, Object> payloadMap = payload.toHashMap();
            for (Map.Entry<String, Object> entry : payloadMap.entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    bundle.putString(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent(name, bundle);
        }
    }

    @ReactMethod
    public void logException(boolean fatal, String message, String error) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("error", error);
        if (firebaseAnalytics != null) {
            firebaseAnalytics.logEvent(fatal ? "reactjs_exception" : "reactjs_warning", bundle);
        }

        if (fatal) {
            Toast.makeText(context,
                           "An application error occurred which has been automatically logged. " +
                           "If you keep seeing this message, please provide feedback to the LBRY " +
                           "team by emailing hello@lbry.com.",
                            Toast.LENGTH_LONG).show();
        }
    }
}