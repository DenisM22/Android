package com.example.movies;

import static com.example.movies.Scheme.*;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    public DatabaseHandler(Context context) {
        super(context, Scheme.DATABASE_NAME, null, Scheme.DATABASE_VERSION);
    }

    //Создание таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT UNIQUE NOT NULL, " +
                PASSWORD + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_USERS_TABLE);


        String CREATE_MOVIES_TABLE = "CREATE TABLE " + TABLE_MOVIES + " (" +
                ID_OF_USER + " INTEGER, " +
                MOVIE_ID + " INTEGER, " +
                MOVIE_POSTER_PATH + " TEXT, " +
                "PRIMARY KEY (" + ID_OF_USER + ", " + MOVIE_ID + "), " +
                "FOREIGN KEY (" + ID_OF_USER + ") REFERENCES users(id) ON DELETE CASCADE)";

        db.execSQL(CREATE_MOVIES_TABLE);

        Log.d("Database", "База данных создана");
    }

    //Обновление таблицы
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USERNAME, username);
            values.put(PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);
            db.close();

            Log.d("Database", "Пользователь добавлен");
            return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { ID };
        String selection = USERNAME + " = ? AND " + PASSWORD + " = ?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean userExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }

    public boolean checkUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { ID };
        String selection = USERNAME + " = ?";
        String[] selectionArgs = { username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean userExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return userExists;
    }

    public User getUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ID, USERNAME, PASSWORD};
        String selection = USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
            String user = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
            cursor.close();
            db.close();
            return new User(id, user, password);
        } else {
            if (cursor != null) cursor.close();
            db.close();
            return null;
        }
    }

    public void addMovie(long userId, Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_OF_USER, userId);
        values.put(MOVIE_ID, movie.id);
        values.put(MOVIE_POSTER_PATH, movie.posterPath);

        db.insert(TABLE_MOVIES, null, values);
        db.close();
        Log.d("Database", "Фильм добавлен");
    }

    public void deleteMovie(long userId, Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, ID_OF_USER + " = ? AND " + MOVIE_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(movie.id)});
        db.close();
        Log.d("Database", "Фильм удален");
    }

    public boolean checkMovie(long userId, long movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = { ID_OF_USER };
        String selection = ID_OF_USER + " = ? AND " + MOVIE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(movie)};
        Cursor cursor = db.query(TABLE_MOVIES, columns, selection, selectionArgs, null, null, null);
        boolean movieExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return movieExists;
    }

    public boolean isTableMovieEmpty(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MOVIES +
                " WHERE " + ID_OF_USER + " = " + userId, null);
        int rowCount = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            rowCount = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        Log.d("Database", "Таблица проверена");
        return rowCount == 0;
    }

    public List<Movie> getAllMovies(Long userId) {
        List<Movie> moviesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MOVIES +
                " WHERE " + ID_OF_USER + " = " + userId, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") long movieId = cursor.getLong(cursor.getColumnIndex(MOVIE_ID));
                @SuppressLint("Range") String posterPath = cursor.getString(cursor.getColumnIndex(MOVIE_POSTER_PATH));
                Movie movie = new Movie(movieId, posterPath); // Создаем объект Movie с полученными данными
                Log.d("Database", "ID пользователя: " + AppConfig.user.id + ", ID фильма: " + movieId);
                moviesList.add(movie); // Добавляем фильм в список
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return moviesList;
    }


}
