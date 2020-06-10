package com.example.todoboom;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class doneActivity extends AppCompatActivity {

    private static String TAG ="doneActivity";
    Button undoneClick;
    Button deleteClick;
    TextView editText;

    String isItemDone = "y";
    String isItem2Delete = "n";
    int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        Log.d(TAG, "onCreateDone");
        undoneClick = findViewById(R.id.undoneButton);
        deleteClick = findViewById(R.id.deleteButton);
        editText = findViewById(R.id.editText);

        Intent intent = getIntent();
        editText.setText(intent.getStringExtra("myString"));
        pos = intent.getIntExtra("pos", -1);  //0

        deleteClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "deleteClick");
                openMyDialog();
                //isItem2Delete = "y";
                //edit2Main();


            }
        });
        undoneClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isItemDone = "n";
                edit2Main();
            }
        });
    }  // onCreate

    private void openMyDialog(){
        SimpleDialog newDialog = new SimpleDialog();
        newDialog.setDeleteClickListener(new SimpleDialog.OnDeleteClickListener()
        {
            @Override
            public void onClickYes() {
                Log.d(TAG, "YesClick");
                isItem2Delete = "y";
                edit2Main();
            }

            @Override
            public void onClickNo() {
                // nothing will happen
                Log.d(TAG, "noClick");
                edit2Main();  // bring back to main activity
            }
        });
        newDialog.show(getSupportFragmentManager(), "delete dialog");
    }

    private void edit2Main(){
        Intent intent2Main = new Intent();
        intent2Main.putExtra("isItemDone", isItemDone);
        intent2Main.putExtra("isItem2Delete", isItem2Delete);
        intent2Main.putExtra("pos", pos);
        setResult(RESULT_OK, intent2Main);
        finish();
    }
}  // doneActivity
