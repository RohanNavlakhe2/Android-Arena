package com.yog.androidarena.model;

public class LibList {
    private String libName;
    private String libShortDesc;

    public LibList(String libName, String libShortDesc) {
        this.libName = libName;
        this.libShortDesc = libShortDesc;
    }

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
