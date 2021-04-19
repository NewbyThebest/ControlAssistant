package com.geekb.controlassistant;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    public ConnectedThread(BluetoothSocket socket) {
        this.socket = socket;
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inputStream = input;
        this.outputStream = output;
    }
    @Override
    public void run() {
        StringBuilder recvText = new StringBuilder();
        byte[] buff = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = inputStream.read(buff);
                String str = new String(buff, "ISO-8859-1");
                str = str.substring(0, bytes);
// 收到数据，单片机发送上来的数据以"#"结束，这样手机知道一条数据发送结束
//Log.e("read", str);
                if (!str.endsWith("#")) {
                    recvText.append(str);
                    continue;
                }
                recvText.append(str.substring(0, str.length() - 1)); // 去除'#'
                recvText.replace(0, recvText.length(), "");
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    public void write(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}