package ma.ac.uit.ensa.ssi.Booku;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import ma.ac.uit.ensa.ssi.Booku.BookAPI.API;
import ma.ac.uit.ensa.ssi.Booku.BookAPI.BookResponse;
import ma.ac.uit.ensa.ssi.Booku.BookAPI.Client;
import ma.ac.uit.ensa.ssi.Booku.adapter.BookRecycler;
import ma.ac.uit.ensa.ssi.Booku.component.GridSpacingItemDecoration;
import ma.ac.uit.ensa.ssi.Booku.model.Book;
import ma.ac.uit.ensa.ssi.Booku.storage.BookDAO;
import ma.ac.uit.ensa.ssi.Booku.storage.DatabaseError;
import ma.ac.uit.ensa.ssi.Booku.ui.SearchResultActivity;
import ma.ac.uit.ensa.ssi.Booku.ui.SettingsActivity;
import ma.ac.uit.ensa.ssi.Booku.utils.FileUtils;
import ma.ac.uit.ensa.ssi.Booku.utils.OnItemSelectedListener;
import ma.ac.uit.ensa.ssi.Booku.utils.SettingsUtil;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    private BookDAO book_access;

    RecyclerView books_view;
    BookRecycler adapter;

    ActivityResultLauncher<Intent> add_book_activity_ret;
    ActivityResultLauncher<Intent> edit_book_activity_ret;

    enum MenuType {
        Normal,
        Action
    }

    private MenuType menuType = MenuType.Normal;

    private API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.setup_defaults(this);
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

        book_access = new BookDAO(this.getBaseContext());

        books_view = findViewById(R.id.book_view);
        books_view.setLayoutManager(new GridLayoutManager(this, 2));
        int spacing = getResources().getDimensionPixelSize(R.dimen.book_grid_spacing);
        books_view.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        new Thread(() -> {
            api = Client.getClient();

            adapter = new BookRecycler(getApplicationContext(), this, book_access);
            runOnUiThread(() -> {
                books_view.setAdapter(adapter);
                dialog.dismiss();
            });
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

        edit_book_activity_ret = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        Book book = (Book)data.getSerializableExtra("editBook");
                        adapter.editSelectedBook(book);
                    }
                    onRelease();
                    adapter.unselectAll();
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (menuType == MenuType.Normal) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setCustomView(null);
            inflater.inflate(R.menu.main_activity_top_bar, menu);
        } else {
            getSupportActionBar().setCustomView(R.layout.book_action);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled (false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            ImageView cancel = findViewById(R.id.action_cancel);
            cancel.setOnClickListener(v -> {
                onRelease();
                adapter.unselectAll();
            });

            ImageView edit = findViewById(R.id.action_edit);
            edit.setOnClickListener(v -> {
                Intent editBookIntent = new Intent(this, ma.ac.uit.ensa.ssi.Booku.ui.EditBookActivity.class);
                editBookIntent.putExtra("book", adapter.getSelectedBook());
                edit_book_activity_ret.launch(editBookIntent);
            });

            FloatingActionButton delete = findViewById(R.id.action_delete);
            delete.setOnClickListener(v -> {
                Book book = adapter.getSelectedBook();
                try {
                    book_access.deleteBook(book.getId());
                } catch (DatabaseError e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(
                        this,
                        String.format(getString(R.string.book_deleted), book.getIsbn()),
                        Toast.LENGTH_SHORT
                ).show();
                adapter.deleteSelectedBook();
                onRelease();

                FileUtils.delete(this, book.getIsbn() + ".jpg");
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_add) {
            Intent addBookIntent = new Intent(this, ma.ac.uit.ensa.ssi.Booku.ui.AddBookActivity.class);
            add_book_activity_ret.launch(addBookIntent);
        } else if (i == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (i == R.id.action_search) {
            search();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected() {
        menuType = MenuType.Action;
        invalidateOptionsMenu();

        RelativeLayout v = findViewById(R.id.bottom_action);
        v.setVisibility(View.VISIBLE);
        books_view.setPadding(0, 0,0, v.getHeight());
    }

    public void onRelease() {
        menuType = MenuType.Normal;
        invalidateOptionsMenu();

        RelativeLayout v = findViewById(R.id.bottom_action);
        v.setVisibility(View.INVISIBLE);
        books_view.setPadding(0, 0,0, 0);
    }

    private void search() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.search_online))
                .setView(input)
                .setPositiveButton(getString(R.string.action_search), (dialogInterface, i) -> {
                    if (input.getText().toString().isEmpty()) {
                        Toast.makeText(
                                this, getString(R.string.search_empty), Toast.LENGTH_LONG
                        ).show();
                        return;
                    }
                    dialogInterface.dismiss();
                    AlertDialog pending = new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.search_wait))
                            .setCancelable(false)
                            .create();
                    pending.show();
                    new Thread(() -> {
                        BookResponse resp;
                        try {
                            resp = api.getBooks(input.getText().toString())
                                    .execute()
                                    .body();
                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                Toast.makeText(
                                        this, getString(R.string.search_error), Toast.LENGTH_LONG
                                ).show();
                                pending.dismiss();
                            });
                            return;
                        }
                        runOnUiThread(() -> {
                            Intent intent = new Intent(this, SearchResultActivity.class);
                            intent.putExtra("resp", resp);
                            add_book_activity_ret.launch(intent);
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