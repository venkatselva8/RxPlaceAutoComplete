package com.venkytuts.rxplaceautocomplete;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.venkytuts.rxplaceautocomplete.Helpers.NtConDetector;
import com.venkytuts.rxplaceautocomplete.Helpers.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author Venkatesh Selvam <venkatselva8@gmail.com>
 *         <p/>
 *         MainActivity class generates an implementation of the PlaceApi interface.
 */
/*You'll need a Google Server API key for you application.
    There are instructions on how to set up your API project and
    generate a Server key

     Link :  https://developers.google.com/places/web-service/get-api-key

     */
public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView atvPlaces;
    AutoCompleteTextView atvCity;
    String apiKey = "Your_API_Key"; //Add your Server API Key
    TextView clickedItem;
    ArrayAdapter<String> adapter;
    NtConDetector ncd;
    AlertDialog.Builder alertDialogBuilder;
    Context Activity_context;
    AlertDialog alertDialog;
    Toast Querytoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Initialize AutoCompleteTextView */
        atvCity = (AutoCompleteTextView) findViewById(R.id.atv_city);
        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);

        atvPlaces.setThreshold(2); //will start working from Second character
        atvCity.setThreshold(2);

        clickedItem = (TextView) findViewById(R.id.clickedItem);

        ncd = new NtConDetector(); //Initialize NtConDetector- To Detect Internet Connection

        Activity_context = MainActivity.this; //Initialize Context

        // Only 1000 requests for a Server API key per Day. So we intimate to the User when Query limit reached .
        Querytoast = Toast.makeText(MainActivity.this, "API-Query limit reached. Try tommorrow", Toast.LENGTH_LONG);

        atvPlaces.addTextChangedListener(new TextWatcher() {
            //  This method is called to notify you that, within s,
            // the count characters beginning at start have just replaced old text that had length before.
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Check Internet Connection
                if (!ncd.isConnected(MainActivity.this)) {
                    ShowNoInternetDialog();
                    return;
                }
                // Calling Google Maps Api for the User given text
                CallMapsApiPlace(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // This method is called to notify you that, within s, the count characters beginning at start
                // are about to be replaced by new text with length after.
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This method is called to notify you that, somewhere within s, the text has been changed.
            }
        });
        // To get the text of Clicked Item.
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (adapter != null) {
                    clickedItem.setText(adapter.getItem(position));
                }
            }
        });

        //To Clear all the texts
        atvPlaces.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (atvPlaces.getRight() - atvPlaces.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        atvPlaces.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });


        // As like above methods for Search City - EditText
        atvCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!ncd.isConnected(MainActivity.this)) {
                    ShowNoInternetDialog();
                    return;
                }
                // Calling Google Maps Api for the User given text
                CallMapsApiCity(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        atvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (adapter != null) {
                    clickedItem.setText(adapter.getItem(position));
                }
            }
        });

        atvCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (atvCity.getRight() - atvCity.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        atvCity.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });

    }


    //Request the Maps Places Api for Cities
    private void CallMapsApiCity(CharSequence s) {

        RestClient.get().autocompleteCity(apiKey, "(cities)", s.toString(),
                new Callback<PlacesResult>() {
                    @Override
                    public void success(final PlacesResult placesResult, Response res) {
                        if (placesResult.status.equals("OK")) {
                            List<String> strings = new ArrayList<String>();
                            for (Prediction p : placesResult.predictions) {
                                strings.add(p.description);
                            }
                            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
                            atvCity.setAdapter(adapter);
                        } else if (placesResult.status.equals("OVER_QUERY_LIMIT")) {
                            ShowQueryToast();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }


    //Request the Maps Places Api for Places (or) Address
    private void CallMapsApiPlace(CharSequence s) {

        RestClient.get().autocompletePlace(apiKey, s.toString(),
                new Callback<PlacesResult>() {
                    @Override
                    public void success(final PlacesResult placesResult, Response res) {
                        if (placesResult.status.equals("OK")) {
                            List<String> strings = new ArrayList<String>();
                            for (Prediction p : placesResult.predictions) {
                                strings.add(p.description);
                            }
                            adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
                            atvPlaces.setAdapter(adapter);
                        } else if (placesResult.status.equals("OVER_QUERY_LIMIT")) {
                            ShowQueryToast();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }
        );
    }

    // Get the Response - places result
    public class PlacesResult {
        @Expose
        List<Prediction> predictions;
        @Expose
        String status;
    }

    public class Prediction {
        @Expose
        String description;
    }

    //Show no Intenet dialog & show the wifi and mobile data to connect the internet
    public void ShowNoInternetDialog() {
        alertDialogBuilder = new AlertDialog.Builder(Activity_context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(Activity_context.getResources().getString(R.string.noInternet_title));
        alertDialogBuilder.setMessage(Activity_context.getResources().getString(R.string.noInternet_msg));
        alertDialogBuilder.setPositiveButton(Activity_context.getResources().getString(R.string.mobile_data), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(
                            "com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                    Activity_context.startActivity(intent);
                } catch (Exception e) {
                    Log.v("Exception", "Alertdialog setting " + e);
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    Activity_context.startActivity(intent);
                }
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton(Activity_context.getResources().getString(R.string.wifi), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                Activity_context.startActivity(intent);
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton(Activity_context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // Show the Query limit reached Toast.
    public void ShowQueryToast() {
        if (Querytoast != null) {
            Querytoast.show();
        }
    }

}

