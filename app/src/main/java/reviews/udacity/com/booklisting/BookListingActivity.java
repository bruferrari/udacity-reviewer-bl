package reviews.udacity.com.booklisting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.io.Serializable;
import java.util.List;
import reviews.udacity.com.booklisting.adapter.BookListingAdapter;
import reviews.udacity.com.booklisting.helper.BookListingHelper;
import reviews.udacity.com.booklisting.model.Book;

public class BookListingActivity extends AppCompatActivity {

    private static final String LIST_VIEW_INSTANCE_STATE = "listViewInstanceState";
    private BookListingHelper helper = new BookListingHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper.initializeUIElements();
        helper.bookSelectionHandler();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.getBooksByTitle(BookListingActivity.this);
            }
        });

        if (savedInstanceState != null) {
            helper.getBooksListView().setAdapter(new BookListingAdapter(this,
                    (List<Book>) savedInstanceState.getSerializable(LIST_VIEW_INSTANCE_STATE)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BookListingAdapter adapter = (BookListingAdapter) helper.getBooksListView().getAdapter();
        outState.putSerializable(LIST_VIEW_INSTANCE_STATE, (Serializable) adapter.getBooksList());
    }
}
