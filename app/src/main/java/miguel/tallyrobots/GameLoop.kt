package miguel.tallyrobots

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class GameLoop(private val config: Configuration) {

	private var gameState = resetGameState(null)

	val events: Observable<GameState> =
	// rxjava interval operator generates ticks on a background thread that drives the game loop
		Observable.interval(config.tickPeriodMilliSeconds, TimeUnit.MILLISECONDS, Schedulers.computation())
			.map {
				gameState
					.resetIfTheresAWin()
					.resetIfBothAreStuck()
					.move()
					.addAnyWinScores()
			}
			.doOnNext { gameState = it }


	private fun GameState.move(): GameState {
		return if (redPlaysNext) {
			val nextPosition = AStar(redPlayer.position, prizePosition, board)
			moveRed(nextPosition)
		} else {
			val nextPosition = greedyBestFirstSearch(bluePlayer.position, prizePosition, board)
			moveBlue(nextPosition)
		}
	}

	private fun GameState.addAnyWinScores(): GameState {
		return when (winner) {
			redPlayer -> copy(redPlayer = redPlayer.copy(wins = redPlayer.wins + 1))
			bluePlayer -> copy(bluePlayer = bluePlayer.copy(wins = bluePlayer.wins + 1))
			else -> this
		}
	}

	private fun GameState.resetIfTheresAWin(): GameState {
		return if (isWinner) {
			resetGameState(gameState)
		} else {
			this
		}
	}

	private fun GameState.resetIfBothAreStuck(): GameState {
		return if (redIsStuck && blueIsStuck) {
			resetGameState(gameState)
		} else {
			this
		}
	}

	private fun resetGameState(gameState: GameState?): GameState {
		val bluePosition =
			if (config.resetBackToCornersAfterWin || gameState == null) config.blueInitialPosition else gameState.bluePlayer.position
		val redPosition =
			if (config.resetBackToCornersAfterWin || gameState == null) config.redInitialPosition else gameState.redPlayer.position

		val bothStuck = gameState?.run { redIsStuck && blueIsStuck } ?: false
		val clearTrails = config.clearTrailsAfterWin || gameState == null || bothStuck

		val prizePosition = if (clearTrails) {
			randomXY(setOf(bluePosition, redPosition))
		} else {
			val notAllowed = mutableListOf<Position>()
			gameState!!.board.forEachIndexed { colIndex, rows ->
				rows.forEachIndexed { rowIndex, cellState ->
					val cell = gameState.board[colIndex][rowIndex]
					if (CellState.occupiedStates.contains(cell)) {
						notAllowed.add(Position(colIndex, rowIndex))
					}
				}
			}
			randomXY(notAllowed)
		}

		return GameState(
			board = if (clearTrails) {
				Board(config.boardCols, config.boardRows).apply {
					set(bluePosition, CellState.BLUE)
					set(redPosition,  CellState.RED)
					set(prizePosition, CellState.PRIZE)
				}
			} else {
				gameState!!.board.apply {
					set(gameState.prizePosition, CellState.EMPTY)
					set(prizePosition, CellState.PRIZE)
				}
			},
			redPlayer = Player(
				position = redPosition,
				wins = gameState?.redPlayer?.wins ?: 0
			),
			bluePlayer = Player(
				position = bluePosition,
				wins = gameState?.bluePlayer?.wins ?: 0
			),
			prizePosition = prizePosition,
			redPlaysNext = randomBoolean()
		)
	}

	private fun randomXY(notAllowed: Iterable<Position>): Position {
		val randomPosition = Position((0..config.boardCols).random(), (0..config.boardRows).random())

		return if (notAllowed.contains(randomPosition)) {
			randomXY(notAllowed)
		} else {
			randomPosition
		}
	}

}

private val randomGenerator = Random()
private fun randomBoolean() = randomGenerator.nextBoolean()
fun ClosedRange<Int>.random() = randomGenerator.nextInt(endInclusive - start) + start
