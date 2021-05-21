package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class ProfileFragment extends Fragment {
    View root;
    Button btnLogout;
    TextView txtName, txtPhone, txtEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.profile_fragment, container, false);
        txtName = root.findViewById(R.id.name);
        txtPhone = root.findViewById(R.id.phone);
        txtEmail = root.findViewById(R.id.email);
        btnLogout = root.findViewById(R.id.btnLogout);
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(Uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String phone = snapshot.child("phone").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                txtName.setText(name);
                txtPhone.setText(phone);
                txtEmail.setText(email);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(root.getContext(),Login.class));
            }
        });

        return root;
    }
}