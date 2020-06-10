package com.example.todoboom;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class editActivity extends AppCompatActivity {

    private static String TAG ="editActivity";
    // All button & text
    Button doneClick;
    Button applyClick;
    EditText edit_Text;
    TextView time_Of_Create;
    TextView time_Of_Edit;

    // Inner parameter
    String isItemDone = "n";
    String  timeOfCreate_string;
    String timeOfEdit_string;
    String newText;
    int pos;

    // onCreate
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        doneClick = findViewById(R.id.doneButton);
        applyClick = findViewById(R.id.applyButton);
        edit_Text = findViewById(R.id.editText);
        time_Of_Create= findViewById(R.id.timeOfCreate);
        time_Of_Edit = findViewById(R.id.timeOfEdit);

        Intent intent = getIntent();
        // Get raw DATA
        edit_Text.setText(intent.getStringExtra("myString"));
        String timeOfEdit_string_raw_data = intent.getStringExtra("edit");
        String timeOfCreate_string_raw_data = intent.getStringExtra("create");
        pos = intent.getIntExtra("pos", 0);

        // Take from raw DATA
        timeOfEdit_string = timeOfEdit_string_raw_data.substring(0, timeOfEdit_string_raw_data.length()-18);
        timeOfCreate_string = timeOfCreate_string_raw_data.substring(0,timeOfCreate_string_raw_data.length()-18);

        // Set the final text
        time_Of_Edit.setText("Edit time was "+ timeOfEdit_string);
        time_Of_Create.setText("Create time was " + timeOfCreate_string);

        doneClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "doneClick");
                newText = edit_Text.getText().toString();
                isItemDone = "y";
                edit2Main();
            }
        });

        applyClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "applyClick");
                newText = edit_Text.getText().toString();
                edit2Main();

            }
        });
    }  // onCreate1"

    private void edit2Main(){
        Intent intent2Main = new Intent();
        intent2Main.putExtra("isItemDone", isItemDone);
        intent2Main.putExtra("newText", newText);
        intent2Main.putExtra("pos", pos);
        Log.d(TAG, "return " + isItemDone + newText + pos);
        setResult(RESULT_OK, intent2Main);
        finish();  // Just after the app will be close the intent will be delivered
    }
}  // editActivity
