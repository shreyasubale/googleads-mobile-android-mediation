package com.google.ads.mediation.sample.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import com.google.ads.mediation.sample.sdk.MRAIDInterface;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A web-based interstitial ad implementation that loads content from a URL.
 */
public class SampleInterstitial {

  private final Context context;
  private String adUnit;
  private SampleAdListener listener;
  private Dialog interstitialDialog;
  private WebView webView;

  private Activity activity;
  private MRAIDInterface mraidInterface;


  /**
   * Create a new {@link SampleInterstitial}.
   *
   * @param context An Android {@link Context}.
   */
  public SampleInterstitial(Context context) {
    this.context = context;
    this.activity = (Activity) context;
    setupInterstitialDialog();
  }

  /**
   * Sets up the dialog and WebView that will display the interstitial
   */
  private void setupInterstitialDialog() {
    interstitialDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    interstitialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

    // Create the layout for the dialog
    RelativeLayout layout = new RelativeLayout(context);
    layout.setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    layout.setBackgroundColor(Color.BLACK);

    // Setup WebView
    webView = new WebView(context);
    RelativeLayout.LayoutParams webViewParams = new RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
    webView.setLayoutParams(webViewParams);
    // Enable JavaScript
    webView.getSettings().setJavaScriptEnabled(true);
    mraidInterface =new MRAIDInterface(webView, this.activity);
    webView.addJavascriptInterface(mraidInterface, "MRAIDInterface");
//    try {
//      InputStream inputStream = context.getAssets().open("mraid.js");
//      byte[] buffer = new byte[inputStream.available()];
//      inputStream.read(buffer);
//      inputStream.close();
//
//      String mraidJs = new String(buffer, "UTF-8");
//      webView.evaluateJavascript(mraidJs, null);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }


    // Set WebViewClient to handle page loading
    webView.setWebViewClient(new WebViewClient() {

      @Override
      public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url.endsWith("mraid.js")) {
          try {
            InputStream mraidStream = view.getContext().getAssets().open("mraid.js");
            return new WebResourceResponse("application/javascript", "UTF-8", mraidStream);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return super.shouldInterceptRequest(view, url);
      }
      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);



        mraidInterface.fireReadyEvent();
        if (listener != null) {
          listener.onAdFullScreen();
        }
      }
    });

    layout.addView(webView);
    interstitialDialog.setContentView(layout);

    // Handle dialog dismissal
    interstitialDialog.setOnDismissListener(dialog -> {
      if (listener != null) {
        listener.onAdClosed();
      }
    });
  }

  /**
   * Sets the sample ad unit.
   *
   * @param sampleAdUnit The sample ad unit.
   */
  public void setAdUnit(String sampleAdUnit) {
    this.adUnit = sampleAdUnit;
  }

  /**
   * Sets a {@link SampleAdListener} to listen for ad events.
   *
   * @param listener The ad listener.
   */
  public void setAdListener(SampleAdListener listener) {
    this.listener = listener;
  }

  /**
   * Fetch an ad from the specified URL.
   *
   * @param request The ad request with targeting information.
   */
//  public void fetchAd(SampleAdRequest request) {
//    if (listener == null) {
//      return;
//    }
//
//    if (adUnit == null) {
//      listener.onAdFetchFailed(SampleErrorCode.BAD_REQUEST);
//      return;
//    }
//
//    // Simulate network conditions with random success/failure
//    Random random = new Random();
//    int nextInt = random.nextInt(100);
//
//    if (nextInt < 80) {
//      listener.onAdFetchSucceeded();
//    } else if (nextInt < 85) {
//      listener.onAdFetchFailed(SampleErrorCode.UNKNOWN);
//    } else if (nextInt < 90) {
//      listener.onAdFetchFailed(SampleErrorCode.BAD_REQUEST);
//    } else if (nextInt < 95) {
//      listener.onAdFetchFailed(SampleErrorCode.NETWORK_ERROR);
//    } else {
//      listener.onAdFetchFailed(SampleErrorCode.NO_INVENTORY);
//    }
//  }

  public void fetchAd(SampleAdRequest request) {
    if (listener == null) {
      return;
    }

    if (adUnit == null) {
      listener.onAdFetchFailed(SampleErrorCode.BAD_REQUEST);
      return;
    }

    // Define the ad URL based on the adUnit or a pre-defined URL
//    String adUrl = "https://vdx.mylab.to/testpages/e9testPage.html&ad=" + adUnit;
    String adUrl = "https://vdx.mylab.to/?productName=VdxMobileInApp&size=300x250";

    // Use a background thread to perform network operations



    new Thread(() -> {

      TrustManager[] trustAllCerts = new TrustManager[] {
              new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new X509Certificate[0];
                }
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
              }
      };

      HttpsURLConnection urlConnection = null;

      try {
        // Set up the URL connection
        URL url = new URL(adUrl);
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(5000); // Set a timeout
        urlConnection.setReadTimeout(5000);

        // Check the response code
        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
          // Read the ad content from the response
          BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
          StringBuilder response = new StringBuilder();
          String inputLine;
          while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
          }
          in.close();

          // Simulate checking ad validity (you could parse JSON or HTML here)
          String adContent = response.toString();
          if (!adContent.isEmpty()) {
            listener.onAdFetchSucceeded(); // Successful ad fetch
          } else {
            listener.onAdFetchFailed(SampleErrorCode.NO_INVENTORY); // No ad available
          }
        } else {
          listener.onAdFetchFailed(SampleErrorCode.NETWORK_ERROR); // Failed due to network issues
        }
      } catch (Exception e) {
        e.printStackTrace();
        listener.onAdFetchFailed(SampleErrorCode.UNKNOWN); // Unknown error occurred
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
      }
    }).start();
  }


  /**
   * Shows the interstitial with content from the specified URL.
   *
   * @param url The URL to load in the interstitial
   */
  public void show(String url) {
    if (webView != null) {
      webView.loadUrl(url);
      interstitialDialog.show();
    }
  }

  /**
   * Shows the interstitial with default test content.
   */
  public void show() {
//    interstitialDialog.show();
    // Load a default test URL if no specific URL is provided
    show("https://vdx.mylab.to/?productName=VdxMobileInApp&size=300x250");
  }

  /**
   * Destroy the interstitial.
   */
  public void destroy() {
    if (webView != null) {
      webView.destroy();
      webView = null;
    }
    if (interstitialDialog != null) {
      interstitialDialog.dismiss();
      interstitialDialog = null;
    }
    listener = null;
  }
}