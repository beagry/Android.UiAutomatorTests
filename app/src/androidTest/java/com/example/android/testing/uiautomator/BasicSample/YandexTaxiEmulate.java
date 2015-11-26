/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.uiautomator.BasicSample;

import android.graphics.Point;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;



@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class YandexTaxiEmulate {

    private UiDevice mDevice;
    private final String appName = "Такси";

    private final int timeOut = 6000;
    private final int bigTimeout = 10000;

    @Test
    public void testCheckPrice() throws Exception {

        setCoordinates();
        runApp();

        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/confirm")),bigTimeout);

        try
        {
            setCoordinates();
        }
        catch(Exception e)
        {
            //не удалось установить коориднаты
        }

        UiObject2 nextButton = mDevice.findObject(By.res("ru.yandex.taxi:id/confirm"));
        nextButton.click();

        Thread.sleep(1500);

        proceedParams();

        setDestination();

        String price = readPrice();

        Assert.assertNotNull(price);
    }

    private void setCoordinates() {

//        Context context = InstrumentationRegistry.getContext();
//
//        LocationManager manager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
//        manager.
//        Location location = new Location("")


        sendLocation(55.723454, 37.604125);
    }

    private void proceedParams() {
        UiObject2 btn;

        mDevice.wait(Until.hasObject(By.text("Ближайшее")),timeOut);
        btn = mDevice.findObject(By.text("Ближайшее"));
        if (btn == null) return;

        btn.click();

        mDevice.wait(Until.hasObject(By.text("Эконом")),timeOut);
        btn = mDevice.findObject(By.text("Эконом"));
        btn.click();

        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        btn = mDevice.findObject(By.text("Наличными"));
        if (btn != null)
            btn.click();
    }

    private void setDestination() {

        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/address_destination")),timeOut);
        UiObject2 btn =  mDevice.findObject(By.res("ru.yandex.taxi:id/add_destination"));
        if (btn == null)
            btn = mDevice.findObject(By.res("ru.yandex.taxi:id/address_destination"));
        btn.click();


//        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/search")),3000);
//        UiObject2 textEdit =  mDevice.findObject(By.res("ru.yandex.taxi:id/search"));
        mDevice.wait(Until.hasObject(By.clazz("android.widget.EditText")),timeOut);
        UiObject2 textEdit = mDevice.findObject(By.clazz("android.widget.EditText"));
        textEdit.click();

//        mDevice.wait(Until.hasObject(By.text("Начните вводить запрос.\n" +
//                "Например: «Некрасова 7»")),3000);

        textEdit = mDevice.findObject(By.clazz("android.widget.EditText"));
        textEdit.click();
        textEdit.legacySetText("Открытое шоссе, 1к3");

        //Wait??
        UiObject2 suggestAddresses = mDevice.findObject(By.res("android:id/list"));
        UiObject2 address =suggestAddresses.getChildren().get(0);
        address.click();
    }

    static void sendLocation(double latitude, double longitude) {
        try {
            Socket socket = new Socket("10.0.2.2", 5554); // usually 5554
            socket.setKeepAlive(true);
            String str = "geo fix " + longitude + " " + latitude ;
            Writer w = new OutputStreamWriter(socket.getOutputStream());
            w.write(str + "\r\n");
            w.flush();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readPrice() {
        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/price_value")),bigTimeout);
        UiObject2 priceView =  mDevice.findObject(By.res("ru.yandex.taxi:id/price_value"));
        return priceView.getText().replace("~","").replace("\u20BD","").replace(" ","");
    }

    @Before
    public void initialApp(){
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
//        mDevice.pressHome();
//
//        mDevice.wait(Until.hasObject(By.desc("Приложения")),timeOut);
//
//        UiObject2 appButton = mDevice.findObject(By.desc("Приложения"));
//        if (appButton == null)
//            appButton = mDevice.findObject(By.desc("Apps"));
//
//        appButton.click();
//        mDevice.wait(Until.hasObject(By.text(appName)),timeOut);



    }

    private void runApp() {
        UiObject2 calcApp = mDevice.findObject(By.text(appName));
        calcApp.click();
    }

    @Test
    public void testTypeAddress(){
        UiObject2 textEdit = mDevice.findObject(By.clazz("android.widget.EditText"));
        textEdit.click();
        textEdit.setText("пнпропро");
        textEdit.setText("okttr");
        textEdit.setText("");
        textEdit.setText("gdfsкпварпа");
    }

    @After
    public void closeApp(){

        try {
            mDevice.pressRecentApps();
        } catch (RemoteException e) {
            //nothing to do
        }

        UiObject2 app = mDevice.findObjects(By.clazz("android.widget.ImageView")).get(0);
        Point center = app.getVisibleCenter();
        mDevice.swipe(center.x, center.y, (center.x-200),center.y,10);
    }

}
