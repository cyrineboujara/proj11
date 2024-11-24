package com.example.myapphadil
import android.content.ContentValues
import android.provider.BaseColumns
import com.example.myapphadil.db.FeedReaderContract



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.Alignment
import com.example.myapphadil.db.FeedReaderDbHelper




@Composable
fun WorkersEmployerScreen(dbHelper: FeedReaderDbHelper, onNavigateBack: () -> Unit) {
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ButtonComposable(text = "Insert", onClick = { insertDB(dbHelper) })
            ButtonComposable(text = "Read") {
                readDB(dbHelper)
            }
            ButtonComposable(text = "Delete") {
                deleteDB(dbHelper)
            }
            ButtonComposable(text = "Update") {
                updateDB(dbHelper)
            }

        }
    }
}



fun insertDB(dbHelper: FeedReaderDbHelper) {
    // Gets the data repository in write mode
    val db = dbHelper.writableDatabase

// Create a new map of values, where column names are the keys
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, "My Name")
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, "description")
    }

// Insert the new row, returning the primary key value of the new row
    val newRowId = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)

    println("Row inserted with ID: $newRowId")
}

fun readDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.readableDatabase

// Define a projection that specifies which columns from the database
// you will actually use after this query.
    val projection = arrayOf(
        BaseColumns._ID,
        FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
        FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION
    )

// Filter results WHERE "title" = 'My Name'
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_NAME} = ?"
    val selectionArgs = arrayOf("My Name")

// How you want the results sorted in the resulting Cursor
    val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION} DESC"

    val cursor = db.query(
        FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
        null,             // The array of columns to return (pass null to get all)
        null,              // The columns for the WHERE clause
        null,          // The values for the WHERE clause
        null,                   // don't group the rows
        null,                   // don't filter by row groups
        sortOrder               // The sort order
    )
    val itemIds = mutableListOf<Long>()
    val itemNames = mutableListOf<String>()
    with(cursor) {
        while (moveToNext()) {
            val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val itemName = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
            itemIds.add(itemId)
            itemNames.add(itemName)
        }
    }
    cursor.close()
    println("Item IDs: $itemIds")
    println("Item Names: $itemNames")
}

fun deleteDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.writableDatabase
// Define 'where' part of query.
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_NAME} LIKE ?"
// Specify arguments in placeholder order.
    val selectionArgs = arrayOf("My Name")
// Issue SQL statement.
    val deletedRows = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)

    println("Deleted rows: $deletedRows")
}

fun updateDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.writableDatabase

// New value for one column
    val name = "MyNewNames"
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name)
    }

// Which row to update, based on the name
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_NAME} LIKE ?"
    val selectionArgs = arrayOf("My Names")
    val count = db.update(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        values,
        selection,
        selectionArgs
    )

    println("Updated rows: $count")

}
@Composable
fun ButtonComposable(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}



