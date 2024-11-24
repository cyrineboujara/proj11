package com.example.myapphadil

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.provider.BaseColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapphadil.db.FeedReaderContract
import com.example.myapphadil.db.FeedReaderDbHelper

data class Employee(val id: Long, val name: String, val description: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = FeedReaderDbHelper(this)

        setContent {
            MaterialTheme {
                WorkersEmployerScreen(dbHelper)
            }
        }
    }
}

@Composable
fun WorkersEmployerScreen(dbHelper: FeedReaderDbHelper) {
    var employees by remember { mutableStateOf(readEmployeesFromDB(dbHelper)) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf<Employee?>(null) } // Pour les mises à jour

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Formulaire pour ajouter ou mettre à jour un employé
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'employé") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp)) // Espacement entre les champs
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacement entre le formulaire et les boutons

                // Boutons pour ajouter ou mettre à jour
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (selectedEmployee == null) {
                                // Ajouter un nouvel employé
                                insertEmployeeDB(dbHelper, name, description)
                            } else {
                                // Mettre à jour l'employé sélectionné
                                updateEmployeeDB(dbHelper, selectedEmployee!!.id, name, description)
                                selectedEmployee = null // Réinitialise la sélection
                            }
                            employees = readEmployeesFromDB(dbHelper)
                            name = ""
                            description = ""
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (selectedEmployee == null) "Ajouter" else "Mettre à jour")
                    }

                    if (selectedEmployee != null) {
                        Button(
                            onClick = {
                                // Annuler la mise à jour
                                selectedEmployee = null
                                name = ""
                                description = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Annuler")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacement avant la liste des employés

                // Liste des employés
                LazyColumn(modifier = Modifier.fillMaxHeight()) {
                    items(employees) { employee ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = employee.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = employee.description,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = {
                                    // Remplit les champs pour mettre à jour cet employé
                                    selectedEmployee = employee
                                    name = employee.name
                                    description = employee.description
                                }) {
                                    Text("Modifier")
                                }
                                Button(onClick = {
                                    deleteEmployeeDB(dbHelper, employee.id)
                                    employees = readEmployeesFromDB(dbHelper)
                                }) {
                                    Text("Supprimer")
                                }
                            }
                        }
                    }
                }

                // Bouton pour supprimer tous les employés
                Button(
                    onClick = {
                        deleteAllEmployeesDB(dbHelper)
                        employees = readEmployeesFromDB(dbHelper) // Rafraîchir la liste après suppression
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Supprimer tous les employés")
                }
            }
        }
    )
}

// Fonction pour insérer un employé dans la base de données
fun insertEmployeeDB(dbHelper: FeedReaderDbHelper, name: String, description: String) {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name)
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description)
    }
    db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
}

// Fonction pour lire tous les employés de la base de données
fun readEmployeesFromDB(dbHelper: FeedReaderDbHelper): List<Employee> {
    val db = dbHelper.readableDatabase
    val cursor = db.query(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        null, null, null, null, null, null
    )
    val employees = mutableListOf<Employee>()
    with(cursor) {
        while (moveToNext()) {
            val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
            val name = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME))
            val description = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION))
            employees.add(Employee(id, name, description))
        }
        close()
    }
    return employees
}

// Fonction pour mettre à jour un employé dans la base de données
fun updateEmployeeDB(dbHelper: FeedReaderDbHelper, id: Long, name: String, description: String) {
    val db = dbHelper.writableDatabase
    val values = ContentValues().apply {
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name)
        put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, description)
    }
    db.update(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        values,
        "${BaseColumns._ID} = ?", arrayOf(id.toString())
    )
}

// Fonction pour supprimer un employé de la base de données
fun deleteEmployeeDB(dbHelper: FeedReaderDbHelper, id: Long) {
    val db = dbHelper.writableDatabase
    db.delete(
        FeedReaderContract.FeedEntry.TABLE_NAME,
        "${BaseColumns._ID} = ?", arrayOf(id.toString())
    )
}

// Fonction pour supprimer tous les employés de la base de données
fun deleteAllEmployeesDB(dbHelper: FeedReaderDbHelper) {
    val db = dbHelper.writableDatabase
    db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null)
}