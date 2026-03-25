package com.example.qlct;

public class Category {

    private long   id;
    private String name;
    private int    iconRes;
    private int    color;

    public Category(long id, String name, int iconRes, int color) {
        this.id      = id;
        this.name    = name;
        this.iconRes = iconRes;
        this.color   = color;
    }

    // Getters & Setters
    public long   getId()                 { return id; }
    public void   setId(long id)          { this.id = id; }

    public String getName()               { return name; }
    public void   setName(String name)    { this.name = name; }

    public int    getIconRes()            { return iconRes; }
    public void   setIconRes(int iconRes) { this.iconRes = iconRes; }

    public int    getColor()              { return color; }
    public void   setColor(int color)     { this.color = color; }
}
