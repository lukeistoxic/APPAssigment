package org.example.student.dotsboxgame

import uk.ac.bournemouth.ap.dotsandboxeslib.*
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.Matrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.MutableSparseMatrix
import uk.ac.bournemouth.ap.dotsandboxeslib.matrix.SparseMatrix
import java.lang.IllegalStateException


class StudentDotsBoxGame(columns: Int, rows: Int, players: List<Player>) : AbstractDotsAndBoxesGame() {

    override val players: List<Player> = players.toList() //Converts passed 'Players' into list.

    var currentPlayerInt = 0
    override val currentPlayer: Player get() = players[currentPlayerInt] //establish current player.

    override val boxes: Matrix<StudentBox> =
        MutableMatrix(columns, rows, ::StudentBox)

    override val lines: SparseMatrix<StudentLine> =
        MutableSparseMatrix(columns+1, (rows*2)+1, ::StudentLine)
        { x, y -> x < columns || y % 2 != 0 }

    override var isFinished: Boolean = false //isFinished is default false unless boxes are filled.
        get() {
            return !boxes.any { it.owningPlayer == null }
        }

    override fun playComputerTurns() {
        var current = currentPlayer
        while (current is ComputerPlayer && !isFinished) {
            current.makeMove(this)
            current = currentPlayer
        }
    }

    inner class StudentLine(lineX: Int, lineY: Int) : AbstractLine(lineX, lineY) {
        override var isDrawn: Boolean = false

        override val adjacentBoxes: Pair<StudentBox?, StudentBox?>
            get() {
                if (lineY % 2 == 0) //lineY is even, so boxes are vertically adjacent to the line.
                {
                    if (lineY==0){ //checks if top adjacent box is out of bounds. If so, return null.
                        return Pair(null, boxes[lineX, lineY / 2])
                        //returns bottom box with null top box, as it is invalid (out of bounds.)
                    }
                    if (lineY==lines.maxHeight-1){ //checks if the bottom adjacent is invalid.
                        return Pair(boxes[lineX, (lineY - 2) / 2], null)
                        //returns top box with null bottom box, as it is invalid (out of bounds).
                    }
                    return Pair(boxes[lineX, (lineY - 2) / 2], boxes[lineX, lineY / 2])
                }
                else {  //lineY is odd, so boxes are horizontally adjacent to the line.

                    if (lineX==0){ //checks if left box adjacent is out of bounds.
                        return Pair(null, boxes[lineX, (lineY - 1) / 2])
                        //returns right box with null left box.
                    }
                    if (lineX==lines.maxWidth-1){ //checks if right adjacent is invalid.
                        return Pair(boxes[lineX - 1, (lineY - 1) / 2], null)
                        //returns left box with null right box.
                    }
                    return Pair(boxes[lineX - 1, (lineY - 1) / 2], boxes[lineX, (lineY - 1) / 2])
                }
            }

        override fun drawLine() {
            var drawnBox = false

            if (lines[lineX, lineY].isDrawn) { //checks if the line is already drawn.
                throw IllegalStateException()
            }

            lines[lineX, lineY].isDrawn = true //sets isDrawn property from false to true.

            val boxCheck = this.adjacentBoxes //grab's the drawn line's two adjacent boxes.
            val firstBoxCheck = boxCheck.first //first adjacent box is declared here.
            val secondBoxCheck = boxCheck.second  //second one, declared here.

            if (firstBoxCheck!=null) { //checks to see if first box was declared invalid.
                if (firstBoxCheck.boundingLines.all { line -> line.isDrawn }) { //is box complete?
                    firstBoxCheck.owningPlayer = currentPlayer //assign box to the current player.
                    drawnBox = true //Informs the program that a box has been drawn.
                }
            }
            if (secondBoxCheck!=null) { //checks to see if the second box was declared invalid.
                if (secondBoxCheck.boundingLines.all { line -> line.isDrawn }) { //is box complete?
                    secondBoxCheck.owningPlayer = currentPlayer //assign box to the current player.
                    drawnBox = true //Informs the program that a box has been drawn.
                }
            }

            if (!drawnBox){ //if no boxes are drawn, iterate to next player.
                currentPlayerInt = (currentPlayerInt+1)%(players.size)
            }

            playComputerTurns() //Initiates logic for computer drawing a line in AIRandom class.

            //Stores player scores alongside their object for fireGameOver to take.
            val scoreArray: MutableList<Pair<Player, Int>> = mutableListOf()
            val scoreList = getScores()
            for (player in players.indices){
                scoreArray.add(Pair(players[player], scoreList[player]))
            }

            if (isFinished){ //if there are no more null boxes, game is now over.
                fireGameOver(scoreArray) //sends the scores with corresponding player.
            }

            fireGameChange() //Inform game state change.
        }
    }

    inner class StudentBox(boxX: Int, boxY: Int) : AbstractBox(boxX, boxY) {

        override var owningPlayer: Player? = null //declaring the box isn't yet owned, but can be.

        override val boundingLines: Iterable<DotsAndBoxesGame.Line>
            get() =
                listOf(
                    lines[boxX, (boxY * 2)], //Top bounding line coordinates.
                    lines[boxX, ((boxY * 2) + 2)], //Bottom line
                    lines[boxX, ((boxY * 2) + 1)], //Left line
                    lines[(boxX + 1), ((boxY * 2) + 1)] //Right line
                      )

    }
}
