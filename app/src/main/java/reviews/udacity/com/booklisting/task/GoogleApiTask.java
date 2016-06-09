package reviews.udacity.com.booklisting.task;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import java.util.ArrayList;
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
    private BookListingHelper helper;

    public GoogleApiTask(Activity activity, String bookTitle) {
        this.activity = activity;
        this.bookTitle = bookTitle;
        this.helper = new BookListingHelper(activity);
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
        } catch (NetworkErrorException e) {
            e.printStackTrace();
            this.UiFeedbackHandler();
        }
        return new ArrayList<>();
    }

    private void UiFeedbackHandler() {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.coordinator_layout),
                activity.getResources().getString(R.string.no_server_response_error),
                Snackbar.LENGTH_INDEFINITE).setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
        snackbar.show();
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        TextView emptyListMsg = (TextView) activity.findViewById(R.id.book_list_empty_msg);
        if (books.isEmpty()) {
            emptyListMsg.setVisibility(View.VISIBLE);
        } else {
            emptyListMsg.setVisibility(View.GONE);
            BookListingAdapter adapter = new BookListingAdapter(activity, books);
            ((ListView) activity.findViewById(R.id.book_list_view)).setAdapter(adapter);
        }
        progressDialog.dismiss();
    }
}
