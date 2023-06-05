package com.example.theoriginal2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.*

class TasksActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var taskList: ArrayList<String>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        listView = findViewById(R.id.list_view_tasks)
        taskList = ArrayList()

        val databaseUrl = "https://theoriginial-copy.asia-southeast1.firebasedatabase.app/"
        val database: FirebaseDatabase = FirebaseDatabase.getInstance(databaseUrl)
        databaseReference = database.reference.child("users").child("Task")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        listView.adapter = adapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                taskList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val task = childSnapshot.getValue(String::class.java)
                    task?.let {
                        taskList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
