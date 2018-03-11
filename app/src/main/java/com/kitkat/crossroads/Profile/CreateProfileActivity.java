package com.kitkat.crossroads.Profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.R;
import com.kitkat.crossroads.Profile.UserInformation;

import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity
{
    private static final String TAG = "CreateProfileActivity";
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseAuth auth;
    private EditText fullName, phoneNumber, addressOne, addressTwo, town, postCode;
    private CheckBox checkBoxAdvertiser, checkBoxCourier;
    private boolean advertiser, courier;
    private Button saveProfile;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        database = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        saveProfile = (Button) findViewById(R.id.buttonSaveProfile);

        fullName = (EditText) findViewById(R.id.editTextFullName);
        phoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        addressOne = (EditText) findViewById(R.id.editTextAddress1);
        addressTwo = (EditText) findViewById(R.id.editTextAddress2);
        town = (EditText) findViewById(R.id.editTextTown);
        postCode = (EditText) findViewById(R.id.editTextPostCode);
        checkBoxAdvertiser = (CheckBox) findViewById(R.id.checkBoxAdvertiser);
        checkBoxCourier = (CheckBox) findViewById(R.id.checkBoxCourier);

//        textViewDateOfBirth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(
//                        CreateProfileActivity.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        dateSetListener,
//                        year,month,day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//            }
//        });

//        dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//                                month = month + 1;
//                                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);
//
//                               if(dayOfMonth >= 1 && dayOfMonth <= 9)
//                                {
//                                    String newDay = "0" + dayOfMonth;
//                                    textViewDateOfBirth.setText(newDay + "/" + month + "/" + year);
//                                }
//
//                                if(month >= 1 && month <= 9)
//                                {
//                                    String newMonth = "0" + month;
//                                    textViewDateOfBirth.setText(dayOfMonth + "/" + newMonth + "/" + year);
//                                }
//
//                                if(dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
//                                {
//                                    String newDay = "0" + dayOfMonth;
//                                    String newMonth = "0" + month;
//                                    textViewDateOfBirth.setText(newDay + "/" + newMonth + "/" + year);
//                                }
//                                else
//                                {
//                                    textViewDateOfBirth.setText(dayOfMonth + "/" + month + "/" + year);
//                                }
//                            }
//        };

        saveProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation()
    {
        String fullName = this.fullName.getText().toString().trim();
        String phoneNumber = this.phoneNumber.getText().toString().trim();
        String addressOne = this.addressOne.getText().toString().trim();
        String addressTwo = this.addressTwo.getText().toString().trim();
        String town = this.town.getText().toString().trim();
        String postCode = this.postCode.getText().toString().trim();

        if (checkBoxAdvertiser.isChecked())
        {
            advertiser = true;
            courier = false;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
            databaseVerification();
        }
        if (checkBoxCourier.isChecked())
        {
            advertiser = false;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
            databaseVerification();
        }
        if (checkBoxAdvertiser.isChecked() && checkBoxCourier.isChecked())
        {
            advertiser = true;
            courier = true;
            UserInformation userInformation = new UserInformation(fullName, phoneNumber, addressOne,
                    addressTwo, town, postCode, advertiser, courier);

            setUserInformation(userInformation);
            databaseVerification();
        }

        startActivity(new Intent(CreateProfileActivity.this, LoginActivity.class));
    }

    private void setUserInformation(UserInformation userInformation)
    {
        FirebaseUser user = auth.getCurrentUser();
        myRef.child("Users").child(user.getUid()).setValue(userInformation);
    }

    private void databaseVerification()
    {
        FirebaseUser userEmail = FirebaseAuth.getInstance().getCurrentUser();
        userEmail.sendEmailVerification();
        FirebaseAuth.getInstance().signOut();
    }
}

