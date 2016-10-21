package com.example.automation.mobilesignalmeasurer.speedtest;

import pervacio.com.wifisignalstrength.speedMeasurer.DefaultHandlerCallback;

public class SimpleMobileInternetHandlerCallback extends DefaultHandlerCallback{

    public SimpleMobileInternetHandlerCallback(DefaultHandlerCallback.ViewSet viewSet, String taskName) {
        super(viewSet, taskName, MOBILE);
    }

}
