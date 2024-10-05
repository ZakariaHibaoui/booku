package ma.ac.uit.ensa.ssi.Booku.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
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
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.utils.FileUtils;
import ma.ac.uit.ensa.ssi.Booku.utils.OnItemSelectedListener;

public class BookRecycler extends RecyclerView.Adapter<BookHolder> {
    private final BookDAO bookaccess;
    private final List<Book> books;

    private final Context ctx;

    private int selectedItem = RecyclerView.NO_POSITION;

    private final OnItemSelectedListener listener;

    public BookRecycler(Context ctx, OnItemSelectedListener listener, BookDAO bookaccess) {
        this.bookaccess = bookaccess;
        this.books      = this.bookaccess.getAllBooks();
        this.ctx        = ctx;
        this.listener   = listener;
    }

    public void addBook(Book book) {
        books.add(0, book);
        this.notifyItemInserted(0);
    }

    public Book getSelectedBook() {
        return books.get(selectedItem);
    }

    public void editSelectedBook(Book book) {
        books.set(selectedItem, book);
        notifyItemChanged(selectedItem);
    }

    public void deleteSelectedBook() {
        books.remove(selectedItem);
        notifyItemRemoved(selectedItem);
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
    public void onBindViewHolder(@NonNull BookHolder holder, int view) {
        Book book = books.get(holder.getAdapterPosition());
        holder.text.setText(book.getName() + "\n" + book.getIsbn());
        FileUtils.setImageFromPath(holder.itemView.getContext(), holder.cover, book.getIsbn() + ".jpg", R.drawable.no_cover);

        holder.itemView.setOnLongClickListener(v -> {
            if (selectedItem != holder.getAdapterPosition()) {
                int previousSelected = selectedItem;
                selectedItem = holder.getAdapterPosition();
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedItem);
            }
            listener.onItemSelected();
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (selectedItem != RecyclerView.NO_POSITION) {
                if (selectedItem != holder.getAdapterPosition()) {
                    int previousSelected = selectedItem;
                    selectedItem = holder.getAdapterPosition();
                    notifyItemChanged(previousSelected);
                    notifyItemChanged(selectedItem);
                } else {
                    unselectAll();
                    listener.onRelease();
                }
            }
        });

        int color;
        if (selectedItem == holder.getAdapterPosition()) {
            color = ContextCompat.getColor(ctx, R.color.selected);
        } else {
            TypedValue typedValue = new TypedValue();
            holder.itemView.getContext()
                    .getTheme()
                    .resolveAttribute(android.R.attr.colorBackground, typedValue, true);
            color = typedValue.data;
        }
        holder.itemView.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void unselectAll() {
        if (selectedItem != RecyclerView.NO_POSITION) {
            int old      = selectedItem;
            selectedItem = RecyclerView.NO_POSITION;
            notifyItemChanged(old);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
