package miguel.tallyrobots

class Configuration {
	var tickPeriodMilliSeconds: Long = 100
	var boardRows: Int = 15
	var boardCols: Int = 15
	var redInitialPosition: Position = Position(0, 14)
	var blueInitialPosition: Position = Position(14, 0)
	var resetBackToCornersAfterWin = false
	var clearTrailsAfterWin: Boolean = false
}
