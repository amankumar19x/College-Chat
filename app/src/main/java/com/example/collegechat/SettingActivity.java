package com.example.collegechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.collegechat.Activity.HomeActivity;
import com.example.collegechat.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private ImageView imgDone;
    private CircleImageView profile_image;
    private EditText edtSettingName,edtSettingStatus;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private Uri selectedImageUri;

    private String email;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        imgDone=findViewById(R.id.imgDone);
        profile_image=findViewById(R.id.profile_image);
        edtSettingName=findViewById(R.id.edtSettingName);
        edtSettingStatus=findViewById(R.id.edtSettingStatus);


        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                email=snapshot.child("email").getValue().toString();
                String name=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String imageUri=snapshot.child("imageUri").getValue().toString();


                edtSettingName.setText(name);
                edtSettingStatus.setText(status);
                Picasso.get().load(imageUri).into(profile_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.show();
                String name=edtSettingName.getText().toString();
                String status=edtSettingStatus.getText().toString();

                if(selectedImageUri!=null)
                {
                    storageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String finalImageUri=uri.toString();
                                    Users users=new Users(auth.getUid(),name,email,finalImageUri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                progressDialog.dismiss();

                                                Toast.makeText(SettingActivity.this, "Data Updated Successfully...", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingActivity.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }
                            });
                        }
                    });
                }
                else {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String finalImageUri=uri.toString();
                            Users users=new Users(auth.getUid(),name,email,finalImageUri,status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Data Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    });

                }



            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10)
        {
            if(data!=null)
            {
                selectedImageUri=data.getData();
                profile_image.setImageURI(selectedImageUri);
            }
        }
    }
}