package com.example.kshitijdani.emergency_location_provider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.*;
import android.database.*;
import android.widget.*;
import android.location.*;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static MainActivity inst;
//    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
//
//    // The minimum time between updates in milliseconds
//    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 min

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    TextView T1,T2,T3;
    Button addC,delC,ref,send;
    ArrayAdapter arrayAdapter;
    LocationManager locationManager;
    Location loc;
    String latitude="10",longitude="10",textToSend;
    String contactNumber;
    SmsManager smsManager;
    SharedPreferences pref;
    Editor editor;


    private static final int READ_SMS_PERMISSIONS_REQUEST=1;

    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadSMS(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission!!", Toast.LENGTH_SHORT).show();

            }
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSIONS_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String permissions[],@NonNull int[ ] grantResults){

        if(requestCode== READ_SMS_PERMISSIONS_REQUEST){
            if(grantResults.length==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Read SMS Permission Granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            }
            else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    public void refreshSmsInbox(){
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if(indexBody < 0 || !smsInboxCursor.moveToFirst()){
            return;
        }
        arrayAdapter.clear();

        do{
            String str = smsInboxCursor.getString(indexBody);

            if(str.contains("location")){
//               System.out.println("in refreshSMS");
//                sendSMS();
            }
        }
        while(smsInboxCursor.moveToNext());

    }





    public void sendSMS(String num){
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            getPermissionToReadSMS();
        }
        else{
//            System.out.println(num);
//            System.out.println(contactNumber);
            if(contactNumber.equals(num)) {
               // System.out.println("They're equal");
                smsManager.sendTextMessage(contactNumber, null, textToSend, null, null);
                T2.setText("SMS Sent");
            }

            // Toast.makeText(this,"Message sent!",Toast.LENGTH_SHORT).show();
        }
    }



    public void getLocation(){
        try {
//            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//            System.out.println(isNetworkEnabled);
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                System.out.println("access not granted");
            } else {
                System.out.println("access granted");
            }

            System.out.println("NOW declaring loc");
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (loc != null) {
                System.out.println("loc was declared");
                if (String.valueOf(loc.getLatitude()) != null)
                    latitude = String.valueOf(loc.getLatitude());
                if (String.valueOf(loc.getLongitude()) != null)
                    longitude = String.valueOf(loc.getLongitude());
            }
        }catch (Exception e) {
            // e.printStackTrace();
            Log.e("Error : Location",
                    "Impossible to connect to LocationManager", e);
            System.out.println("Can't access location manager");
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        T1 = findViewById(R.id.Text1);
        T2 = findViewById(R.id.Text2);
        T3 = findViewById(R.id.Text3);

        addC= findViewById(R.id.addContact);
        delC = findViewById(R.id.delContact);
        ref = findViewById(R.id.ref);
        send = findViewById(R.id.send);


        final SharedPreferences pref = getApplicationContext().getSharedPreferences("ContactFile",0);
        final Editor editor  = pref.edit();
        smsManager=SmsManager.getDefault();

        // DELETING ALL DATA IN SHARED PREFERENCES
        delC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editor.clear();
                editor.commit();
            }
        });

        addC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), AddContact.class);
                startActivity(intent);


            }
        });

        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getString("Name",null)!=null)
                    T1.setText("Name :" +pref.getString("Name",null)+" Mobile Number :" +pref.getString("Number",null));
                else
                    T1.setText("No Contact Available");


                contactNumber=pref.getString("Number",null);

                getLocation();

                textToSend="Latitude : "+latitude+" Longitude: "+longitude;
                T2.setText("nothing sent yet");
                T3.setText(textToSend);
            }




        });

//        //send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSMS();
//            }
//        });




    }





}
