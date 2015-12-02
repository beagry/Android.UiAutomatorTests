package com.example;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

//ant build
//adb push c://<build-location>/file.jar /sdcard/
//adb -e shell uiautomator runtest /sdcard/file.jar -c com.example.SimpleTestClass#testMethod

public class SimpleTestClass extends UiAutomatorTestCase {

    private UiDevice mDevice;
    private String appName = "Такси";

    public void testMethod() throws InterruptedException {

        mDevice = getUiDevice();

        mDevice.pressHome();

////        setCoordinates();
//        runApp();
//
//        mDevice.wait(5000);
//
//        try
//        {
//            setCoordinates();
//        }
//        catch(Exception e)
//        {
//            //не удалось установить коориднаты
//        }
//
//        UiObject2 nextButton = mDevice.findObject(By.res("ru.yandex.taxi:id/confirm"));
////        UiObject nextButton = new UiObject(new UiSelector().resourceId("ru.yandex.taxi:id/confirm"));
//
//        nextButton.click();
//
//        Thread.sleep(1500);

//        proceedParams();
//
//        setDestination();
//
//        String price = readPrice();
    }

    private void setCoordinates() {


//        sendLocation(55.723454, 37.604125);
    }

//    private void proceedParams() {
//        UiObject2 btn;
//
//        mDevice.wait(Until.hasObject(By.text("Ближайшее")),timeOut);
//        btn = mDevice.findObject(By.text("Ближайшее"));
//        if (btn == null) return;
//
//        btn.click();
//
//        mDevice.wait(Until.hasObject(By.text("Эконом")),timeOut);
//        btn = mDevice.findObject(By.text("Эконом"));
//        btn.click();
//
//        try {
//            Thread.sleep(1000);                 //1000 milliseconds is one second.
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }
//
//        btn = mDevice.findObject(By.text("Наличными"));
//        if (btn != null)
//            btn.click();
//    }
//
//    private void setDestination() {
//
//        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/address_destination")),timeOut);
//        UiObject2 btn =  mDevice.findObject(By.res("ru.yandex.taxi:id/add_destination"));
//        if (btn == null)
//            btn = mDevice.findObject(By.res("ru.yandex.taxi:id/address_destination"));
//        btn.click();
//
//
////        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/search")),3000);
////        UiObject2 textEdit =  mDevice.findObject(By.res("ru.yandex.taxi:id/search"));
//        mDevice.wait(Until.hasObject(By.clazz("android.widget.EditText")),timeOut);
//        UiObject2 textEdit = mDevice.findObject(By.clazz("android.widget.EditText"));
//        textEdit.click();
//
////        mDevice.wait(Until.hasObject(By.text("Начните вводить запрос.\n" +
////                "Например: «Некрасова 7»")),3000);
//
//        textEdit = mDevice.findObject(By.clazz("android.widget.EditText"));
//        textEdit.click();
//        textEdit.legacySetText("Открытое шоссе, 1к3");
//
//        //Wait??
//        UiObject2 suggestAddresses = mDevice.findObject(By.res("android:id/list"));
//        UiObject2 address =suggestAddresses.getChildren().get(0);
//        address.click();
//    }
//
//    static void sendLocation(double latitude, double longitude) {
//        try {
//            Socket socket = new Socket("10.0.2.2", 5554); // usually 5554
//            socket.setKeepAlive(true);
//            String str = "geo fix " + longitude + " " + latitude ;
//            Writer w = new OutputStreamWriter(socket.getOutputStream());
//            w.write(str + "\r\n");
//            w.flush();
//        }
//        catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private String readPrice() {
//        mDevice.wait(Until.hasObject(By.res("ru.yandex.taxi:id/price_value")),bigTimeout);
//        UiObject2 priceView =  mDevice.findObject(By.res("ru.yandex.taxi:id/price_value"));
//        return priceView.getText().replace("~","").replace("\u20BD","").replace(" ","");
//    }
//
    private void runApp() {
        UiObject2 calcApp = mDevice.findObject(By.text(appName));
        calcApp.click();
    }
//
//    public void closeApp(){
//
//        try {
//            mDevice.pressRecentApps();
//        } catch (RemoteException e) {
//            //nothing to do
//        }
//
//        UiObject2 app = mDevice.findObjects(By.clazz("android.widget.ImageView")).get(0);
//        Point center = app.getVisibleCenter();
//        mDevice.swipe(center.x, center.y, (center.x-200),center.y,10);
//    }
}
