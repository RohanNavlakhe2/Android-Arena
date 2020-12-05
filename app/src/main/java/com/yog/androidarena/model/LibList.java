package com.yog.androidarena.model;

public class LibList {
    private String libName;
    private String libShortDesc;
    private int orderBy;

    public LibList(String libName, String libShortDesc,int orderBy) {
        this.libName = libName;
        this.libShortDesc = libShortDesc;
        this.orderBy = orderBy;
    }

    public LibList()
    {}

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getLibShortDesc() {
        return libShortDesc;
    }

    public void setLibShortDesc(String libShortDesc) {
        this.libShortDesc = libShortDesc;
    }
}
