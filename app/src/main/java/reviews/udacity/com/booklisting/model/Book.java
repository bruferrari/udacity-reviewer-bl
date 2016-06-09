package reviews.udacity.com.booklisting.model;

import java.util.List;

/**
 * Created by bruno on 6/8/16.
 */
public class Book {

    private String title;
    private List<Author> authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + authors +
                '}';
    }
}
