package org.prenux.young.budgetbro;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener {

    public final static String PRICE_EXTRA = "org.prenux.budgetbuddy.PRICE";
    public final static String WHAT_EXTRA = "org.prenux.budgetbuddy.WHAT";
    TextView textView;
    EditText priceEditText, whatEditText;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String catStr;
    int catInt;
    int totalInt;
    ArrayList<Integer> catTotArrList = new ArrayList<>();
    ArrayList<String[]> finalArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView)findViewById(R.id.textview);
        assert textView != null;
        textView.setVisibility(View.GONE);
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.wasteType,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        confirm();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    public void confirm(){
    //write data to file
        priceEditText = (EditText) findViewById(R.id.price_message);
        whatEditText = (EditText) findViewById(R.id.what_message);
        assert priceEditText != null;
        String priceStr = priceEditText.getText().toString();
        assert whatEditText != null;
        String whatStr = whatEditText.getText().toString();

        if (whatStr.matches("") || priceStr.matches("")) {
            Toast.makeText(getApplicationContext(), "Nothing to confess!!!", Toast.LENGTH_LONG).show();
            return;
        }
        String catIntStr = String.valueOf(catInt);
        String datStr = String.format("%s,%s,%s,%s \r\n", catIntStr, catStr, whatStr, priceStr);
        String datFile = "data";

        try {
            FileOutputStream fileOutputStream = openFileOutput(datFile, MODE_APPEND);
            fileOutputStream.write(datStr.getBytes());
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(), "I'll remember!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        priceEditText.setText("");
        whatEditText.setText("");
    }

    public void readIt(View view){
        ArrayList<String[]> csvArrayList = new ArrayList<>();
        FileInputStream fileInputStream;
        try {
            String datStr;
            fileInputStream = openFileInput("data");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ((datStr=bufferedReader.readLine()) != null) {
                String[] tmpArray = datStr.split(",");
                csvArrayList.add(tmpArray);
            }
            analyzeIt(csvArrayList);
            for (int i : catTotArrList){
                try {
                    catStr = "Hello";
                    String cost = String.valueOf(i);
                    String displayStr = String.format("%s : %s$", catStr, cost);
                    stringBuilder.append(displayStr).append("\n");
                    textView.setText(stringBuilder);
                    textView.setVisibility(View.VISIBLE);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void analyzeIt(ArrayList<String[]> csvArrayList){
        totalInt = 0;


        // csvArrayList[x] = [[catInt, catStr, whatStr, priceStr],...]
        for(String[]  stringArray : csvArrayList){

            totalInt += Integer.valueOf(stringArray[3]);

            finalArrayList.add(Integer.valueOf(stringArray[0]), Arrays.copyOfRange(stringArray, 2, stringArray.length));

            catTotArrList.add(Integer.valueOf(stringArray[0]), catTotArrList.get(Integer.valueOf(stringArray[0])) + Integer.valueOf(stringArray[3]));
        }

        //hello

    }

    public void displaySettings (MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
