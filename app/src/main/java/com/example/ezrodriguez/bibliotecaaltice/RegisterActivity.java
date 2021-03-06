package com.example.ezrodriguez.bibliotecaaltice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ezrodriguez.bibliotecaaltice.entity.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private EditText mName,mLastName,mEmail, mUsername, mPassword;
    private static final int CLIENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_create).setOnClickListener(this);
        mName = (EditText) findViewById(R.id.register_name);
        mLastName = (EditText) findViewById(R.id.register_lastname);
        mEmail = (EditText) findViewById(R.id.register_mail);
        mUsername = (EditText) findViewById(R.id.register_username);
        mPassword = (EditText) findViewById(R.id.register_password);

    }

    @Override
    public void onClick(View view) {
        mAuth.createUserWithEmailAndPassword(mEmail.getText().toString()
                , mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserData(user);
                            goMainScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "User creation failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void saveUserData(FirebaseUser user) {
        UserProfile userProfile = getUserData(user);

        reference = FirebaseDatabase.getInstance().getReference("userProfile");
        reference.child(user.getUid()).setValue(userProfile);
    }

    private void goMainScreen() {
        Intent intent = new Intent(this,testActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private UserProfile getUserData(FirebaseUser user){
        UserProfile userProfile = new UserProfile();
        userProfile.setCreated(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                + ":" + String.valueOf(Calendar.getInstance().get(Calendar.MINUTE))
                + ":" + String.valueOf(Calendar.getInstance().get(Calendar.SECOND))
                + "-" + String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        + "/" + String.valueOf(Calendar.getInstance().get(Calendar.MONTH)
        + "/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        userProfile.setEmail(user.getEmail());
        if(user.getDisplayName() != null){
            userProfile.setName(user.getDisplayName());
        }else {
            userProfile.setName(mName.getText().toString() +
                    " " + mLastName.getText().toString());
        }
        userProfile.setRole(CLIENT);
        userProfile.setUsername(mUsername.getText().toString());

        return userProfile;
    }
}
