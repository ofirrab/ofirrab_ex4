package com.example.todoboom;


public class MyTodo {
    private String myString;
    private long itemID;
    private String timeOfCreate;
    private String timeOfEdit;
    private int isItemDone;

    public MyTodo(String my_String, long item_ID, String time_Of_Create, String time_Of_Edit,int is_Item_Done){
        this.myString = my_String;
        this.itemID = item_ID;
        this.timeOfCreate = time_Of_Create;
        this.timeOfEdit = time_Of_Edit;
        this.isItemDone = is_Item_Done;
    }

    // Change val
    public void edit_Text(String my_String){this.myString = my_String;}

    // public void edit_itemID(long itemID){this.itemID = item_ID;}

    public void edit_timeOfCreate(String time_Of_Creat){this.timeOfCreate=time_Of_Creat;}

    public void edit_timeOfEdit(String time_Of_Edit){ this.timeOfEdit=time_Of_Edit;}

    public void edit_IsItemDone(int is_Item_Done){this.isItemDone=is_Item_Done;}


    // Get val
    public String getMyString(){return this.myString; }

    public String getTimeOfCreate(){return this.timeOfCreate;}

    public String getTimeOfEdit(){return  this.timeOfEdit;}

    public int get_isItemDone(){return  this.isItemDone;}

    public long getItemID(){return  this.itemID;}




}
