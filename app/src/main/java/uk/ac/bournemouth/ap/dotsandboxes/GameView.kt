package uk.ac.bournemouth.ap.dotsandboxes
import org.example.student.dotsboxgame.StudentDotsBoxGame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import uk.ac.bournemouth.ap.dotsandboxeslib.*

class GameView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr)

    //Declaring all Paint variables that will be used for various graphics in the program.
    private var mBackgroundColour: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#c7c7c7") //grey
    }
    private var mText: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#000000")
        textSize = 60F
    }
    private var mDotPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#000000")
    }
    private var mVacantLine: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#ffffff")
        strokeWidth = 12.5F
    }
    private var mDrawnLine: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#636363") //dark grey
        strokeWidth = 12.5F
    }
    private var mPlayerPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#0000ff")
    }
    private var mComputerPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#ff0000")
        textSize = 60F
    }
    private var mBoxBackground: Paint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#a8a8a8") //dark grey
    }

    //Declaring all grid and line variables, including line(x,y) coordinates that have been pressed,
    //to allow the View class to interact with the logic and display the game.
    private val dotRadius = 10
    private val dotDiameter = dotRadius * 2
    private val padding = 200
    private val boxWidth = 220

    private val myGestureDetector = GestureDetector(context, myGestureListener())

    private val columns = 3
    private val rows = 4
    private val players = listOf(HumanPlayer(), AIRandom())
    var gameFinished = false
    var lineXBoundingLine = -1
    var lineYBoundingLine = -1
    var mStudentDotsBoxGame: StudentDotsBoxGame = StudentDotsBoxGame(columns, rows, players)

    //Listener declared to listen for fireGameChange() (to refresh game state).
    var listenerImp = object: DotsAndBoxesGame.GameChangeListener{
        override fun onGameChange(game: DotsAndBoxesGame){
            invalidate()
        }
    }
    var gameEnd = object: DotsAndBoxesGame.GameOverListener{
        override fun onGameOver(game: DotsAndBoxesGame, scores: List<Pair<Player, Int>>) {
            var gameFinished = true
        }
    }
    init {
        mStudentDotsBoxGame.addOnGameChangeListener(listenerImp)
        mStudentDotsBoxGame.addOnGameOverListener(gameEnd)
    }

    //All the elements in this view class are drawn here.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var token: Paint  //paint variable to be used in conditional statements.
        val viewWidth: Float = width.toFloat()
        val viewHeight: Float = height.toFloat()
        canvas.drawRect(0F, 0F, viewWidth, viewHeight, mBackgroundColour) //Background.

        //Key at the bottom left of the screen that includes the scores of both Human and AI.
        canvas.drawRect(95F, 1245F, 205F, 1355F, mText)
        canvas.drawRect(95F, 1395F, 205F, 1505F, mText)
        canvas.drawRect(100F, 1250F, 200F, 1350F, mPlayerPaint)
        canvas.drawRect(100F, 1400F, 200F, 1500F, mComputerPaint)
        canvas.drawText("Human", 225F, 1320F, mText)
        canvas.drawText("Computer", 225F, 1470F, mText)
        var scoreHuman = mStudentDotsBoxGame.getScores()[0]
        var scoreAi = mStudentDotsBoxGame.getScores()[1]
        canvas.drawText("$scoreHuman", 135F, 1320F, mText)
        canvas.drawText("$scoreAi", 135F, 1470F, mText)

        //Two for loops to draw out the boxes in the grid, in addition to colouring the box to the
        //corresponding owner.
        for (xValue in 0 until 3) {
            for (yValue in 0 until 4) {
                val cx = (padding + (xValue) * boxWidth) //Assignment of Box X coordinate
                val cy = (padding + (yValue) * boxWidth)//Assignment of Box Y coordinate
                token = if (mStudentDotsBoxGame.boxes[xValue, yValue].owningPlayer == players[0]) {
                    mPlayerPaint //Box owned by Player.
                } else if (mStudentDotsBoxGame.boxes[xValue, yValue].owningPlayer == players[1]) {
                    mComputerPaint //Box owned by Computer.
                } else {
                    mBoxBackground //Not owned yet by anyone.
                }

                canvas.drawRect(
                    cx.toFloat() + dotRadius, cy.toFloat() + dotRadius,
                    cx.toFloat() + boxWidth + dotRadius, cy.toFloat() + boxWidth + dotRadius,
                    token) //Drawing the boxes on the grid
            }
        }

        //Two for loops that iterate to draw the Horizontal Lines for the grid.
        for (yValue in 0 until 10 step 2) { //iterates 0,2,4,6,8
            for (xValue in 0 until 3) { //iterates 0,1,2
                val cx = dotDiameter * (xValue + 1) + dotRadius + (padding * (xValue + 1))
                val cy = dotDiameter * (yValue / 2) + dotRadius + (padding * (yValue / 2))
                token = if (mStudentDotsBoxGame.lines[xValue, yValue].isDrawn) {
                    mDrawnLine
                } else {
                    mVacantLine
                }
                canvas.drawLine(
                    cx.toFloat() - dotDiameter, padding.toFloat() + cy,
                    cx.toFloat() + padding, padding.toFloat() + cy, token
                               ) //Drawing horizontal clickable lines on the display.
            }
        }

        //Two for loops that iterate to draw the Vertical Lines for the grid.
        for (yValue in 1 until 9 step 2) { //iterates 1,3,5,7
            for (xValue in 0 until 4) { //iterates 0,1,2
                val cx = dotDiameter * (xValue) + dotRadius + (padding * (xValue))
                val cy = dotDiameter * (yValue/2) + dotRadius + (padding * (yValue/2) + boxWidth)
                token = if (mStudentDotsBoxGame.lines[xValue, yValue].isDrawn) {
                    mDrawnLine
                } else {
                    mVacantLine
                }
                canvas.drawLine(
                    padding.toFloat() + cx, cy.toFloat() - dotDiameter,
                    padding.toFloat() + cx, cy.toFloat() + padding, token
                               )//Drawing vertical clickable lines on the display.
            }
        }

        //Two for loops to display the dots, that separate each individual line on the grid.
        for (xValue in 1 until 6) {
            for (yValue in 1 until 6) {
                val cx = dotDiameter * xValue - dotRadius + (padding * xValue)
                val cy = dotDiameter * yValue - dotRadius + (padding * yValue)
                canvas.drawCircle(cx.toFloat(), cy.toFloat(), dotRadius.toFloat(), mDotPaint)
            }
        }

        //Text appears on top of screen displaying winner and their score.
        if (mStudentDotsBoxGame.isFinished) {
            if (scoreHuman > scoreAi) {
                canvas.drawText("Winner is - Human! with score: $scoreHuman", 100F, 100F, mText)
            }
            if (scoreHuman < scoreAi) {
                canvas.drawText("Winner is - Computer! with score: $scoreAi", 80F, 100F, mText)
            }
            if (scoreHuman == scoreAi) {
                canvas.drawText("Winner is: Draw! You both scored $scoreAi", 90F, 100F, mText)
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return myGestureDetector.onTouchEvent(ev) || super.onTouchEvent(ev)
    }

    inner class myGestureListener: GestureDetector.SimpleOnGestureListener() {
        override fun onDown(ev: MotionEvent): Boolean {
            return true
        }
        override fun onSingleTapUp(ev: MotionEvent): Boolean {
            var boxXcoordinate = 0
            var boxYcoordinate = 0
            var topLine: Boolean? = null
            var leftLine: Boolean? = null

            //These if statements return the X coordinate of the box that the click event was
            //closest to. Embedded within, are if statements to determine if the click instance was
            //closer to the top or the bottom line, to work out the correct bounding line to return.
            //A line will not be drawn if the click was close to a box corner, to remove ambiguity
            //as to what line the user intended to press.

            if (ev.x.toInt() > padding+boxWidth && ev.x.toInt() < padding+(boxWidth)*2){
                boxXcoordinate = 1
                if (ev.x.toInt() < padding+boxWidth+(boxWidth/8)){
                    leftLine = true
                }
                if (ev.x.toInt() > padding+(boxWidth*2)-(boxWidth/8)){
                    leftLine = false
                }
            }
            else if (ev.x.toInt() < padding+boxWidth){
                boxXcoordinate=0
                if (ev.x.toInt() < padding+(boxWidth/8)){
                    leftLine = true
                }
                if (ev.x.toInt() > padding+boxWidth-(boxWidth/8)){
                    leftLine = false
                }
            }
            else{
                boxXcoordinate = 2
                if (ev.x.toInt() < padding+(boxWidth*2)+(boxWidth/8)){
                    leftLine= true
                }
                if (ev.x.toInt() > padding+(boxWidth*3)-(boxWidth/8)){
                    leftLine = false
                }
            }

            //These if statements return the Y coordinate of the box that the click event was
            //closest to. Embedded within, are if statements to determine if the click instance was
            //closer to the left or the right line, to work out the correct bounding line to return.
            //A line will again not be drawn if the click was close to a box corner, to remove
            //ambiguity as to what line the user intended to press.

            if (ev.y.toInt() > padding+(boxWidth*2) && ev.y.toInt() < padding+(boxWidth*3)){
                boxYcoordinate = 2
                if (ev.y.toInt() < padding+(boxWidth*2)+(boxWidth/8)){
                    topLine = true
                }
                if (ev.y.toInt() > padding+(boxWidth*3)-(boxWidth/8)){
                    topLine = false
                }
            }
            else if (ev.y.toInt() > padding+boxWidth && ev.y.toInt() < padding+(boxWidth*2)){
                boxYcoordinate = 1
                if (ev.y.toInt() < padding+boxWidth+(boxWidth/8)){
                    topLine = true
                }
                if (ev.y.toInt() > padding+(boxWidth*2)-(boxWidth/8)){
                    topLine = false
                }
            }
            else if (ev.y.toInt() < padding+boxWidth){
                boxYcoordinate = 0
                if (ev.y.toInt() < padding+(boxWidth/8)){
                    topLine = true
                }
                if (ev.y.toInt() > padding+(boxWidth*2)-(boxWidth/8)){
                    topLine = false
                }
            }
            else{
                boxYcoordinate = 3
                if (ev.y.toInt() < padding+(boxWidth*3)+(boxWidth/8)){
                    topLine = true
                }
                if (ev.y.toInt() > padding+(boxWidth*4)-(boxWidth/8)){
                    topLine = false
                }
            }

            //If a box is drawn, these values will change from -1 to the correct line coordinate.
            lineXBoundingLine= -1
            lineYBoundingLine= -1

            //These conditions check if there is only one definitive decision as to what line was
            //pressed. If e.g. topLine == true & leftLine == true, click was too close to box corner.
            if (topLine == true && leftLine == null) { //If top line was pressed
                var boxLines = mStudentDotsBoxGame.StudentBox(boxX= boxXcoordinate,
                                                              boxY= boxYcoordinate).boundingLines
                //returns the top bounding box line's x and y coordinates, calculated in logic.
                lineXBoundingLine = boxLines.elementAt(0).lineX
                lineYBoundingLine = boxLines.elementAt(0).lineY

            }
            if (topLine == false && leftLine == null){ //If bottom line was pressed
                var boxLines = mStudentDotsBoxGame.StudentBox(boxX= boxXcoordinate,
                                                              boxY= boxYcoordinate).boundingLines
                //returns the bottom bounding box line's x and y coordinates, calculated in logic.
                lineXBoundingLine = boxLines.elementAt(1).lineX
                lineYBoundingLine = boxLines.elementAt(1).lineY

            }
            if (topLine == null && leftLine == true){ //if left line was pressed
                var boxLines = mStudentDotsBoxGame.StudentBox(boxX= boxXcoordinate,
                                                              boxY= boxYcoordinate).boundingLines
                //returns the left bounding box line's x and y coordinates, calculated in logic.
                lineXBoundingLine = boxLines.elementAt(2).lineX
                lineYBoundingLine = boxLines.elementAt(2).lineY

            }
            if (topLine == null && leftLine == false){ //if right line was pressed
                var boxLines = mStudentDotsBoxGame.StudentBox(boxX= boxXcoordinate,
                                                              boxY= boxYcoordinate).boundingLines
                //returns the right bounding box line's x and y coordinates, calculated in logic.
                lineXBoundingLine = boxLines.elementAt(3).lineX
                lineYBoundingLine = boxLines.elementAt(3).lineY

            }

            //If the move was valid, then these variables would not be -1. So it'll return true and
            //call drawLine()
            return if (lineXBoundingLine != -1 && lineYBoundingLine != -1) {
                mStudentDotsBoxGame.StudentLine(
                    lineX = lineXBoundingLine,
                    lineY = lineYBoundingLine).drawLine()
                true
            } else {
                false
            }
        }
    }
}

