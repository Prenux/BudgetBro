package org.prenux.young.budgetbro;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void deleteData (View view){

        DBHelper db = new DBHelper(this);
        db.deleteDb(this);
        Toast.makeText(getApplicationContext(), "All Forgotten!", Toast.LENGTH_LONG).show();

    }

    public void showNoticeDialog(View view) {
        // Create an instance of the dialog fragment and show it
        ConfirmDialogFragment dialog = new ConfirmDialogFragment();
        dialog.show(getFragmentManager(), "ConfirmDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteData(getCurrentFocus());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void goToMain (View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToModifyExp (View view){
        Intent intent = new Intent(this, ModifyExpActivity.class);
        startActivity(intent);
    }
}
