package miguel.tallyrobots

import android.renderscript.RenderScript
import java.util.*
import kotlin.math.abs

fun breadthFirstSearch(start: Position, goal: Position, board: Board): Position {
	val frontier = mutableListOf<Position>()
	frontier.add(start)
	val cameFrom = mutableMapOf<Position, Position>()
	cameFrom[start] = start

	while (frontier.isNotEmpty()) {
		val current = frontier.pop()

		if (current == goal) break
		board.neighbors(current)
			.filter { !cameFrom.containsKey(it) }
			.forEach { next ->
				frontier.add(next)
				cameFrom[next] = current
			}
	}

	return getNextMoveFromPath(goal, start, cameFrom, board)
}

fun greedyBestFirstSearch(start: Position, goal: Position, board: Board): Position {
	val frontier = PriorityQueue<Position>(10, compareBy { abs(goal.x - it.x) + abs(goal.y - it.y) })
	frontier.add(start)
	val cameFrom = mutableMapOf<Position, Position>()
	cameFrom[start] = start

	while (frontier.isNotEmpty()) {
		val current = frontier.poll()
		if (current == goal) break
		board.neighbors(current)
			.filter { !cameFrom.containsKey(it) }
			.forEach { next ->
				frontier.add(next)
				cameFrom[next] = current
			}

	}

	return getNextMoveFromPath(goal, start, cameFrom, board)
}

fun AStar(start: Position, goal: Position, board: Board): Position {
	val frontier = PriorityQueue<Pair<Position, Int>>(10, compareBy { it.second })
	frontier.add(Pair(start, 0))
	val cameFrom = mutableMapOf<Position, Position>()
	cameFrom[start] = start
	val costSoFar = mutableMapOf<Position, Int>()
	costSoFar[start] = 0

	while (frontier.isNotEmpty()) {
		val current = frontier.poll()
		if (current.first == goal) break

		for (next in board.neighbors(current.first)) {
			val newCost = (costSoFar[current.first] ?: 0) + 1
			if (!costSoFar.containsKey(next) || newCost < costSoFar[next] ?: 0) {
				costSoFar[next] = newCost
				val priority =  newCost + abs(goal.x - next.x) + abs(goal.y - next.y)
				frontier.add(Pair(next, priority))
				cameFrom[next] = current.first
			}
		}
	}

	return getNextMoveFromPath(goal, start, cameFrom, board)
}

private fun getNextMoveFromPath(
	goal: Position,
	start: Position,
	cameFrom: Map<Position, Position>,
	board: Board
): Position {
	var current = goal
	val path = mutableListOf<Position>()
	while (current != start) {
		path.add(current)
		if (cameFrom[current] != null) {
			current = cameFrom[current]!!
		} else {
			// no path to goal
			val neighbors = board.neighbors(start)
			if (neighbors.isNotEmpty()) {
				// If we still have some neighbors pick a random one
				path.add(neighbors.random())
			} else {
				// if we're stuck just return the current position
				path.add(start)
			}
			break
		}
	}
	return if (path.isEmpty()) start else path.last()
}

fun <T> MutableList<T>.pop(): T {
	val last = last()
	removeAt(lastIndex)
	return last
}

fun random(start: Position, goal: Position, board: Board): Position {
	val neighbors = board.neighbors(start)
	return if (neighbors.isEmpty()) {
		start
	} else {
		neighbors.random()
	}
}

private fun List<Position>.random() = get((0..size).random())

