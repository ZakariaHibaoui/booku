package ma.ac.uit.ensa.ssi.Booku;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.adapter.BookRecycler;
import ma.ac.uit.ensa.ssi.Booku.composent.GridSpacingItemDecoration;
import ma.ac.uit.ensa.ssi.Booku.model.Book;

public class MainActivity extends AppCompatActivity {
    private RecyclerView books_view;
    private List<Book> books = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_screen);
        dialog.setCancelable(true);
        dialog.show();

        new Thread(() -> {
            // TODO: Await main page to fully load instead of this
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                // Do nothing...
            }
            runOnUiThread(() -> dialog.dismiss());
        }).start();

        //books.add(new Book(1, "1", "1"));
        //books.add(new Book(2, "2", "2"));
        //books.add(new Book(3, "3", "3"));

        books_view = findViewById(R.id.book_view);
        books_view.setLayoutManager(new GridLayoutManager(this, 2));

        int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
        books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        BookRecycler adapter = new BookRecycler(books);
        books_view.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_top_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // TODO: Add actions
        return super.onOptionsItemSelected(item);
    }
}