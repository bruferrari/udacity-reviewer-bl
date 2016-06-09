package reviews.udacity.com.booklisting.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import org.json.JSONException;
import java.util.List;
import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.adapter.BookListingAdapter;
import reviews.udacity.com.booklisting.helper.BookListingHelper;
import reviews.udacity.com.booklisting.model.Book;

/**
 * Created by bruno on 6/8/16.
 */
public class GoogleApiTask extends AsyncTask<String, String, List<Book>> {

    private Activity activity;
    private ProgressDialog progressDialog;
    private String bookTitle;
    private BookListingHelper helper = new BookListingHelper(activity);

    public GoogleApiTask(Activity activity, String bookTitle) {
        this.activity = activity;
        this.bookTitle = bookTitle;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getResources().getString(R.string.loading_message));
        progressDialog.show();
    }

    @Override
    protected List<Book> doInBackground(String... params) {
        String bookJsonStr = helper.callGoogleBooksApi(bookTitle);
        try {
            return helper.getBookDataFromJson(bookJsonStr);
        } catch (JSONException e) {
            Log.e("GoogleApiTask", "Error occurred on AsyncTask processing", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        BookListingAdapter adapter = new BookListingAdapter(activity, books);
        ((ListView) activity.findViewById(R.id.books_list_view)).setAdapter(adapter);
        progressDialog.dismiss();
    }
}
