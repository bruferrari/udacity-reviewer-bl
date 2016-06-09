package reviews.udacity.com.booklisting.model;

/**
 * Created by bruno on 6/8/16.
 */
public class Author {

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Author{" +
                "fullName='" + fullName + '\'' +
                '}';
    }
}
