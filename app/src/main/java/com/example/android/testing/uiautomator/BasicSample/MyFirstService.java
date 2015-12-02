package com.example.android.testing.uiautomator.BasicSample;

import android.app.Instrumentation;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)

public final class MyFirstService extends Service {

    private final String LOG_TAG = "myLogs";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    @Before
    private void someTask() {
        Log.d(LOG_TAG, "someTask: Sleep and press HOME");
        boolean result = startInstrumentation(new ComponentName(MyFirstService.this, Instrumentation.class), null, null);
//        InstrumentationRegistry.registerInstance();

        UiDevice device;
        try {
            Instrumentation inst = InstrumentationRegistry.getInstrumentation();
             device = UiDevice.getInstance(inst);
        }
        catch (Exception e)
        {
            Log.d(LOG_TAG, "someTask: ERROR - " + e.getMessage() );
            return;
        }

        device.pressHome();
    }
}
