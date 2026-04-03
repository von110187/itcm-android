package my.edu.tarc.itcm

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SearchForPostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var listViewHistory: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_post)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        val buttonSearch: Button = findViewById(R.id.buttonSearch)
        val buttonClearAll: Button = findViewById(R.id.buttonClearAll)
        val editTextSearchForPost: EditText = findViewById(R.id.editTextSearchForPost)
        listViewHistory = findViewById(R.id.listViewHistory)

        val textViewCommunity = findViewById<TextView>(R.id.textViewCommunity)
        textViewCommunity.setOnClickListener {
            val intent = Intent(this, HomepageActivity::class.java)
            startActivity(intent)
        }

        val textViewMarketplace = findViewById<TextView>(R.id.textViewMarketplace)
        textViewMarketplace.setOnClickListener {
            val intent = Intent(this, MarketplaceActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the search button
        buttonSearch.setOnClickListener {
            val query = editTextSearchForPost.text.toString().trim()
            saveSearchHistory(query)
            startSearchResultActivity(query)
        }

        // Set click listener for the clear all button
        buttonClearAll.setOnClickListener {
            clearAllSearchHistory()
        }

        // Display search history
        displaySearchHistory()
    }

    private fun saveSearchHistory(query: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (!userEmail.isNullOrEmpty()) {
                val historyRef = database.child("history").child(userEmail.replace(".", ","))
                // Check if the query already exists in the database
                historyRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val history = postSnapshot.getValue(String::class.java)
                            if (history == query) {
                                // If the query already exists, remove the old entry
                                postSnapshot.ref.removeValue()
                            }
                        }
                        // Add the new query as a history entry
                        historyRef.push().setValue(query)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }

    private fun startSearchResultActivity(query: String) {
        val intent = Intent(this, SearchPostResultActivity::class.java)
        intent.putExtra("QUERY", query)
        startActivity(intent)
    }

    private fun clearAllSearchHistory() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (!userEmail.isNullOrEmpty()) {
                val historyRef = database.child("history").child(userEmail.replace(".", ","))
                historyRef.removeValue()
            }
        }
    }

    private fun displaySearchHistory() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (!userEmail.isNullOrEmpty()) {
                val historyRef = database.child("history").child(userEmail.replace(".", ","))
                val historyListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val historyList = mutableListOf<DataSnapshot>()
                        for (postSnapshot in snapshot.children) {
                            historyList.add(postSnapshot)
                        }
                        val adapter = HistoryAdapter(this@SearchForPostActivity, historyList)
                        listViewHistory.adapter = adapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                }
                historyRef.addValueEventListener(historyListener)
            }
        }
    }

    inner class HistoryAdapter(private val context: Context, private val historyList: List<DataSnapshot>) : ArrayAdapter<DataSnapshot>(context, R.layout.list_item_history, historyList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_history, parent, false)
            val historySnapshot = historyList[position]
            val history = historySnapshot.getValue(String::class.java)
            val textViewHistoryItem = view.findViewById<TextView>(R.id.textViewHistoryItem)
            textViewHistoryItem.text = history

            // Set click listener for textViewHistoryItem
            textViewHistoryItem.setOnClickListener {
                // Retrieve the text of the clicked history item
                val query = historySnapshot.getValue(String::class.java)
                // Start search result activity with the clicked query
                if (query != null) {
                    startSearchResultActivity(query)
                }
            }

            // Set click listener for the clear button
            view.findViewById<Button>(R.id.buttonClearHistory).setOnClickListener {
                // Remove the selected history item
                historySnapshot.ref.removeValue()
            }
            return view
        }
    }
}
