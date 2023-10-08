package com.example.pr_17_glazirinnikita_pr21_102;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText etTelephone, etModel, etSize, etPrice, etID;
    Button btnInsert, btnRead, btnClear, btnDelete, btnUpdate, btnSort;
    RadioGroup rgSort;

    String Telephone, Model, id, orderBy;
    double Size, Price;
    Cursor c = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        etTelephone = findViewById(R.id.etTelephone);
        etModel = findViewById(R.id.etModel);
        etSize = findViewById(R.id.etSize);
        etPrice = findViewById(R.id.etPrice);
        etID = findViewById(R.id.etID);

        btnInsert = findViewById(R.id.btnInsert);
        btnClear = findViewById(R.id.btnClear);
        btnRead = findViewById(R.id.btnRead);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnSort = findViewById(R.id.btnSort);

        rgSort = findViewById(R.id.rgSort);

        btnInsert.setOnClickListener(v -> {insertData();});
        btnRead.setOnClickListener(v -> {readData();});
        btnDelete.setOnClickListener(v -> {deleteData();});
        btnClear.setOnClickListener(v -> {clearData();});
        btnUpdate.setOnClickListener(v -> {updateData();});
        btnSort.setOnClickListener(v -> {sortData();});

    }
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void insertData() {
        ContentValues cv = new ContentValues();

        Telephone = etTelephone.getText().toString();
        Model = etModel.getText().toString();

        try {
            Size = Double.parseDouble(etSize.getText().toString());
            Price = Double.parseDouble(etPrice.getText().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("Telephone", Telephone);
        cv.put("Model", Model);
        cv.put("Size", Size);
        cv.put("Price", Price);

        // вставляем запись и получаем ее ID
        long rowID = db.insert("mytable", null, cv);
        Log.d("LOG_TAG", "row inserted, ID = " + rowID);
        // Выводим toast
        if (rowID != -1) {
            showToast("Вставка успешно выполнена!");
        }
    }


    public void readData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("LOG_TAG", "--- Rows in mytable: ---");
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if(c.moveToFirst()) {

            int idColIndex = c.getColumnIndex("id");
            int animalColIndex = c.getColumnIndex("Telephone");
            int nameColIndex = c.getColumnIndex("Model");
            int sizeColIndex = c.getColumnIndex("Size");
            int heightColIndex = c.getColumnIndex("Price");

            do {
                // получаем значения по номерам столбцов и пишем в лог
                Log.d("LOG_TAG",
                        "ID = "+ c.getInt(idColIndex) +
                                ", Telephone = "+ c.getString(animalColIndex) +
                                ", Model = "+ c.getString(nameColIndex) +
                                ", Size = "+ c.getDouble(sizeColIndex) +
                                ", Price = "+ c.getDouble(heightColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while(c.moveToNext());
        } else
            Log.d("LOG_TAG", "0 rows");
        c.close();
    }
    public void clearData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d("LOG_TAG", "--- Clear mytable: ---");
        // удаляем все записи
        int clearCount = db.delete("mytable", null, null);
        Log.d("LOG_TAG", "deleted rows count = " + clearCount);
        // Выводим toast-сообщение о успешной очистке
        if (clearCount > 0) {
            showToast("Очистка успешно выполнена!");
        }
    }

    @SuppressLint("RestrictedApi")
    public void updateData() {
        try {ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            id = etID.getText().toString();
            if (id.equalsIgnoreCase("")) {
            }
            Log.d(LOG_TAG, "--- Update mytable: ---");

            Telephone = etTelephone.getText().toString();
            Model = etModel.getText().toString();

            try {
                Size = Double.parseDouble(etSize.getText().toString());
                Price = Double.parseDouble(etPrice.getText().toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            cv.put("Telephone", Telephone);
            cv.put("Model", Model);
            cv.put("Size", Size);
            cv.put("Price", Price);

            // обновляем по id
            int updCount = db.update("mytable", cv, "id = ?",
                    new String[] { id });
            Log.d(LOG_TAG, "updated rows count = "+ updCount);

            c.close();
            // Выводим toast-сообщение о успешном обновлении
            if (updCount > 0) {
                showToast("Обновление успешно выполнено!");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Вывод стека вызовов ошибки в логи
        }
    }


    @SuppressLint("RestrictedApi")
    public void deleteData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        id = etID.getText().toString();

        if(id.equalsIgnoreCase("")) {}

        Log.d(LOG_TAG, "--- Delete from mytable: ---");
        // удаляемпо id
        int delCount = db.delete("mytable", "id = "+ id, null);
        Log.d(LOG_TAG, "deleted rows count = "+ delCount);
        // Выводим toast-сообщение об успешном удалении
        if (delCount > 0) {
            showToast("Удаление успешно выполнено!");
        }
    }

    @SuppressLint("RestrictedApi")
    public void sortData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(rgSort.getCheckedRadioButtonId() == R.id.rbTelephone){
            Log.d(LOG_TAG, "--- Сортировка по названию телефона ---");
            orderBy = "Telephone";
        }
        if(rgSort.getCheckedRadioButtonId() == R.id.rbModel){
            Log.d(LOG_TAG, "--- Сортировка по модели ---");
            orderBy = "Model";
        }
        if(rgSort.getCheckedRadioButtonId() == R.id.rbSize){
            Log.d(LOG_TAG, "--- Сортировка по размеру ---");
            orderBy = "Size";
        }
        if(rgSort.getCheckedRadioButtonId() == R.id.rbPrice){
            Log.d(LOG_TAG, "--- Сортировка по цене ---");
            orderBy = "Price";
        }

        c = db.query("mytable", null, null, null, null, null, orderBy);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn :c.getColumnNames()) {
                        str = str.concat(cn + " = "
                                + c.getString(c.getColumnIndexOrThrow(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (c.moveToNext());
            }
            c.close();
        } else
            Log.d(LOG_TAG, "Cursor is null");
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}


class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE mytable ("
                + "id INTEGER PRIMARY KEY, "
                + "Telephone TEXT, "
                + "Model TEXT, "
                + "Size REAL, "
                + "Price REAL" + ");");}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}