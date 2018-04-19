package miguel.tallyrobots

enum class CellState {
	EMPTY, RED, BLUE, RED_PAST, BLUE_PAST, PRIZE;

	companion object {
		val occupiedStates = setOf(RED, BLUE, RED_PAST, BLUE_PAST)
	}
}

data class Board(
	val cols: Int,
	val rows: Int,
	/**
	 * The Array of Arrays is mutable
	 */
	val arrayOfArrays: Array<Array<CellState>> = Array<Array<CellState>>(cols) { Array<CellState>(rows) { CellState.EMPTY } }
) : Iterable<Array<CellState>> {

	operator fun get(index: Int) = arrayOfArrays[index]

	override fun iterator(): Iterator<Array<CellState>> = arrayOfArrays.iterator()

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Board

		if (cols != other.cols) return false
		if (rows != other.rows) return false

		return true
	}

	fun neighbors(current: Position): List<Position> {
		return setOf(current.down(), current.left(), current.right(), current.up())
			.filterNot {
				isIllegal(it) || CellState.occupiedStates.contains(get(it))
			}
	}

	fun get(position: Position) = arrayOfArrays[position.x][position.y]

	fun set(position: Position, cellState: CellState) {
		arrayOfArrays[position.x][position.y] = cellState
	}

	private fun isIllegal(position: Position) = position.x < 0
			|| position.y < 0
			|| position.x == cols
			|| position.y == rows

}

data class Position(val x: Int, val y: Int) {
	fun left() = Position(x - 1, y)
	fun right() = Position(x + 1, y)
	fun down() = Position(x, y + 1)
	fun up() = Position(x, y - 1)
}

data class Player(val position: Position, val wins: Int)

data class GameState(
	val board: Board,
	val redPlayer: Player,
	val bluePlayer: Player,
	val prizePosition: Position,
	val redPlaysNext: Boolean,
	val redIsStuck: Boolean = false,
	val blueIsStuck: Boolean = false
) {

	val winner: Player? = when (prizePosition) {
		redPlayer.position -> redPlayer
		bluePlayer.position -> bluePlayer
		else -> null
	}

	val isWinner = winner != null

	fun moveRed(newPosition: Position): GameState {
		if (newPosition == redPlayer.position) return this.copy(redIsStuck = true, redPlaysNext = !redPlaysNext)

		val newBoard = board.copy()
		newBoard.set(redPlayer.position, CellState.RED_PAST)
		newBoard.set(newPosition, CellState.RED)
		return this.copy(
			board = newBoard,
			redPlayer = redPlayer.copy(position = newPosition),
			redPlaysNext = !redPlaysNext
		)
	}

	fun moveBlue(newPosition: Position): GameState {
		if (newPosition == bluePlayer.position) return this.copy(blueIsStuck = true, redPlaysNext = !redPlaysNext)

		val newBoard = board.copy()
		newBoard.set(bluePlayer.position, CellState.BLUE_PAST)
		newBoard.set(newPosition, CellState.BLUE)
		return this.copy(
			board = newBoard,
			bluePlayer = bluePlayer.copy(position = newPosition),
			redPlaysNext = !redPlaysNext
		)
	}

}