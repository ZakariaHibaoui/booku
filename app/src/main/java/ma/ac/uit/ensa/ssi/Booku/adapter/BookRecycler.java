package ma.ac.uit.ensa.ssi.Booku.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.bookDAO;

public class BookRecycler extends RecyclerView.Adapter<BookHolder> {
    private bookDAO bookaccess;
    private List<Book> books;

    public BookRecycler(bookDAO bookaccess) {
        this.bookaccess = bookaccess;
        this.books      = this.bookaccess.getAllBooks();
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
