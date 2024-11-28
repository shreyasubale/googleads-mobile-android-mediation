package com.google.ads.mediation.sample.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class MRAIDInterface {
    private String state = "loading";
    private WebView webView;
    private Activity activity;
    private Dialog dialog;

    public MRAIDInterface(WebView webView, Activity activity ,Dialog dialog) {
        this.webView = webView;
        this.activity = activity;
        this.dialog = dialog;
    }

    @JavascriptInterface
    public String getState() {
        return state;
    }

    @JavascriptInterface
    public boolean supports(String feature) {
        switch (feature) {
            case "expand":
            case "resize":
            case "close":
                return true;
            default:
                return false;
        }
    }

    @JavascriptInterface
    public String getCurrentPosition() {
        // Return current ad position
        return "{\"x\": 0, \"y\": 0, \"width\": 320, \"height\": 480}";
    }

    @JavascriptInterface
    public String getMaxSize() {
        // Get device screen size
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return "{\"width\": " + metrics.widthPixels + ", \"height\": " + metrics.heightPixels + "}";
    }

    @JavascriptInterface
    public void expand() {
        activity.runOnUiThread(() -> {
            // Implement expand logic
            state = "expanded";
            webView.evaluateJavascript(
                    "mraid.setState('expanded')",
                    null
            );
        });
    }

    @JavascriptInterface
    public void fireReadyEvent() {
        activity.runOnUiThread(() -> webView.evaluateJavascript("mraid.fireReadyEvent()", null));
    }

    @JavascriptInterface
    public void close() {
        activity.runOnUiThread(() -> {
//            this.activity
            this.dialog.dismiss();
            // Implement close logic
            state = "default";
            webView.evaluateJavascript(
                    "mraid.setState('default')",
                    null
            );
        });
    }
}

