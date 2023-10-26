package com.example.collegechat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegechat.R;
import com.example.collegechat.ModelClass.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    private EditText edtRegisterName,edtRegisterEmail,edtRegisterPassword,edtRegisterConfirmPassword;
    private Button btnSignup;
    private TextView txtSignin;
    private CircleImageView profile_image;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private Uri imgUri;
    String imageURI;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog=new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        edtRegisterName=findViewById(R.id.edtRegisterName);
        edtRegisterEmail=findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword=findViewById(R.id.edtRegisterPassword);
        edtRegisterConfirmPassword=findViewById(R.id.edtRegisterConfirmPassword);
        btnSignup=findViewById(R.id.btnSignUp);
        txtSignin=findViewById(R.id.txtSignin);
        profile_image=findViewById(R.id.profile_image);



        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String name=edtRegisterName.getText().toString();
                String email=edtRegisterEmail.getText().toString();
                String pass=edtRegisterPassword.getText().toString();
                String confirmPass=edtRegisterConfirmPassword.getText().toString();
                String status="Hey There I'm Using This Application";
                //String imageURI;

                if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)||TextUtils.isEmpty(confirmPass))
                {

                    Toast.makeText(RegistrationActivity.this, "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                }
                else if(!email.matches(emailPattern))
                {
                    edtRegisterEmail.setError("Please Enter Valid Email");
                    Toast.makeText(RegistrationActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();
                } else if (!pass.equals(confirmPass))
                {
                    Toast.makeText(RegistrationActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }
                else if(pass.length()<6)
                {
                    Toast.makeText(RegistrationActivity.this, "Enter minimum 6 character password", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegistrationActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());


                                if(imgUri!=null)
                                {
                                    storageReference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if(task.isSuccessful())
                                            {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        imageURI=uri.toString();

                                                        Users users=new Users(auth.getUid(),name,email,imageURI,status);

                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    progressDialog.dismiss();
                                                                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                                    finish();
                                                                }
                                                                else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(RegistrationActivity.this, "Error in Creating Account", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                                else
                                {
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/collegechat-67978.appspot.com/o/profile_icon.jpg?alt=media&token=3c882327-64ca-4a87-aee4-45f188c9a90b";

                                    Users users=new Users(auth.getUid(),name,email,imageURI,status);

                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(RegistrationActivity.this, "Error in Creating Account", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }

                            }
                            else
                            {
                                Toast.makeText(RegistrationActivity.this, "Error in Registration", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }





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


        txtSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
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
                imgUri=data.getData();
                profile_image.setImageURI(imgUri);
            }
        }
    }
}