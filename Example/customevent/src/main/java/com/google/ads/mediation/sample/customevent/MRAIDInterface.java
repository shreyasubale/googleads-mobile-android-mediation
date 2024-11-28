package com.google.ads.mediation.sample.customevent;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class MRAIDInterface {
    private String state = "loading";
    private WebView webView;
    private Activity activity;

    public MRAIDInterface(WebView webView, Activity activity) {
        this.webView = webView;
        this.activity = activity;
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
                    "updateState('expanded')",
                    null
            );
        });
    }

    @JavascriptInterface
    public void close() {
        activity.runOnUiThread(() -> {
            // Implement close logic
            state = "default";
            webView.evaluateJavascript(
                    "updateState('default')",
                    null
            );
        });
    }
}
