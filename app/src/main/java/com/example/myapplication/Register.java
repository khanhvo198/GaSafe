package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Register extends AppCompatActivity {

    EditText rgName, rgPassword, rgRePassword, rgPhone, rgEmail;
    Button btnRegister;
    TextView txtLogin;
    FirebaseAuth mAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rgName = findViewById(R.id.rgName);
        rgPassword = findViewById(R.id.rgPassword);
        rgRePassword = findViewById(R.id.rgRePassword);
        rgPhone = findViewById(R.id.rgPhone);
        rgEmail = findViewById(R.id.rgEmail);

        btnRegister = findViewById(R.id.btnRegister);

        txtLogin = findViewById(R.id.txtLogin);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = rgEmail.getText().toString().trim();
                String password = rgPassword.getText().toString().trim();
                String repassword = rgRePassword.getText().toString().trim();
                final String phone = rgPhone.getText().toString().trim();
                final String name = rgName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    rgName.setError("Name is Required.");
                    rgName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    rgPhone.setError("Phone is Required.");
                    rgPhone.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    rgEmail.setError("Email is Required.");
                    rgEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    rgEmail.setError("Please provide valid email.");
                    rgEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    rgPassword.setError("Password is Required.");
                    rgPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    rgPassword.setError("Password must be >= 6 characters");
                    rgPassword.requestFocus();
                    return;
                }

                if (!password.equals(repassword)) {
                    rgRePassword.setError("Re Password is not correct");
                    rgRePassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User user = new User(name, phone, email);
                            FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(getApplicationContext(),Login.class));
                                    }
                                    else {
                                        Toast.makeText(Register.this, "Error !" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                        }
                        else {
                            Toast.makeText(Register.this, "Error !" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}