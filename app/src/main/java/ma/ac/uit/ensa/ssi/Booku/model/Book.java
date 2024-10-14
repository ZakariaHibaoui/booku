package ma.ac.uit.ensa.ssi.Booku.model;

import java.io.Serializable;
import java.util.Optional;

// No record :/
public class Book implements Serializable {
    private Long id;
    private String name;
    private String isbn;
    private String cover_resource;
    private String desc;

    public Book(Long id, String name, String isbn) {
        this.id   = id;
        this.name = name;
        this.isbn = isbn;
        this.cover_resource = null;
        this.desc = null;
    }

    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCoverResource() { return cover_resource; }
    public void setCoverResource(String coverResource) { this.cover_resource = coverResource; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }
}
