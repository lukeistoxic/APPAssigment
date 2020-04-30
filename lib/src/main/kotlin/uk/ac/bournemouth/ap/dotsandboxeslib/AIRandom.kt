package uk.ac.bournemouth.ap.dotsandboxeslib

open class AIRandom: ComputerPlayer (){

    override fun makeMove(game: DotsAndBoxesGame){
         val possibleMoves = game.lines.filter { !it.isDrawn }
         val chosenMove = possibleMoves.random()
         chosenMove.drawLine()
    }
}
