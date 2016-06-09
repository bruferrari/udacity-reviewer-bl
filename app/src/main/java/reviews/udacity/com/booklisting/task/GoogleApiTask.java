package reviews.udacity.com.booklisting.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.helper.BookListingHelper;

/**
 * Created by bruno on 6/8/16.
 */
public class GoogleApiTask extends AsyncTask<String, String, String> {

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
    protected String doInBackground(String... params) {
        return helper.callGoogleBooksApi(bookTitle);
    }

    @Override
    protected void onPostExecute(String result) {
//        System.out.println("RESULT: " + result);
        progressDialog.dismiss();
    }
}
