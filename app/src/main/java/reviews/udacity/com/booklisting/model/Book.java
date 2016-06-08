package reviews.udacity.com.booklisting.model;

import java.util.List;

/**
 * Created by bruno on 6/8/16.
 */
public class Book {

    private String title;
    private List<Author> author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthor() {
        return author;
    }

    public void setAuthor(List<Author> author) {
        this.author = author;
    }
}
