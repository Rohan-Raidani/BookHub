package com.rohan.bookhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohan.bookhub.R
import com.rohan.bookhub.database.BookDatabase
import com.rohan.bookhub.database.BookEntity
import com.rohan.bookhub.util.ConnectionManger
import com.squareup.picasso.Picasso
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView
    lateinit var txtBookDesc: TextView
    lateinit var btnAddToFavourites: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var bookId: String? = "100"
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFavourites = findViewById(R.id.btnAddToFavourites)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.GONE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occured",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bookId.isNullOrEmpty()) {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected Error Occured",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val queue = Volley.newRequestQueue(this@DescriptionActivity)
            val url = "http://13.235.250.119/v1/book/get_book/"

            val jsonParams = JSONObject()
            jsonParams.put("book_id", bookId)


            if (ConnectionManger().checkConnectivity(this@DescriptionActivity)) {
                val jsonRequest = object :
                    JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                        try {
                            val success = it.getBoolean("success")
                            if (success) {
                                val bookJsonObject = it.getJSONObject("book_data")
                                progressLayout.visibility = View.GONE
                                val bookImageUrl = bookJsonObject.getString("image")
                                Picasso.get().load(bookJsonObject.getString("image"))
                                    .error(R.drawable.default_book_cover).into(imgBookImage)
                                txtBookName.text = bookJsonObject.getString("name")
                                txtBookAuthor.text = bookJsonObject.getString("author")
                                txtBookPrice.text = bookJsonObject.getString("price")
                                txtBookRating.text = bookJsonObject.getString("rating")
                                txtBookDesc.text = bookJsonObject.getString("description")


                                val bookEntity = BookEntity(
                                    bookId?.toInt() as Int,
                                    txtBookName.text.toString(),
                                    txtBookAuthor.text.toString(),
                                    txtBookPrice.text.toString(),
                                    txtBookRating.text.toString(),
                                    txtBookDesc.text.toString(),
                                    bookImageUrl
                                )

                                val checkFav =
                                    DBAsyncTask(applicationContext, 1, bookEntity).execute()
                                val isFav = checkFav.get()
                                if (isFav) {
                                    btnAddToFavourites.text = "Remove From Favourites"
                                    val favColor = ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorFavourite
                                    )
                                    btnAddToFavourites.setBackgroundColor(favColor)
                                } else {
                                    btnAddToFavourites.text = "Add to Favourites"
                                    val favColor = ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorPrimary
                                    )
                                    btnAddToFavourites.setBackgroundColor(favColor)
                                }
                                btnAddToFavourites.setOnClickListener {
                                    if (!DBAsyncTask(
                                            applicationContext,
                                            1,
                                            bookEntity
                                        ).execute().get()
                                    ) {
                                        val async = DBAsyncTask(
                                            applicationContext,
                                            2,
                                            bookEntity
                                        ).execute()
                                        val result = async.get()
                                        if (result) {
                                            Toast.makeText(
                                                this@DescriptionActivity,
                                                "Book Added to Favourites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            btnAddToFavourites.text = "Remove From Favourites"
                                            val favColor = ContextCompat.getColor(
                                                applicationContext,
                                                R.color.colorFavourite
                                            )
                                            btnAddToFavourites.setBackgroundColor(favColor)
                                        }else{
                                            Toast.makeText(
                                                this@DescriptionActivity,
                                                "Some Error Occured",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        val async = DBAsyncTask(applicationContext,3,bookEntity).execute()
                                        val result = async.get()
                                        if (result){
                                            Toast.makeText(
                                                this@DescriptionActivity,
                                                "Book Removed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            btnAddToFavourites.text = "Add to Favourites"
                                            val favColor = ContextCompat.getColor(
                                                applicationContext,
                                                R.color.colorPrimary
                                            )
                                            btnAddToFavourites.setBackgroundColor(favColor)
                                        }else{
                                            Toast.makeText(
                                                this@DescriptionActivity,
                                                "Some Error Occured",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Some Error Occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Some Error Occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        Response.ErrorListener {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "Volley Error Occured",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Context-type"] = "application/json"
                        headers["token"] = "46d07f91f13618"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else {
                val dialog = AlertDialog.Builder(this@DescriptionActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Not Connected to internet")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@DescriptionActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}

class DBAsyncTask(val context: Context, val mode: Int, val bookEntity: BookEntity) :
    AsyncTask<Void, Void, Boolean>() {
    /*
    MODE 1-> Check DB if book is favourite or not
    MODE 2 -> Save book into DB as favourite
    MODE 3 -> Remove the favourite book
             */
    val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when (mode) {
            1 -> {
                // MODE 1-> Check DB if book is favourite or not
                val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                db.close()
                return book != null
            }

            2 -> {
                // MODE 2 -> Save book into DB as favourite
                db.bookDao().insertBook(bookEntity)
                db.close()
                return true
            }

            3 -> {
                //  MODE 3 -> Remove the favourite book
                db.bookDao().deleteBook(bookEntity)
                db.close()
                return true
            }
        }
        return false
    }

}