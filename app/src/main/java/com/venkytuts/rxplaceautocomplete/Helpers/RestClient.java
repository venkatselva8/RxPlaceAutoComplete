package com.venkytuts.rxplaceautocomplete.Helpers;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * @author Venkatesh Selvam <venkatselva8@gmail.com>
 *         <p/>
 *         RestClient class generates an implementation of the PlaceApi interface.
 *         <p/>
 *         Sample URL to make Request
 *         https://maps.googleapis.com/maps/api/place/autocomplete/json?input=YOUR_INPUT&key=YOUR_API_KEY
 */
public class RestClient {
    private static PlaceApi REST_CLIENT;
    private static String ROOT =
            "https://maps.googleapis.com/maps/api/place/autocomplete";

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    public static PlaceApi get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()));
        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(PlaceApi.class);
    }
}

