package reviews.udacity.com.booklisting.helper;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.task.GoogleApiTask;

/**
 * Created by bruno on 6/8/16.
 */
public class BookListingHelper {

    private Activity activity;
    private EditText bookTitleField;
    private CoordinatorLayout coordinatorLayout;

    public BookListingHelper(Activity activity) {
        this.activity = activity;
    }

    public void initializeUIElements() {
        this.bookTitleField = (EditText) activity.findViewById(R.id.book_title_field);
        this.coordinatorLayout = (CoordinatorLayout) activity.findViewById(R.id.coordinator_layout);
    }

    public void getBooksByTitle(Activity activity){
        String bookTitle = this.bookTitleField.getText().toString().trim();
        try {
            if (titleIsValid(bookTitle))
                new GoogleApiTask(activity, bookTitle).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    activity.getResources().getString(R.string.book_empty_title),
                    Snackbar.LENGTH_LONG).setAction("Action", null);
            snackbar.show();
        }
    }

    private boolean titleIsValid(String title) throws MalformedURLException {
        if (title.isEmpty())
            throw new MalformedURLException("Book title must not be empty or null");
        return true;
    }

    public String callGoogleBooksApi(String bookTitle) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + bookTitle
                    + "&maxResults=10");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("BookListingHelper", "Error while trying to close stream", e);
                }
        }
        return null;
    }

}
