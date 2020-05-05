package uk.ac.bournemouth.ap.dotsandboxes
import org.example.student.dotsboxgame.StudentDotsBoxGame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private var mGameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mGameView = GameView(this)
        setContentView(mGameView)
    }


}
