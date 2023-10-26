package com.example.collegechat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collegechat.Adapter.MessagesAdapter;
import com.example.collegechat.ModelClass.Messages;
import com.example.collegechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String receiverName,receiverImage,receiverUID;
    private CircleImageView profile_image;
    private TextView txtReceiverName;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private String senderImage;
    private EditText edtMessage;
    private CardView sendBtn;
    private RecyclerView messageRecyclerView;


    private String senderRoom,receiverRoom;
    public static String sImage,rImage;
    private ArrayList<Messages> messagesArrayList;

    private MessagesAdapter messagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesArrayList=new ArrayList<>();

        profile_image=findViewById(R.id.profile_image);
        txtReceiverName=findViewById(R.id.txtReceiverName);
        edtMessage=findViewById(R.id.edtMessage);
        sendBtn=findViewById(R.id.sendBtn);
        messageRecyclerView=findViewById(R.id.messageRecyclerView);

        LinearLayoutManager layoutManager=new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(layoutManager);
        messagesAdapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageRecyclerView.setAdapter(messagesAdapter);


        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();


        receiverName=getIntent().getStringExtra("name");
        receiverImage=getIntent().getStringExtra("ReceiverImage");
        receiverUID=getIntent().getStringExtra("uid");

        rImage = receiverImage;

        senderRoom=auth.getUid()+receiverUID;
        receiverRoom=receiverUID+auth.getUid();

        Picasso.get().load(receiverImage).into(profile_image);
        txtReceiverName.setText(receiverName+"");


        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference=database.getReference().child("chats").child(senderRoom).child("messages");



        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                messagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                senderImage=snapshot.child("imageUri").getValue().toString();
                sImage=senderImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=edtMessage.getText().toString();
                if(message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this, "Please enter valid messages...", Toast.LENGTH_SHORT).show();
                }

                edtMessage.setText("");

                Date date=new Date();

                Messages messages=new Messages(message,auth.getUid(),date.getTime());

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push()
                                        .setValue(messages)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        });
            }
        });
    }
}