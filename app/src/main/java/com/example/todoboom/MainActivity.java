package com.example.todoboom;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import javax.sql.StatementEvent;


public class MainActivity extends AppCompatActivity
{
    private static String TAG ="MainActivity";
    private ArrayList<MyTodo> mNames = new ArrayList<>();
    int counter = 0;
    DataLoadingDialog loadingDialog = new DataLoadingDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText1 = (EditText)findViewById(R.id.input_text);
        final Button button1 = (Button)findViewById(R.id.input_button);
        final TextView TextError = (TextView) findViewById(R.id.errorText);

        if(savedInstanceState != null  && savedInstanceState.getString("names") != null){
            // if bundle isn't empty then we'll take from there.
            // conversion between bundle to mNames
            Log.d(TAG, "Read from bundle");
            String saved_string = savedInstanceState.getString("names");  // Bundle ti string
            String[] split_string = saved_string.split(",");  // create a list by split the string
            for(int j=0; j<split_string.length; j=j+5){
                MyTodo new_todo = new MyTodo(
                                                split_string[j],
                                                Long.parseLong(split_string[j+1]),
                                                split_string[j+2],
                                                split_string[j+3],
                                                Integer.parseInt(split_string[j+4]));
                mNames.add(new_todo);
            }
            initRecyclerView();
            Log.d(TAG, "finish add new todo");
        }
        else{
            takeDataFromFirebase();
            initRecyclerView();
            Log.d(TAG, "Loading from server");
            // We will take data from firebase when the user open the app
            loadingDialog.show(getSupportFragmentManager(), "loading");
            }


        button1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClickListener");
                String new_text = editText1.getText().toString();
                Date newTime = new Date();

