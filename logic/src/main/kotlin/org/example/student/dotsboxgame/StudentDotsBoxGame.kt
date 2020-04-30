package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix
import java.lang.IllegalStateException


class StudentDotsBoxGame(columns: Int, rows: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {

    override val players: List<Player> = players.toList()
    // completeTODO("You will need to get players from your constructor")

    var currentPlayerInt = 0
    override val currentPlayer: Player get() = players[currentPlayerInt]
    //completeTODO("Determine the current player, like keeping" + "the index into the players list")

    // NOTE: you may want to me more specific in the box type if you use that type in your class
    override val boxes: Matrix<StudentBox> =
        MutableMatrix(columns, rows, ::StudentBox)
    //completeTODO("Create a matrix initialized with your own box type")

    override val lines: SparseMatrix<StudentLine> =
        MutableSparseMatrix(columns+1, (rows*2)+1, ::StudentLine)
        { x, y -> x < columns || y % 2 != 0 }
    //completeTODO("Create a matrix initialized with your own line type")

    override var isFinished: Boolean = false
        get() {
            return !boxes.any { it.owningPlayer == null }
        }
    //completeTODO("Provide this getter. Note you can make it a var to do so")

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && !isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    /**
     * This is an inner class as it needs to refer to the game to be able to look up the correct
     * lines and boxes. Alternatively you can have a game property that does the same thing without
     * it being an inner class.
     */
    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false
        //completeTODO("Provide this getter. Note you can make it a var to do so")

        override val adjacentBoxes: Pair<StudentBox?, StudentBox?>
            get() {
                if (lineY % 2 == 0) //lineY is even, so boxes are vertically adjacent
                {
                    if (lineY==0){ //checks if top adjacent is invalid
                        return Pair(null, boxes[lineX, lineY / 2])
                        //returns bottom box with null top box
                    }
                    if (lineY==lines.maxHeight-1){ //checks if bottom adjacent is invalid
                        return Pair(boxes[lineX, (lineY - 2) / 2], null)
                        //returns top box with null bottom box
                    }
                    return Pair(boxes[lineX, (lineY - 2) / 2], boxes[lineX, lineY / 2])
                }
                else {  //lineY is odd, so boxes are horizontally adjacent

                    if (lineX==0){ //checks if left adjacent is invalid
                        return Pair(null, boxes[lineX, (lineY - 1) / 2])
                        //returns right box with null left box
                    }
                    if (lineX==lines.maxWidth-1){ //checks if right adjacent is invalid
                        return Pair(boxes[lineX - 1, (lineY - 1) / 2], null)
                        //returns left box with null right box
                    }
                    return Pair(boxes[lineX - 1, (lineY - 1) / 2], boxes[lineX, (lineY - 1) / 2])
                }
            }

        //completeTODO("You need to look up the correct boxes for this to work")


        override fun drawLine() {
            var drawnBox = false
            if (lines[lineX, lineY].isDrawn) { //checks if the line is already drawn
                throw IllegalStateException()
            }

            lines[lineX, lineY].isDrawn = true //sets isDrawn property from false to true

            val boxCheck = this.adjacentBoxes //grab's the drawn line's two adjacent boxes
            val firstBoxCheck = boxCheck.first //first one is declared here
            val secondBoxCheck = boxCheck.second  //second one is declared here

            if (firstBoxCheck!=null) { //checks to see if first box was assigned null
                if (firstBoxCheck.boundingLines.all { line -> line.isDrawn }) { //is box complete?
                    firstBoxCheck.owningPlayer = currentPlayer //assign box to the current player
                    drawnBox = true
                }
            }
            if (secondBoxCheck!=null) { //checks to see if the second box was assigned null
                if (secondBoxCheck.boundingLines.all { line -> line.isDrawn }) { //is box complete?
                    secondBoxCheck.owningPlayer = currentPlayer //assign box to the current player
                    drawnBox = true
                }
            }

            if (drawnBox){
                currentPlayerInt
            }
            else{
                currentPlayerInt = (currentPlayerInt+1)%(players.size)
            }

            playComputerTurns()

            if (isFinished){ //if amount of lines not drawn is 0, game is over
                val scoreArray: MutableList<Pair<Player, Int>> = mutableListOf()
                val scoreList = getScores()
                for (player in players.indices){
                    scoreArray.add(Pair(players[player], scoreList[player]))
                }
                fireGameOver(scoreArray)
            }

            fireGameChange() //refreshes game state

            //completeTODO("Implement the logic for a player drawing a line. Don't forget to inform the listeners (fireGameChange, fireGameOver)")

        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {

        override var owningPlayer: Player? = null
        //completeTODO("Provide this getter. Note you can make it a var to do so")

        override val boundingLines: Iterable<DotsAndBoxesGame.Line>
            get() =
                listOf(
                    lines[boxX, (boxY * 2)],
                    lines[boxX, ((boxY * 2) + 2)],
                    lines[boxX, ((boxY * 2) + 1)],
                    lines[(boxX + 1), ((boxY * 2) + 1)]
                      )
        //completeTODO("Look up the correct lines from the game outer class")

    }
}
