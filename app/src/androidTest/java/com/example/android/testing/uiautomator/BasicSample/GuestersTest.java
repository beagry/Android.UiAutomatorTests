package com.example.android.testing.uiautomator.BasicSample;


import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class GuestersTest {

    private UiDevice mDevice;

    @Before
    public void initialTest(){
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    @Test
    public void swipeGuester(){
        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            Assert.assertTrue(false);
        }

//        mDevice.wait(Until.hasObject(By.clazz("android.widget.ImageView")),4000);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UiObject2 app = mDevice.findObjects(By.clazz("android.widget.ImageView")).get(0);
        Point center = app.getVisibleCenter();
        mDevice.swipe(center.x, center.y, (center.x-200),center.y,10);
    }

}
