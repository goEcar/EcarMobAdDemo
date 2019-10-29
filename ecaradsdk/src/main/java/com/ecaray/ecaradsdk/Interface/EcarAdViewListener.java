package com.ecaray.ecaradsdk.Interface;

public interface EcarAdViewListener {
    void onAdClicked();

//    void onAdDisplayed();

//    void onAdReceived();

//    void onAdFailedReceived(String reason);

    void onAdClosed();

//    void onAdSpreadPrepareClosed();

    void onAdClosedByUser();

//    void onAdNotifyCustomCallback(int ruleTime,  int delayTime);
    void noAd();
}
