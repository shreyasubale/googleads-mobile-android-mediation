//package com.google.ads.mediation.sample.customevent;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.mediation.CustomEventInterstitial;
//import com.google.android.gms.ads.mediation.CustomEventInterstitialListener;
//import com.vdxtv.sdk.VdxTvAd;
//import com.vdxtv.sdk.VdxTvInterstitialAd;
//import com.vdxtv.sdk.VdxTvInterstitialAdListener;
//
//public class VdxTvCustomEventInterstitial implements CustomEventInterstitial {
//    private static final String TAG = "VdxTvCustomEventInterstitial";
//    private VdxTvInterstitialAd interstitialAd;
//
//    @Override
//    public void requestInterstitialAd(
//            Context context,
//            CustomEventInterstitialListener listener,
//            String serverParameter,
//            com.google.android.gms.ads.mediation.MediationAdRequest mediationAdRequest,
//            Bundle customEventExtras) {
//
//        // Parse server parameters
//        if (serverParameter == null || serverParameter.trim().isEmpty()) {
//            listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
//            return;
//        }
//
//        // Initialize VdxTv SDK if needed
//        VdxTvAd.initialize(context, serverParameter);
//
//        // Create interstitial ad
//        interstitialAd = new VdxTvInterstitialAd(context);
//
//        // Set up ad listener
//        interstitialAd.setAdListener(new VdxTvInterstitialAdListener() {
//            @Override
//            public void onAdLoaded() {
//                Log.d(TAG, "VdxTv interstitial ad loaded successfully");
//                listener.onAdLoaded();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Log.e(TAG, "VdxTv interstitial ad failed to load with error: " + errorCode);
//                switch (errorCode) {
//                    case VdxTvAd.ERROR_CODE_NETWORK_ERROR:
//                        listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
//                        break;
//                    case VdxTvAd.ERROR_CODE_INVALID_REQUEST:
//                        listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
//                        break;
//                    case VdxTvAd.ERROR_CODE_NO_FILL:
//                        listener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
//                        break;
//                    default:
//                        listener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
//                        break;
//                }
//            }
//
//            @Override
//            public void onAdOpened() {
//                Log.d(TAG, "VdxTv interstitial ad opened");
//                listener.onAdOpened();
//            }
//
//            @Override
//            public void onAdClicked() {
//                Log.d(TAG, "VdxTv interstitial ad clicked");
//                listener.onAdClicked();
//            }
//
//            @Override
//            public void onAdClosed() {
//                Log.d(TAG, "VdxTv interstitial ad closed");
//                listener.onAdClosed();
//            }
//        });
//
//        // Load the ad
//        interstitialAd.loadAd(serverParameter);
//    }
//
//    @Override
//    public void showInterstitial() {
//        if (interstitialAd != null && interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        if (interstitialAd != null) {
//            interstitialAd = null;
//        }
//    }
//
//    @Override
//    public void onPause() {
//        // Implement if needed
//    }
//
//    @Override
//    public void onResume() {
//        // Implement if needed
//    }
//}