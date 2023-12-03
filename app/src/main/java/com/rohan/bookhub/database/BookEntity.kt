package com.rohan.bookhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("books")
data class BookEntity(
    @PrimaryKey val book_id:Int,
    @ColumnInfo("book_name") val bookName:String,
    @ColumnInfo("book_author") val bookAuthor:String,
    @ColumnInfo("book_price") val bookPrice:String,
    @ColumnInfo("book_rating") val bookRating:String,
    @ColumnInfo("book_desc") val bookDesc:String,
    @ColumnInfo("book_image") val bookImage:String
)