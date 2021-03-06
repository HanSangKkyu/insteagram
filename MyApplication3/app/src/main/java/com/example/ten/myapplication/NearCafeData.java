package com.example.ten.myapplication;

import java.io.Serializable;

public class NearCafeData implements Serializable{
    String name;
    String id;
    String img;

    public NearCafeData(String name, String id, String img) {
        this.name = name;
        this.id = id;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
