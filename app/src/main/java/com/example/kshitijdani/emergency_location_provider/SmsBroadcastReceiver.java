package com.example.kshitijdani.emergency_location_provider;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;



public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    SmsMessage smsMessage;


    public void onReceive(Context context, Intent intent) {
        //SHIFT MAINACTIVITY INST HERE AND CHECK WHETHER WE CAN GET APPLICATION CONTEXT()
//        final SharedPreferences pref = this.getApplicationContext().getSharedPreferences("ContactFile",0);
//        final SharedPreferences.Editor editor  = pref.edit();

        MainActivity inst = MainActivity.instance();

        Bundle intentExtras = intent.getExtras();
        String format = intentExtras.getString("format");
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                }
                else {
                    smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                }
                String smsBody = smsMessage.getMessageBody().toString();
                String address = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + address + "\n";
                smsMessageStr += smsBody + "\n";


                String number = address.substring(3,13);



                if(smsBody.toLowerCase().contains("location")){
                    System.out.println("\n\nSARTHAK AND DANI\n\n");
                    System.out.println("\n\nADDRESS = "+number+"\n\n");

//                    MainActivity inst = MainActivity.instance();
                    inst.sendSMS(number);

                    System.out.println("\n\nSent Message\n\n");


                }
            }

        }
    }
}

