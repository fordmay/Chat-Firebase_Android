package com.example.dmitro.chat_firebase.adapter;

public class Message {
    private String id;
    private String name;
    private String text;
    private String photoUrl;

    public Message(){
    }
    public Message(String name,String text,String photoUrl){
        this.name = name;
        this.text = text;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
