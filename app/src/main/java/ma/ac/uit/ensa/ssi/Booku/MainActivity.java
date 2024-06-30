package ma.ac.uit.ensa.ssi.Booku;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;

public class MainActivity extends AppCompatActivity {
    private BookDAO book_access;

    RecyclerView books_view;
    BookRecycler adapter;

    ActivityResultLauncher<Intent> add_book_activity_ret;

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
            book_access = new BookDAO(this.getBaseContext());

            books_view = findViewById(R.id.book_view);
            books_view.setLayoutManager(new GridLayoutManager(this, 2));

            int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
            books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
            adapter = new BookRecycler(getApplicationContext(), book_access);
            books_view.setAdapter(adapter);

            runOnUiThread(() -> dialog.dismiss());
        }).start();

        add_book_activity_ret = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        Book book = (Book)data.getSerializableExtra("addBook");
                        adapter.addBook(book);
                        books_view.smoothScrollToPosition(0);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_top_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_add) {
            Intent addBookIntent = new Intent(this, ma.ac.uit.ensa.ssi.Booku.ui.AddBookActivity.class);
            add_book_activity_ret.launch(addBookIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}