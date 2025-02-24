package com.example.happyplaces.ui.theme

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.service.autofill.UserData
import androidx.compose.runtime.currentRecomposeScope
import com.example.happyplaces.HappyPlacesModel

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context,DATABASE_NAME, null,DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME="UserDatabase.db"
        private const val DATABASE_VERSION=1
        const val TABLE_NAME="users"
        const val TABLE_ID="_id"
       // const val TABLE_IMG="img"
        const val TABLE_TITLE="title"
        const val TABLE_LOCATION="location"
        const val TABLE_DES="description"

    }

    // Method called when database is created

    override fun onCreate(db: SQLiteDatabase?)
    {
       val createTable =("CREATE TABLE $TABLE_NAME (" +
               "$TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
              // "$TABLE_IMG TEXT," +
                "$TABLE_TITLE TEXT,"+
                 "$TABLE_LOCATION TEXT,"  +
                 "$TABLE_DES TEXT)")
        db?.execSQL(createTable)
    }


    // Method called when database needs to upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
      db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to put val ues in database
    fun insertUser(title:String,location : String, description : String): Long
    {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
           // put(TABLE_IMG, image)
            put(TABLE_TITLE, title)
            put(TABLE_LOCATION, location)
            put(TABLE_DES, description)
        }
        return db.insert(TABLE_NAME, null,contentValues)
    }

    // Method to fetch all user data
    @SuppressLint("Range")
    fun getAllUsers() : ArrayList<HappyPlacesModel>{

        val userList = ArrayList<HappyPlacesModel>()
        val db = this.readableDatabase
        val cursor= db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if(cursor.moveToFirst())
        {
            do{
                // fetch each column's data
                val id= cursor.getString(cursor.getColumnIndex(TABLE_ID))
                //val image=cursor.getString(cursor.getColumnIndex(TABLE_IMG))
                val title= cursor.getString(cursor.getColumnIndex(TABLE_TITLE))
                val location=cursor.getString(cursor.getColumnIndex(TABLE_LOCATION))
                val description=cursor.getString(cursor.getColumnIndex(TABLE_DES))

                //create a object of userdata
                val user = HappyPlacesModel(title, location, description)
                userList.add(user)

            }while (cursor.moveToNext())

        }
        cursor.close()
        db.close()

        return userList

    }

    fun deleteUser(id : Int)
    {
        val db=this.writableDatabase
         db.delete(TABLE_NAME,"$TABLE_ID=?", arrayOf(id.toString()))
    }
}