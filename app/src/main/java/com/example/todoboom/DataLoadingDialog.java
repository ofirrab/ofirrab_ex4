package com.example.todoboom;

import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


public class DataLoadingDialog extends AppCompatDialogFragment {
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("loading").setTitle("Wait for server");
        return builder.create();
    }
}
