package com.example.switchwifisender;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class PacketSended extends Thread{
    private static final String LOG_TAG = PacketSended.class.getSimpleName();
    private String IP_BUVO = "10.168.2.5";
    //private InetAddress adrOfServ;
    //private DatagramSocket senderSocket = new DatagramSocket();
    private byte[] bytesMac;
    private int PORT_DST = 15000;
    private int counter = 0;
    private int sleepTime = 1000;
    private boolean wifiStatus = true;

    public PacketSended() throws SocketException {
    }

    public PacketSended(boolean status){
        wifiStatus = status;
    }


    public static NetworkInterface getPhoneIntrName() throws SocketException {
        Enumeration<NetworkInterface> listOfInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface phoneIntrName = null;
        while (listOfInterfaces.hasMoreElements()) {
            phoneIntrName = listOfInterfaces.nextElement();
//            System.out.println("nameOfInterface = " + phoneIntrName.getName());
            if (phoneIntrName.getName().equals("wlan0")) {
                return phoneIntrName;
            }
        }
        Log.d(LOG_TAG, "getPhoneIntrName: {" + phoneIntrName + "}");
        return phoneIntrName;
    }


    public static String getMAC() {
        String MAC = "";
        try {
            NetworkInterface netIntr = getPhoneIntrName();
            for (int i = 0; i < netIntr.getHardwareAddress().length; i++) {
                String stringMACByte = Integer.toHexString(netIntr.getHardwareAddress()[i] & 0xFF);
                if (stringMACByte.length() == 1) {
                    stringMACByte = "0" + stringMACByte;
                }
                MAC = MAC + stringMACByte.toUpperCase() + ":";
//                        System.out.println("String: " + MAC);
            }
            MAC = MAC.substring(0, MAC.length() - 1);
            //System.out.println("MAC is: " + MAC);

        } catch (Exception e) {
            e.printStackTrace();
        }


//            textView.setText(MAC.substring(0, MAC.length()-1));
        Log.d(LOG_TAG, "MAC: {" + MAC + "}");
        return MAC;
    }


    @Override
    public void run() {
        Log.d(LOG_TAG, "run start");
        Log.d(LOG_TAG, "wifistatus = " + CheckWiFiSSID_State.wifistatus);
        while (wifiStatus){
            Log.d(LOG_TAG, "WiFi status is [ " + wifiStatus + " ]");
            while(CheckWiFiSSID_State.wifistatus.get() == 1){
                try {
                    Log.d(LOG_TAG, "wifistatus = " + CheckWiFiSSID_State.wifistatus);
                    InetAddress adrOfServ = InetAddress.getByName(IP_BUVO);
                    DatagramSocket senderSocket = new DatagramSocket();
                    String mac2Msg = PacketSended.getMAC();
                    //String mac2Msg = "test";
                    bytesMac = mac2Msg.getBytes(StandardCharsets.UTF_8);
                    if(senderSocket != null){
                        DatagramPacket datagramPacket = new DatagramPacket(bytesMac, bytesMac.length, adrOfServ, PORT_DST);
                        senderSocket.send(datagramPacket);
                        Log.d(LOG_TAG, "Packet has been sended");
                    }
                    Log.d(LOG_TAG, "Thread steel running" + counter);
                    counter++;
                    Thread.sleep(sleepTime);

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(LOG_TAG, "run end");
    }
}
