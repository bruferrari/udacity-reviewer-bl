package reviews.udacity.com.booklisting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import reviews.udacity.com.booklisting.R;
import reviews.udacity.com.booklisting.model.Author;
import reviews.udacity.com.booklisting.model.Book;

/**
 * Created by bruno on 6/9/16.
 */
public class BookListingAdapter extends ArrayAdapter<Book> {

    private List<Book> books;
    private Context context;
    private TextView bookTitle;
    private TextView bookAuthors;

    public BookListingAdapter(Context context, List<Book> books) {
        super(context, -1, books);
        this.books = books;
        this.context = context;
    }

    public List<Book> getBooksList() {
        return books;
    }

    @Override
    public Book getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.book_list_row, parent, false);
        } else
            view = convertView;

        bookTitle = (TextView) view.findViewById(R.id.list_row_book_title);
        bookAuthors = (TextView) view.findViewById(R.id.list_row_authors);
        bookTitle.setText(books.get(position).getTitle());

        StringBuilder stringBuilder = null;

        if (existAuthorsInBooksList(position))
            stringBuilder = parseAuthorNames(position);

        bookAuthors.setText(stringBuilder);

        return view;
    }

    private boolean existAuthorsInBooksList(int position) {
        return !books.get(position).getAuthors().isEmpty();
    }

    @NonNull
    private StringBuilder parseAuthorNames(int position) {
        List<Author> authorsList = books.get(position).getAuthors();

        StringBuilder stringBuilder = new StringBuilder();
        for (Author author : authorsList) {
            stringBuilder.append(author.getFullName()).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(", "));
        return stringBuilder;
    }
}
