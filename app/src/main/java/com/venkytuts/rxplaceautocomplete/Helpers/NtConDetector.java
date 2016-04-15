package com.venkytuts.rxplaceautocomplete.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author Venkatesh Selvam <venkatselva8@gmail.com>
 *         <p/>
 *         NtConDetector class detects the Intenet Connection
 */

public class NtConDetector {

    /**
     * Get the network info
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = NtConDetector.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

}