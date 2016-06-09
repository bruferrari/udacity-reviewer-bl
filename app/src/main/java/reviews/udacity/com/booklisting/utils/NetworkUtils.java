package reviews.udacity.com.booklisting.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by bruno on 6/9/16.
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
