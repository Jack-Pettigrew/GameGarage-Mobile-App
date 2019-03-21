package uk.ac.lincoln.a15593452students.gamegarage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Favourites Database Class (Implemented alongside SQLite code)
public class FavouritesDBHandler extends SQLiteOpenHelper {

    // SQLite DB prerequisites
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favourites.db";
    public static final String TABLE_GAMES = "favourites";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GAME_NAME = "game_name";
    public static final String COLUMN_GAME_BANNER = "game_banner";



    // Constructor: Initialisation of DB
    public FavouritesDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    // Called when creating DB for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create initial DB Table (Feed SQL code through Strings!
        String query = "CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_ID + " TEXT, " +
                COLUMN_GAME_NAME + " TEXT, " +
                COLUMN_GAME_BANNER + " TEXT " +
                ");";

        // Execute the Query (create table with created table
        db.execSQL(query);
    }

    // Called when upgrading DB version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }

    // Adds game to favourites database
    public void addGame(Game game) {

        // Add required information to Table
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, game.getId());
        values.put(COLUMN_GAME_NAME, game.getName());
        values.put(COLUMN_GAME_BANNER, game.getImageScreen());

        // Write values to DB
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_GAMES, null, values);
        db.close();

    }

    // Removes game from favourites database
    public void removeGame(String gameName) {

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GAMES + " WHERE " + COLUMN_GAME_NAME + "=\"" + gameName + "\"");
        db.close();

    }

    // Debug SQL Database print Method
    public String databasePrint() {

        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GAMES + " WHERE 1";

        // Cursor(pointer) to current DB entry (query)
        Cursor cursor =  db.rawQuery(query, null);
        cursor.moveToFirst();

        // Loop through query table
        while (!cursor.isAfterLast()) {

            // If there is a name...
            if (cursor.getString(cursor.getColumnIndex("game_name")) != null) {

                // Add it to the String
                dbString += cursor.getString(cursor.getColumnIndex("game_name"));
                dbString += " ";
            }

            // Iterate to next entry
            cursor.moveToNext();
        }

        db.close();

        // Return Games as String
        return dbString;
    }

    // Returns database in the form of a List
    public ArrayList<Game> databaseToList(){

        // Array to return
        ArrayList<Game> arrayList = new ArrayList<>();

        // Search all DB entries
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GAMES + " WHERE 1";

        // Cursor to Iterate
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        // Loop through query table
        while (!cursor.isAfterLast()) {

            // If there is a name...
            if (cursor.getString(cursor.getColumnIndex("game_name")) != null) {

                // Extract DB data to Object
                arrayList.add(new Game(cursor.getString(cursor.getColumnIndex("game_name")),
                        cursor.getString(cursor.getColumnIndex("game_banner"))));

            }

            // Iterate to next entry
            cursor.moveToNext();
        }

        db.close();

        return arrayList;
    }

}
