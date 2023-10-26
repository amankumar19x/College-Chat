package com.example.collegechat.Adapter;

import static com.example.collegechat.Activity.ChatActivity.rImage;
import static com.example.collegechat.Activity.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collegechat.ModelClass.Messages;
import com.example.collegechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==ITEM_SEND)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            SenderViewHolder viewHolder=new SenderViewHolder(view);
            return viewHolder;
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.receiver_layout_item,parent,false);
            ReceiverViewHolder viewHolder=new ReceiverViewHolder(view);
            return viewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);

        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.txtMessage.setText(messages.getMessage());
            Picasso.get().load(sImage).into(viewHolder.sender_image);

        }else
        {
            ReceiverViewHolder  viewHolder=(ReceiverViewHolder) holder;
            viewHolder.txtMessage.setText(messages.getMessage());
            Picasso.get().load(rImage).into(viewHolder.receiver_image);
        }

    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderID()))
        {
            return ITEM_SEND;
        }
        else
        {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder
    {

        CircleImageView sender_image;
        TextView txtMessage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sender_image=itemView.findViewById(R.id.profile_image);
            txtMessage=itemView.findViewById(R.id.txtMessages);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView receiver_image;
        TextView txtMessage;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiver_image=itemView.findViewById(R.id.profile_image);
            txtMessage=itemView.findViewById(R.id.txtMessages);
        }
    }
}
