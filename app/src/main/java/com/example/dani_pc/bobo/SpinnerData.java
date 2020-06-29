package com.example.dani_pc.bobo;

public class SpinnerData {
    private int icon;
    private String iconName;

    public SpinnerData(int icon, String iconName) {
        this.icon = icon;
        this.iconName = iconName;
    }

    public int getIcon(){
        return icon;
    }

    public String getIconName(){
        return iconName;
    }
}
