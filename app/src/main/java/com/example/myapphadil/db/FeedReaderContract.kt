package com.example.myapphadil.db


import android.provider.BaseColumns

object FeedReaderContract {

    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "workes"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_DESCRIPTION= "description"
    }
    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${FeedEntry.COLUMN_NAME_NAME } TEXT," +
                "${FeedEntry.COLUMN_NAME_DESCRIPTION } TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"
}