                if(!new_text.equals("")){
                    // If user insert a valid string
                    long newID = findNewID();
                    MyTodo newTodo = new MyTodo(new_text, newID,newTime.toString(),newTime.toString(), 1);
                    mNames.add(newTodo);
                    editText1.setText(""); // Clean the input text from screen
                    TextError.setText("");  // clean the text error
                    initRecyclerView();
                    setOnFire(newTodo);
                    Log.d(TAG, "saveToFire");
                }
                else{
                    // If string is empty the user will get an error
                    Log.d(TAG, "Error: empty input");
                    TextError.setText("You can't create an empty TODO item, oh silly!");
                }
            }
        });
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.my_recyclerview);
        final RecyclerViewAdapter adaptor = new RecyclerViewAdapter(this, mNames);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptor.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Log.d(TAG, "wants to change activity");
                openActivity(mNames.get(position), position);
            }  // OnItemClick

            @Override
            public boolean onLongClick(int position) {
                //Context context = getApplicationContext();
                openMyDialog(position);
                return true;
            }  // onLongClick
        });
    }  // initRecyclerView


    private void openActivity(MyTodo singleTodo, int pos){
        if(mNames.get(pos).get_isItemDone() == 1)
        {
            Log.d(TAG, "edit activity");
            Intent intent = new Intent(this, editActivity.class);
            intent.putExtra("myString", singleTodo.getMyString());
            intent.putExtra("create", singleTodo.getTimeOfCreate());
            intent.putExtra("edit", singleTodo.getTimeOfEdit());
            intent.putExtra("pos", pos);
            startActivityForResult(intent, 1);
        }
        else
        {
            Log.d(TAG, "Done activity");
            Intent intentDone = new Intent(this, doneActivity.class);
            intentDone.putExtra("myString", singleTodo.getMyString());
            intentDone.putExtra("pos", pos);
            startActivityForResult(intentDone, 2);
        }
    }


    public void openMyDialog(final int position){
        SimpleDialog simpleDialog = new SimpleDialog();
        simpleDialog.setDeleteClickListener(new SimpleDialog.OnDeleteClickListener() {
            @Override
            public void onClickYes() {
                my_delete(position);
                deleteDataFromFire(mNames.get(position));
                Log.d(TAG, "user give a permission to delete");
            }
            @Override
            public void onClickNo() {
                // delete has been canceled
                Log.d(TAG, "delete has been canceled");
            }
        });
        simpleDialog.show(getSupportFragmentManager(), "dialog");
    }  //  openMyDialog


    private void my_delete(int position){
        mNames.remove(position);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("mNames", list2string());
        initRecyclerView();
        editor.apply();
    }


    private String list2string() {
        String new_string = "";
        if (mNames.size() > 0) {
            //for (int i=0; i<mNames.size(); i++){
            //new_string = new_string + mNames.get(i)+",";
            //}
            for (MyTodo k : mNames) {
                new_string = new_string + k.getMyString()  + "," + k.getItemID() + "," + k.getTimeOfCreate() + "," + k.getTimeOfEdit() + "," + k.get_isItemDone()+ ",";
            }
        }
        return new_string;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //MyTodo newTodo1 = new MyTodo("1111111", 111,"0000","0000", 1);
        //mNames.add(newTodo1);
        //initRecyclerView();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String currentString = data.getStringExtra("newText");
                int pos = data.getIntExtra("pos", 0);
                assert currentString != null;

                if (!currentString.equals(mNames.get(pos).getMyString())) {
                    mNames.get(pos).edit_Text(currentString);
                    Date newDate = new Date();
                    mNames.get(pos).edit_timeOfEdit(newDate.toString());
                    Log.d(TAG, "string has been updated");
                }
                if (data.getStringExtra("isItemDone").equals("y")) {
                    mNames.get(pos).edit_IsItemDone(0);
                    Date newDate = new Date();
                    mNames.get(pos).edit_timeOfEdit(newDate.toString());
                    Log.d(TAG, "time of edit has been updated");
                }
                setNewData2Firebase(mNames.get(pos));
                initRecyclerView();
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra("pos", 0);
                String isItemDone = data.getStringExtra("isItemDone");
                String isItem2Delete = data.getStringExtra("isItem2Delete");

                assert isItemDone != null;
                if (isItemDone.equals("n")) {
                    mNames.get(pos).edit_IsItemDone(1);
                    Date newDate = new Date();
                    mNames.get(pos).edit_timeOfEdit(newDate.toString());
                    setNewData2Firebase(mNames.get(pos));
                    initRecyclerView();
                    Log.d(TAG, "time of edit has been updated");
                }
                assert isItem2Delete != null;
                if (isItem2Delete.equals("y")) {
                    mNames.remove(pos);
                    deleteDataFromFire(mNames.get(pos));
                    initRecyclerView();
                    Log.d(TAG, "has been removed");
                }
            }
        }
    }


    private long findNewID(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextLong(10_000_000_000L, 100_000_000_000L);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mNames.size() == 0){ outState = null; }
        else {outState.putString("names", list2string()); }
    }


    // FireBase function

    private void takeDataFromFirebase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ItemsFolder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        MyTodo newTodo = new MyTodo(doc.get("itemString").toString(), Long.parseLong(doc.get("itemID").toString()), doc.get("itemCreteTime").toString(), doc.get("itemEditTime").toString(), Integer.parseInt(doc.get("isItemDone").toString()));
                        mNames.add(newTodo);
                    }  // Run on all items
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                initRecyclerView();
                loadingDialog.dismiss();
            }
        });
    }

    private void setOnFire(MyTodo todoItem)
    {
        // [START get_firestore_instance]
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /*
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);
        db.collection("rooms").document("roomA");
        */

        Map<String, Object> myDB = new HashMap<>();
        myDB.put("itemID", todoItem.getItemID());
        myDB.put("itemString", todoItem.getMyString());
        myDB.put("itemEditTime", todoItem.getTimeOfEdit());
        myDB.put("itemCreteTime", todoItem.getTimeOfCreate());
        myDB.put("isItemDone", todoItem.get_isItemDone());
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).set(myDB);

    }

    private void setNewData2Firebase(MyTodo todoItem){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).update("itemString", todoItem.getMyString());
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).update("itemEditTime", todoItem.getTimeOfEdit());
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).update("itemCreteTime", todoItem.getTimeOfCreate());
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).update("isItemDone", todoItem.get_isItemDone());
    }

    private void deleteDataFromFire(MyTodo todoItem){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ItemsFolder").document(""+todoItem.getItemID()).delete();
    }
}  //  MainActivity


