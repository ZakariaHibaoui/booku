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

import ma.ac.uit.ensa.ssi.Booku.adapter.BookRecycler;
import ma.ac.uit.ensa.ssi.Booku.component.GridSpacingItemDecoration;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.Database;
import ma.ac.uit.ensa.ssi.Booku.storage.bookDAO;

public class MainActivity extends AppCompatActivity {
    private Database db;
    private bookDAO book_access;


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

        Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.splash_screen);
        dialog.setCancelable(true);
        dialog.show();

        new Thread(() -> {
            db          = new Database(this.getBaseContext());
            book_access = new bookDAO(db);

            RecyclerView books_view = findViewById(R.id.book_view);
            books_view.setLayoutManager(new GridLayoutManager(this, 2));

            int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
            books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
            BookRecycler adapter = new BookRecycler(book_access);
            books_view.setAdapter(adapter);

            runOnUiThread(() -> dialog.dismiss());
        }).start();
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