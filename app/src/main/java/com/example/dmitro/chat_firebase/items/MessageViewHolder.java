package com.example.dmitro.chat_firebase.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmitro.chat_firebase.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public ImageView messengerAvatar;
    public TextView messengerName;
    public TextView massageText;

    public MessageViewHolder(View itemView) {
        super(itemView);
        messengerAvatar = (ImageView) itemView.findViewById(R.id.messenger_avatar);
        messengerName = (TextView) itemView.findViewById(R.id.messenger_name);
        massageText = (TextView) itemView.findViewById(R.id.massage_text);
    }
}
