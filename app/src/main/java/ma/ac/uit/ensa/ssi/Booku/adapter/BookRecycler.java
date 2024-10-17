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

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.utils.FileUtils;
import ma.ac.uit.ensa.ssi.Booku.utils.OnItemSelectedListener;

public class BookRecycler extends RecyclerView.Adapter<BookHolder> {
    private final List<Book> books;

    private final Context ctx;

    private int selectedItem = RecyclerView.NO_POSITION;

    private final OnItemSelectedListener listener;
    private final OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookRecycler(Context ctx, OnItemSelectedListener listener, BookDAO bookaccess) {
        this.books      = bookaccess.getAllBooks();
        this.ctx        = ctx;
        this.listener   = listener;
        this.clickListener = null;
    }

    public BookRecycler(Context ctx, OnItemSelectedListener listener, List<Book> books,
                        OnItemClickListener clickListener) {
        this.books      = books;
        this.ctx        = ctx;
        this.listener   = listener;
        this.clickListener = clickListener;
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
        selectedItem = RecyclerView.NO_POSITION;
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

        Glide.with(holder.itemView.getContext())
                .load(book.getCoverResource() != null ? book.getCoverResource()
                        : holder.itemView.getContext().getFilesDir() + "/" + book.getIsbn() + ".jpg")
                .signature(new ObjectKey(System.currentTimeMillis()))
                .error(R.drawable.no_cover)
                .into(holder.cover);

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
            } else if (clickListener != null) {
                clickListener.onItemClick(book);
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
