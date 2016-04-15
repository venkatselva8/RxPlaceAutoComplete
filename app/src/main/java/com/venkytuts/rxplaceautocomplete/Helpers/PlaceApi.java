package com.venkytuts.rxplaceautocomplete.Helpers;

import com.venkytuts.rxplaceautocomplete.MainActivity;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * @author Venkatesh Selvam <venkatselva8@gmail.com>
 *         <p/>
 *         Retrofit turns your HTTP API into a Java interface (PlaceApi) .
 *         <p/>
 *         Sample URL to make Request
 *         https://maps.googleapis.com/maps/api/place/autocomplete/json?input=YOUR_INPUT&key=YOUR_API_KEY
 */
public interface PlaceApi {
    //To search Place
    @GET("/json")
    void autocompletePlace(
            @Query("key") String key,
            @Query("input") String input, Callback<MainActivity.PlacesResult> callback);

    //To search City
    @GET("/json")
    void autocompleteCity(
            @Query("key") String key,
            @Query("types") String types,
            @Query("input") String input, Callback<MainActivity.PlacesResult> callback);
}
