package com.example.collegechat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.collegechat.R;
import com.example.collegechat.Adapter.UserAdapter;
import com.example.collegechat.ModelClass.Users;
import com.example.collegechat.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<Users> usersArrayList;

    private ImageView imgLogout,imgSetting;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog=new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        usersArrayList=new ArrayList<>();
        DatabaseReference reference=database.getReference().child("user");

        progressDialog.show();
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    if(!users.getUid().equals(auth.getUid())) {
                        usersArrayList.add(users);
                    }


                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();

            }
        });

        recyclerView=findViewById(R.id.recyclerView);
        imgLogout=findViewById(R.id.imgLogout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imgSetting=findViewById(R.id.imgSetting);

        adapter=new UserAdapter(HomeActivity.this,usersArrayList);


        recyclerView.setAdapter(adapter);

        if(auth.getCurrentUser()==null)
        {
            startActivity(new Intent(HomeActivity.this, RegistrationActivity.class));
            finish();
        }

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure want to logout?");
                builder.setIcon(R.drawable.logout);

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        auth.signOut();
                        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                        finish();

                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }

        });


        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            }
        });
    }
}