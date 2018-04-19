package miguel.tallyrobots.ui

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import miguel.tallyrobots.R
import miguel.tallyrobots.SingletonHolder


class MainActivity : AppCompatActivity() {

	private val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		configureToolbar()

		SingletonHolder.gameLoop.events
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { gameState ->
				grid_view.board = gameState.board
				tv_red_robot_score.text = gameState.redPlayer.wins.toString()
				tv_blue_robot_score.text = gameState.bluePlayer.wins.toString()

				if (gameState.isWinner ) {
					tg.startTone(ToneGenerator.TONE_PROP_BEEP)
				}
			}
	}

	private fun configureToolbar() {
		toolbar.title = "Tally Robots"
		toolbar.inflateMenu(R.menu.toolbar)
/*
		toolbar.setOnMenuItemClickListener { item: MenuItem ->
			if (item.itemId == R.id.settings) {
				drawer_layout.openDrawer(Gravity.RIGHT)
				true
			} else {
				false
			}
		}
*/
	}
}
