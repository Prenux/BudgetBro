package org.prenux.young.budgetbro;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements ConfirmDialogFragment.ConfirmDialogListener {

    TextView totView, expenseView,catView1,catView2;
    EditText priceEditText, whatEditText;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String catStr;
    int catInt;
    String[] categoriesArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();
        categoriesArr = res.getStringArray(R.array.wasteType);

        totView = (TextView)findViewById(R.id.totView);
        catView1 = (TextView)findViewById(R.id.catView1);
        catView2 = (TextView)findViewById(R.id.catView2);
        expenseView = (TextView)findViewById(R.id.expenseView);
        assert totView != null;
        assert catView1 != null;
        assert catView2 != null;
        assert  expenseView !=null;
        catView1.setVisibility(View.GONE);
        catView2.setVisibility(View.GONE);
        totView.setVisibility(View.GONE);
        expenseView.setVisibility(View.GONE);
        expenseView.setMovementMethod(new ScrollingMovementMethod());

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
        priceEditText = (EditText) findViewById(R.id.price_message);
        assert priceEditText != null;
        String priceStr = priceEditText.getText().toString();
        if (isNumeric(priceStr)){
            ConfirmDialogFragment dialog = new ConfirmDialogFragment();
            dialog.show(getFragmentManager(), "ConfirmDialogFragment");
        } else {
            Toast.makeText(getBaseContext(),R.string.notDigit , Toast.LENGTH_LONG).show();
        }

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
    //write data to database
        priceEditText = (EditText) findViewById(R.id.price_message);
        whatEditText = (EditText) findViewById(R.id.what_message);
        assert priceEditText != null;
        String priceStr = priceEditText.getText().toString();
        assert whatEditText != null;
        String whatStr = whatEditText.getText().toString();

        if (whatStr.matches("") || priceStr.matches("")) {
            Toast.makeText(getApplicationContext(), R.string.notSel, Toast.LENGTH_LONG).show();
            return;
        }

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c.getTime());

        DBHelper db = new DBHelper(this);
        db.insertExpense(catInt,catStr,whatStr,Float.valueOf(priceStr),formattedDate);

        priceEditText.setText("");
        whatEditText.setText("");
    }

    public void readIt(View view){

        DBHelper db = new DBHelper(this);


        //Compute big total and show it
        String totStr = String.valueOf(Math.ceil(db.getTotal()*100)/100);
        String totMessStr = getString(R.string.tot_message);
        totView.setText(String.format("%s :       %s\n", totMessStr, totStr));
        totView.setVisibility(View.VISIBLE);

        //compute categories total and show them
        StringBuilder catTotStr1 = new StringBuilder();
        StringBuilder catTotStr2 = new StringBuilder();
        double catTotDbl;
        Cursor cursor= db.getCatTot(0);

        for (int i = 0; i< categoriesArr.length ;i++){
            cursor = db.getCatTot(i);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex("price");
            catTotDbl = 0;
            while(!cursor.isAfterLast()){
                catTotDbl += cursor.getFloat(column);
                cursor.moveToNext();
            }
            if (i%2 == 1) catTotStr2.append(categoriesArr[i]).append(" :     ").append(String.valueOf(Math.ceil(catTotDbl*100)/100)).append("\n");
            else catTotStr1.append(categoriesArr[i]).append(" :     ").append(String.valueOf(Math.ceil(catTotDbl*100)/100)).append("\n");
        }
        cursor.close();
        catView1.setText(catTotStr1);
        catView2.setText(catTotStr2);
        catView1.setVisibility(View.VISIBLE);
        catView2.setVisibility(View.VISIBLE);

        //Set all expense view
        ArrayList<String> allExpense = db.getAllExpPrice();
        StringBuilder expStringBuilder = new StringBuilder();
        for (String exp: allExpense){
            expStringBuilder.append(exp).append("\n\n");
        }

        expenseView.setText(expStringBuilder);
        expenseView.setVisibility(View.VISIBLE);

    }


    public void displaySettings (MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}
