package edu.temple.audiobb

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService

class MainActivity : AppCompatActivity(), BookListFragment.BookSelectedInterface, ControlFragment.ControlInterface {



    private lateinit var bookListFragment : BookListFragment
    private lateinit var controlFragment: ControlFragment
    lateinit var mediaPlayer: PlayerService.MediaControlBinder


    private val searchRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        supportFragmentManager.popBackStack()
        it.data?.run {
            bookListViewModel.copyBooks(getSerializableExtra(BookList.BOOKLIST_KEY) as BookList)
            bookListFragment.bookListUpdated()
        }

    }

    private val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    private val selectedBookViewModel : SelectedBookViewModel by lazy {
        ViewModelProvider(this).get(SelectedBookViewModel::class.java)
    }

    private val bookListViewModel : BookList by lazy {
        ViewModelProvider(this).get(BookList::class.java)
    }

    companion object {
        const val BOOKLISTFRAGMENT_KEY = "BookListFragment"
        const val CONTROLFRAGMENT_KEY = "ControlFragment"
    }

    private var serviceConnected: Boolean = false
    lateinit var serviceIntent: Intent

    var progressHandler = Handler(
        Looper.getMainLooper()
    ) { message -> // Don't update contols if we don't know what bok the service is playing
        if (message.obj != null && selectedBookViewModel.getPlayingBook().value != null) {
            controlFragment.updateProgress(((message.obj as PlayerService.BookProgress).progress as Float / selectedBookViewModel.getPlayingBook().value!!.duration * 100) as Int)
            controlFragment.setPlaying(selectedBookViewModel.getPlayingBook().value!!.title)
        }
        true
    }



    private val Connection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, ibinder: IBinder?) {
            mediaPlayer = ibinder as PlayerService.MediaControlBinder
            mediaPlayer.setProgressHandler(progressHandler)
            serviceConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            serviceConnected = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //register for download reciever here
        // this is how you start a intent to a service
        serviceIntent = Intent(this, PlayerService::class.java)
        bindService(serviceIntent,Connection, BIND_AUTO_CREATE)

        // If we're switching from one container to two containers
        // clear BookDetailsFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookDetailsFragment
            && selectedBookViewModel.getSelectedBook().value != null) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            controlFragment = ControlFragment()
            bookListFragment = BookListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, bookListFragment, BOOKLISTFRAGMENT_KEY)
                .add(R.id.control, controlFragment, CONTROLFRAGMENT_KEY)
                .commit()
        } else {
            bookListFragment = supportFragmentManager.findFragmentByTag(BOOKLISTFRAGMENT_KEY) as BookListFragment
            // If activity loaded previously, there's already a BookListFragment
            // If we have a single container and a selected book, place it on top
            if (isSingleContainer && selectedBookViewModel.getSelectedBook().value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // If we have two containers but no BookDetailsFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookDetailsFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookDetailsFragment())
                .commit()

        findViewById<ImageButton>(R.id.searchButton).setOnClickListener {
            searchRequest.launch(Intent(this, SearchActivity::class.java))
        }

    }

    override fun onBackPressed() {
        // Backpress clears the selected book
        selectedBookViewModel.setSelectedBook(null)
        super.onBackPressed()
    }

    override fun bookSelected() {
        // Perform a fragment replacement if we only have a single container
        // when a book is selected

        if (isSingleContainer) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }



    override fun play() {
        //Toast.makeText(this, "Pressed Play", Toast.LENGTH_SHORT).show()
        var tempBook = selectedBookViewModel.getSelectedBook().value
        if(tempBook != null){
            controlFragment.nowPlaying.text = tempBook.title
            //Log.d("FILE", bookList.getByTitle(selectedBook).getFile().toString() + ": path of selected book")

            if(serviceConnected){
                mediaPlayer.play(tempBook.id)
                // download here for next assignment
                Toast.makeText(this, "Playing "+tempBook.title, Toast.LENGTH_SHORT).show()
            }

        }else{

        }
    }

    override fun pause() {
        Toast.makeText(this, "Pressed Paused", Toast.LENGTH_SHORT).show()
    }

    override fun stop() {
        Toast.makeText(this, "Pressed Stop", Toast.LENGTH_SHORT).show()
    }

    override fun changeTime(position: Int) {
        Toast.makeText(this, "Changing Time", Toast.LENGTH_SHORT).show()

    }
}