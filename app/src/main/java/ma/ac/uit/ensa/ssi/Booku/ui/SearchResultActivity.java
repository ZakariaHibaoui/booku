package ma.ac.uit.ensa.ssi.Booku.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ma.ac.uit.ensa.ssi.Booku.BookAPI.BookResponse;
import ma.ac.uit.ensa.ssi.Booku.R;
import ma.ac.uit.ensa.ssi.Booku.adapter.BookRecycler;
import ma.ac.uit.ensa.ssi.Booku.component.GridSpacingItemDecoration;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.utils.FileUtils;
import ma.ac.uit.ensa.ssi.Booku.utils.ImagePicker;
import ma.ac.uit.ensa.ssi.Booku.utils.Isbn;
import ma.ac.uit.ensa.ssi.Booku.utils.OnItemSelectedListener;

public class SearchResultActivity extends AppCompatActivity implements OnItemSelectedListener,
        BookRecycler.OnItemClickListener {
    RecyclerView books_view;
    BookRecycler adapter;

    BookDAO book_access;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search_result));
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        book_access = new BookDAO(this.getBaseContext());
        BookResponse resp = (BookResponse) getIntent()
                .getSerializableExtra("resp");

        books_view = findViewById(R.id.book_view);
        books_view.setLayoutManager(new GridLayoutManager(this, 2));

        long count = 0;
        ArrayList<Book> books = new ArrayList<>();
        for (BookResponse.BookItem item : resp.items) {
            String isbn = null;
            if (item.volumeInfo == null || item.volumeInfo.identifiers == null) {
                continue;
            }
            for (BookResponse.Identifier identifier: item.volumeInfo.identifiers) {
                if (identifier.type.equals("ISBN_13") && identifier.id.length() == 13) {
                    isbn = Isbn.formatISBN13(identifier.id);
                    break;
                } else if (identifier.type.equals("ISBN_10") && identifier.id.length() == 10) {
                    isbn = Isbn.formatISBN10(identifier.id);
                    break;
                }
            }
            if (isbn == null) {
                continue;
            }
            Book book = new Book(count, item.volumeInfo.title, isbn);
            if (item.volumeInfo.imageLinks != null && item.volumeInfo.imageLinks.thumbnail != null) {
                book.setCoverResource(item.volumeInfo.imageLinks.thumbnail.replace("http://", "https://"));
            }
            if (item.volumeInfo.description != null) {
                book.setDesc(item.volumeInfo.description);
            }
            books.add(book);
        }

        int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
        books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        adapter = new BookRecycler(getApplicationContext(), this, books, this);
        books_view.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.desc))
                .setMessage(adapter.getSelectedBook().getDesc() != null
                        ? adapter.getSelectedBook().getDesc()
                        : getString(R.string.no_desc))
                .create();
        dialog.show();
        dialog.setOnDismissListener(v -> {
            adapter.unselectAll();
        });
    }

    public void onRelease() {
    }

    public void onItemClick(Book book) {
        adapter.unselectAll();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.search_add_confirm, book.getName()))
                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    dialogInterface.dismiss();

                    AlertDialog pending = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.adding_wait))
                            .setCancelable(false)
                            .create();
                    pending.show();
                    new Thread(() -> {
                        if (book.getCoverResource() != null) {
                            try {
                                FileUtils.write(
                                        this,
                                        book.getIsbn() + ".jpg",
                                        ImagePicker.downloadImage(book.getCoverResource(), this)
                                );
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    pending.dismiss();
                                    Toast.makeText(
                                            this,
                                            e.toString(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                                return;
                            }
                        }
                        runOnUiThread(() -> {
                            AddBookActivity.addBook(book_access, this, book);
                            pending.dismiss();
                        });
                    }).start();
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .create();
        dialog.show();
    }
}
