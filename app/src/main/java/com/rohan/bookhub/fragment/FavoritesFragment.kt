package com.rohan.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.rohan.bookhub.R
import com.rohan.bookhub.adapter.FavouriteRecyclerAdapter
import com.rohan.bookhub.database.BookDatabase
import com.rohan.bookhub.database.BookEntity

class FavoritesFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    var dbBookList = listOf<BookEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressBar =view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE


        layoutManager = GridLayoutManager(activity as Context,2)
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        if (activity != null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<BookEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<BookEntity> {

            val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()
            return db.bookDao().getAllBooks()
        }
    }

}