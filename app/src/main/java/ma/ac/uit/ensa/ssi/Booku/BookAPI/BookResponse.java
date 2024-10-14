package ma.ac.uit.ensa.ssi.Booku.BookAPI;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookResponse implements Serializable {
    @SerializedName("items")
    public List<BookItem> items;

    public static class BookItem implements Serializable {
        @SerializedName("volumeInfo")
        public VolumeInfo volumeInfo;
    }

    public static class VolumeInfo implements Serializable {
        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("imageLinks")
        public ImageLinks imageLinks;
        @SerializedName("industryIdentifiers")
        public List<Identifier> identifiers;
    }

    public static class ImageLinks implements Serializable {
        @SerializedName("thumbnail")
        public String thumbnail;
    }

    public static class Identifier implements Serializable {
        @SerializedName("type")
        public String type;
        @SerializedName("identifier")
        public String id;
    }
}