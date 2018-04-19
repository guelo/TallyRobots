package miguel.tallyrobots

import java.util.concurrent.TimeUnit

object SingletonHolder {
	val configuration = Configuration()

	val gameLoop = GameLoop(configuration)

}

