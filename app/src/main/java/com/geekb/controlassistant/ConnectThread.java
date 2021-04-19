package com.geekb.controlassistant;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ConnectThread extends Thread {
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private BluetoothConnectCallback connectCallback;

    interface BluetoothConnectCallback{
        void connectSuccess(BluetoothSocket socket);
        void connectFailed(String msg);
    }
    public ConnectThread(BluetoothDevice device,BluetoothConnectCallback connectCallback) {
        this.device = device;
        this.connectCallback = connectCallback;
        BluetoothSocket tmp = null;
        try {
//            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));

           tmp = (BluetoothSocket) device.getClass()
                    .getDeclaredMethod("createRfcommSocket",new Class[]{int.class})
                    .invoke(device,1);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.socket = tmp;
    }
    @Override
    public void run() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   socket.connect();
                   connectCallback.connectSuccess(socket);
               } catch (IOException e) {
                   e.printStackTrace();
                   try {
                       connectCallback.connectFailed(e.getMessage());
                       socket.close();
                   } catch (IOException ee) {
                       ee.printStackTrace();
                   }
                   return;
               }
           }
       }).start();

    }
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}