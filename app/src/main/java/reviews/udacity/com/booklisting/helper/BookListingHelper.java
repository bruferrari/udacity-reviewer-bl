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
import java.net.URL;
import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.exception.BookValidationException;
import reviews.udacity.com.booklisting.task.GoogleApiTask;

/**
 * Created by bruno on 6/8/16.
 */
public class BookListingHelper {

    private static final int API_QUERY_MAX_RESULTS = 2;

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
        } catch (BookValidationException e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                    activity.getResources().getString(R.string.book_empty_title),
                    Snackbar.LENGTH_LONG).setAction("Action", null);
            snackbar.show();
        }
    }

    private boolean titleIsValid(String title) throws BookValidationException {
        if (title.isEmpty())
            throw new BookValidationException("Book title must not be empty or null");
        return true;
    }

    private HttpURLConnection getUrlConnection(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
        } catch (IOException e) {
            Log.e("BookListingHelper", "Error occurred while getting url connection", e);
        }
        return urlConnection;
    }

    public String callGoogleBooksApi(String bookTitle) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        StringBuffer buffer;

        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + bookTitle
                    + "&maxResults=" + API_QUERY_MAX_RESULTS);

            conn = getUrlConnection(url);
            InputStream inputStream = conn.getInputStream();

            reader = generateBufferedReader(inputStream);
            buffer = generateStringBuffer(reader);

            if (buffer != null)
                return buffer.toString();

        } catch (Exception e) {
            Log.e("BookListingHelper", "Error occurred while calling Google Books API", e);
        } finally {
            closeUrlConnection(conn);
            closeReaderConnection(reader);
        }
        return null;
    }

    private BufferedReader generateBufferedReader(InputStream inputStream) {
        if (inputStreamIsNull(inputStream))
            throw new NullPointerException("InputStream is null");
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private StringBuffer generateStringBuffer(BufferedReader reader) throws IOException {
        StringBuffer buffer = new StringBuffer();

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line + "\n");
        }

        if (buffer.length() != 0)
            return buffer;

        return null;
    }

    private void closeUrlConnection(HttpURLConnection conn) {
        if (conn != null)
            conn.disconnect();
    }

    private void closeReaderConnection(BufferedReader reader) {
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                Log.e("BookListingHelper", "Error while trying to close stream", e);
            }
    }

    private boolean inputStreamIsNull(InputStream stream) {
        return stream == null;
    }

}
