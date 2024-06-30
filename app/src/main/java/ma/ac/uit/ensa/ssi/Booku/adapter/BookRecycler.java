package ma.ac.uit.ensa.ssi.Booku.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;

public class BookRecycler extends RecyclerView.Adapter<BookHolder> {
    private BookDAO bookaccess;
    private List<Book> books;

    public BookRecycler(BookDAO bookaccess) {
        this.bookaccess = bookaccess;
        this.books      = this.bookaccess.getAllBooks();
    }

    public void addBook(Book book) {
        books.add(0, book);
        this.notifyItemInserted(0);
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book, parent, false);

        BookHolder holder = new BookHolder(view);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        GridLayoutManager.LayoutParams gridParams = (GridLayoutManager.LayoutParams) layoutParams;
        gridParams.width  = ViewGroup.LayoutParams.MATCH_PARENT;
        gridParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        Book book = books.get(position);
        holder.text.setText(book.getName() + "\n" + book.getIsbn());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
