package reviews.udacity.com.booklisting.helper;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.exception.BookValidationException;
import reviews.udacity.com.booklisting.model.Author;
import reviews.udacity.com.booklisting.model.Book;
import reviews.udacity.com.booklisting.task.GoogleApiTask;
import reviews.udacity.com.booklisting.utils.NetworkUtils;

/**
 * Created by bruno on 6/8/16.
 */
public class BookListingHelper {

    private static final int API_QUERY_MAX_RESULTS = 25;

    private Activity activity;
    private EditText bookTitleField;
    private CoordinatorLayout coordinatorLayout;
    private ListView booksList;

    public BookListingHelper(Activity activity) {
        this.activity = activity;
    }

    public void initializeUIElements() {
        this.bookTitleField = (EditText) activity.findViewById(R.id.book_title_field);
        this.coordinatorLayout = (CoordinatorLayout) activity.findViewById(R.id.coordinator_layout);
        this.booksList = (ListView) activity.findViewById(R.id.book_list_view);
    }

    public void bookSelectionHandler() {
        booksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book selectedBook = (Book) booksList.getAdapter().getItem(position);
                Uri webReaderLink = Uri.parse(selectedBook.getWebReaderLink());
                Intent webVisualizer = new Intent(Intent.ACTION_VIEW, webReaderLink);
                if (webVisualizer.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(webVisualizer);
                }
            }
        });
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
        String bookJsonStr = null;

        bookTitle = parseBookTitleBlankSpaces(bookTitle);

        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=" + bookTitle
                    + "&maxResults=" + API_QUERY_MAX_RESULTS);

            conn = getUrlConnection(url);
            InputStream inputStream = conn.getInputStream();

            reader = generateBufferedReader(inputStream);
            StringBuffer buffer = generateStringBuffer(reader);

            if (buffer != null)
                bookJsonStr = buffer.toString();

            return bookJsonStr;

        } catch (Exception e) {
            Log.e("BookListingHelper", "Error occurred while calling Google Books API", e);
        } finally {
            closeUrlConnection(conn);
            closeReaderConnection(reader);
        }
        return null;
    }

    private String parseBookTitleBlankSpaces(String bookTitle) {
        if (bookTitle.contains(" "))
            bookTitle = bookTitle.replace(" ", "%20");
        return bookTitle;
    }

    public List<Book> getBookDataFromJson(String bookJsonStr) throws JSONException,
            NetworkErrorException {

        if (NetworkUtils.isNetworkNotAvailable(activity))
            throw new NetworkErrorException("No server response. Check network connection");

        List<Book> bookList = new ArrayList<>();

        final String BOOK_LIST = "items";
        final String BOOK_INFO = "volumeInfo";
        final String BOOK_TITLE = "title";
        final String LIST_BOOK_AUTHORS = "authors";
        final String BOOK_ID = "id";
        final String BOOK_WEB_READER = "webReaderLink";

        JSONObject bookJson = new JSONObject(bookJsonStr);
        JSONArray booksArray;
        try {
            booksArray = bookJson.getJSONArray(BOOK_LIST);
        } catch (JSONException e) {
            booksArray = null;
        }

        if (booksArray != null) {
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject bookInfo = booksArray.getJSONObject(i).getJSONObject(BOOK_INFO);
                String bookId = booksArray.getJSONObject(i).getString(BOOK_ID);
                String bookWebReaderLink = booksArray.getJSONObject(i).getJSONObject("accessInfo")
                        .getString(BOOK_WEB_READER);

                JSONArray authorsArray;
                try {
                    authorsArray = bookInfo.getJSONArray(LIST_BOOK_AUTHORS);
                } catch (JSONException e) {
                    authorsArray = null;
                }

                String bookTitle = bookInfo.getString(BOOK_TITLE);

                List<Author> authors = new ArrayList<>();
                Book book = new Book();
                book.setTitle(bookTitle);
                book.setAuthors(authors);
                book.setId(bookId);
                book.setWebReaderLink(bookWebReaderLink);

                bookList.add(book);

                if (authorsArray != null) {
                    for (int j = 0; j < authorsArray.length(); j++) {
                        Author author = new Author();
                        author.setFullName(authorsArray.get(j).toString());
                        authors.add(author);
                    }
                }
            }
        }
        return bookList;
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
            buffer.append(line).append("\n");
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
