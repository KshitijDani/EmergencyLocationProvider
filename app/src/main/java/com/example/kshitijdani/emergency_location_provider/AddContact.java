package com.example.kshitijdani.emergency_location_provider;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import android.view.View;

public class AddContact extends AppCompatActivity {

    EditText name,mob;
    Button submit;

    public String contactName;
    public String contactMob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        name =findViewById(R.id.name);
        mob =findViewById(R.id.mob);
        submit = findViewById(R.id.submit);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("ContactFile",0);
        final SharedPreferences.Editor editor  = pref.edit();


        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Storing the values of mobile number and name
                contactMob = mob.getText().toString();
                contactName = name.getText().toString();

                mob.setText("");
                name.setText("");

                editor.putString("Name",contactName);
                editor.putString("Number",contactMob);

                editor.commit();
//                System.out.println(pref.getString("Name",null));
//                System.out.println(pref.getString("Number",null));


            }


        });
    }



}
