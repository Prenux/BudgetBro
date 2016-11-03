package org.prenux.young.budgetbro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ModifyExpActivity extends AppCompatActivity {

    DatePicker datePicker;
    EditText newPriceEditText,newWhatEditText, elementIDEditText;
    Spinner spinnerCat;
    Spinner spinnerDay;
    Spinner spinnerMonth;
    Spinner spinnerYear;
    ArrayAdapter<CharSequence> adapter;
    String catStr;
    int catInt;
    boolean dateChkBox = false;
    boolean catChkBox = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_exp);

        initSpinner(R.id.spinnerModifyCat, R.array.wasteType);
    }

    private void initSpinner(int view, int array){
        Spinner spinner = (Spinner) findViewById(view);
        adapter = ArrayAdapter.createFromResource(this, array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assert spinner != null;
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catStr = (String) parent.getItemAtPosition(position);
                catInt = (int)  parent.getItemIdAtPosition(position);
                Toast.makeText(getBaseContext(),parent.getItemAtPosition(position) + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void updateExp (View view){

        DBHelper db = new DBHelper(this);
        newPriceEditText = (EditText) findViewById(R.id.mod_price_message);
        newWhatEditText = (EditText) findViewById(R.id.mod_what_message);
        elementIDEditText = (EditText) findViewById(R.id.elementID);
        assert newPriceEditText != null;
        String priceStr = newPriceEditText.getText().toString();
        assert newWhatEditText != null;
        String whatStr = newWhatEditText.getText().toString();
        assert elementIDEditText != null;
        Integer elementID = Integer.parseInt(elementIDEditText.getText().toString());

        if (whatStr.matches("") || priceStr.matches("") || elementID <= 0) {
            Toast.makeText(getApplicationContext(), R.string.notSel, Toast.LENGTH_LONG).show();
            return;
        }

        datePicker = (DatePicker) findViewById(R.id.spinnerModifyDate);
        assert datePicker != null;
        int day  = datePicker.getDayOfMonth();
        int month= datePicker.getMonth();
        month ++;                           //jan=0 dec=11!!
        int year = datePicker.getYear();

        String dateStr = String.valueOf(day) + "/" + String.valueOf(month) + "/"
                + String.valueOf(year);

        SimpleDateFormat curFormatter = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        Date date = null;
        try {
            date = curFormatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postSdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = postSdf.format(date);

        String res = db.updateExpense(elementID, catInt, catStr,whatStr,Float.valueOf(priceStr),formattedDate);
        Toast.makeText(getApplicationContext(), String.valueOf(month), Toast.LENGTH_LONG).show();
        if (!Objects.equals(res, "")){
            Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
        }
        elementIDEditText.setText("");
        newPriceEditText.setText("");
        newWhatEditText.setText("");
    }

/*
    public void dateClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        dateChkBox = checkBox.isChecked();
    }

    public void catClicked(View v) {
        //code to check if this checkbox is checked!
        CheckBox checkBox = (CheckBox)v;
        catChkBox = checkBox.isChecked();
    }*/

    public void goToSettings (View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
