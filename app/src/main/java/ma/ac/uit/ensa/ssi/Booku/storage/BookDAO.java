package ma.ac.uit.ensa.ssi.Booku.storage;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ma.ac.uit.ensa.ssi.Booku.model.Book;

/**
 * BookDAO est une classe responsable de la gestion des opérations CRUD
 * (Create, Read, Update, Delete) pour les objets Book dans la base de données SQLite.
 * Elle permet d'insérer, supprimer, mettre à jour et récupérer des livres stockés dans une table SQLite.
 */

public class BookDAO {

     // Instance de la base de données utilisée pour effectuer les opérations SQLite
    public Database sdb;

    // Constantes représentant les noms de la table et des colonnes dans la base de données SQLite
    static final String TABLE_NAME  = "book";
    static final String COLUMN_ID   = "id";
    static final String COLUMN_ISBN = "isbn";
    static final String COLUMN_NAME = "name";

     /**
     * Constructeur de la classe BookDAO.
     * Initialise une instance de la base de données SQLite via la classe Database.
     *
     * @param ctx Le contexte de l'application nécessaire pour la connexion à SQLite.
     */

    public BookDAO(Context ctx) {
        this.sdb = new Database(ctx);
    }

     /**
     * Ferme la connexion à la base de données.
     * Cette méthode doit être appelée une fois que l'objet DAO n'est plus utilisé.
     */

    public void close() { this.sdb.close(); }

    /**
     * Ajoute un nouveau livre dans la base de données.
     * Si l'ISBN du livre existe déjà, une exception de type DatabaseError est lancée.
     *
     * @param book Un objet Book à insérer dans la base de données.
     * @return L'ID du livre nouvellement ajouté.
     * @throws DatabaseError Si une contrainte est violée (par exemple, l'ISBN existe déjà).
     */

    public long addBook(Book book) throws DatabaseError {
        SQLiteDatabase db = sdb.get_write();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_NAME, book.getName());
        try {
            long id = db.insertOrThrow(TABLE_NAME, null, values);
            db.close();
            return id;
        } catch (SQLiteConstraintException e) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.Constraint, "ISBN already exists");
        } catch (SQLException e) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.Constraint, e.getMessage());
        }
    }

    /**
     * Supprime un livre de la base de données par son ID.
     * Si aucun livre ne correspond à l'ID fourni, une exception de type DatabaseError est lancée.
     *
     * @param id L'ID du livre à supprimer.
     * @throws DatabaseError Si aucun livre n'est trouvé avec l'ID spécifié.
     */

    public void deleteBook(long id) throws DatabaseError {
        SQLiteDatabase db = sdb.getWritableDatabase();
        int affected      = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        if (affected <= 0) {
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "No book to delete with given id");
        }
    }

     /**
     * Met à jour les informations d'un livre dans la base de données.
     * Si aucun livre n'est trouvé avec l'ID fourni, une exception de type DatabaseError est lancée.
     *
     * @param book L'objet Book contenant les informations à mettre à jour.
     * @throws DatabaseError Si aucun livre n'est trouvé avec l'ID spécifié.
     */

    public void updateBook(Book book) throws DatabaseError {
        SQLiteDatabase db    = sdb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ISBN, book.getIsbn());
        values.put(COLUMN_NAME, book.getName());

        int affected = db.update(
                TABLE_NAME,
                values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(book.getId())}
        );
        db.close();

        if (affected <= 0) {
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "No book to update with given id");
        }
    }

     /**
     * Récupère un livre de la base de données par son ID.
     * Si aucun livre n'est trouvé, une exception de type DatabaseError est lancée.
     *
     * @param id L'ID du livre à récupérer.
     * @return Un objet Book contenant les informations du livre trouvé.
     * @throws DatabaseError Si aucun livre n'est trouvé avec l'ID spécifié.
     */

    public Book getBook(long id) throws DatabaseError {
        SQLiteDatabase db = sdb.getReadableDatabase();
        Cursor cursor     = db.query(
                TABLE_NAME,
                new String[] {COLUMN_ISBN, COLUMN_NAME},
                COLUMN_ID + "=?",
                new String[] {String.valueOf(id)},
                null, null, null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            db.close();
            throw new DatabaseError(DatabaseError.ExceptionType.NoMatch, "Book with given id not found");
        }

        Book b = new Book(
                id,
                cursor.getString(1),
                cursor.getString(0)
        );
        cursor.close();
        db.close();
        return b;
    }

    /**
     * Récupère tous les livres de la base de données, triés par ordre décroissant d'ID.
     *
     * @return Une liste d'objets Book représentant tous les livres présents dans la base de données.
     */

    @SuppressLint("Range")
    public List<Book> getAllBooks() {
        SQLiteDatabase db   = sdb.getReadableDatabase();
        List<Book> bookList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC", null);

        if (cursor.moveToFirst()) {
            do {
                long id     = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String isbn = cursor.getString(cursor.getColumnIndex(COLUMN_ISBN));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

                Book book = new Book(id, name, isbn);
                bookList.add(book);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return bookList;
    }
}
