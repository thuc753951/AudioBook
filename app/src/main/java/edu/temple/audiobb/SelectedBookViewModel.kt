package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedBookViewModel : ViewModel() {

    private val selectedBook: MutableLiveData<Book> by lazy {
        MutableLiveData()
    }

    private val playingBook: MutableLiveData<Book> by lazy {
        MutableLiveData()
    }

    fun getSelectedBook(): LiveData<Book> {
        return selectedBook
    }

    fun setSelectedBook(selectedBook: Book?) {
        this.selectedBook.value = selectedBook
    }

    fun getPlayingBook(): LiveData<Book> {
        return playingBook
    }

    fun setPlayingBook(selectedBook: Book?) {
        this.playingBook.value = selectedBook
    }

}