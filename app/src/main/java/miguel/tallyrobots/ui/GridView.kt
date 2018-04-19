package miguel.tallyrobots.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import miguel.tallyrobots.*
import kotlin.math.min

class GridView : View {
	private val redPaint = Paint().apply { color = resources.getColor(R.color.redRobot) }
	private val redPathPaint = Paint().apply { color = resources.getColor(R.color.redRobotPath) }
	private val bluePaint = Paint().apply { color = resources.getColor(R.color.blueRobot) }
	private val bluePathPaint = Paint().apply { color = resources.getColor(R.color.blueRobotPath) }
	private val prizePaint = Paint().apply { color = Color.GREEN }
	private val emptyPaint = Paint().apply { color = resources.getColor(R.color.emptyCell) }

	private val starPaint = Paint().apply {
		color = Color.RED
		isAntiAlias = true
		style = Paint.Style.FILL
	}
	private val starPath = Path()

	private val circlePadding = dpToPx(1)

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

	var board = Board(7, 7)
		set(value) {
			field = value
			invalidate()
		}

	override fun onDraw(canvas: Canvas) {
		val board = board
		val cellWidth = measuredWidth / board.cols.toFloat()
		val cellHeight = measuredHeight / board.rows.toFloat()

		board.forEachIndexed { colIndex, rows ->
			rows.forEachIndexed { rowIndex, cellState ->
				canvas.drawCircle(
					colIndex * cellWidth + cellWidth / 2, // circle center x
					rowIndex * cellHeight + cellHeight / 2, // circle center y
					min(cellWidth, cellHeight) / 2 - circlePadding, // radius

					when (cellState) { //paint
						CellState.EMPTY -> emptyPaint
						CellState.RED -> redPaint
						CellState.BLUE -> bluePaint
						CellState.RED_PAST -> redPathPaint
						CellState.BLUE_PAST -> bluePathPaint
						CellState.PRIZE -> prizePaint
					}
				)

				if (cellState  == CellState.PRIZE) {
					val left = cellWidth * colIndex
					val top = cellHeight * rowIndex
					val right = left + cellWidth
					val bottom = top + cellHeight
					drawStar(left, top, right, bottom, canvas)
				}
			}
		}
	}

	private fun drawStar(left: Float, top: Float, right: Float, bottom: Float, canvas: Canvas) {
		val width = right - left
		val height = bottom - top
		val min = Math.min(width, height)
		val half = min / 2
		val midx = (width / 2) - half + left

		starPath.reset()
		// top left
		starPath.moveTo(midx + half * 0.5f, half * 0.84f + top)
		// top right
		starPath.lineTo(midx + half * 1.5f, half * 0.84f + top)
		// bottom left
		starPath.lineTo(midx + half * 0.68f, half * 1.45f + top)
		// top tip
		starPath.lineTo(midx + half * 1.0f, half * 0.5f + top)
		// bottom right
		starPath.lineTo(midx + half * 1.32f, half * 1.45f + top)
		// top left
		starPath.lineTo(midx + half * 0.5f, half * 0.84f + top)

		starPath.close()
		canvas.drawPath(starPath, starPaint)

	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)

		val squareDimension = min(measuredWidth, measuredHeight)
		setMeasuredDimension(squareDimension, squareDimension)
	}

	fun dpToPx(dp: Int)= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
}

