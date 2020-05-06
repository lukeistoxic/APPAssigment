package uk.ac.bournemouth.ap.dotsandboxeslib

open class AIRandom: ComputerPlayer (){

    override fun makeMove(game: DotsAndBoxesGame){

        var lineDrawn = false
        for (box in game.boxes){
            val lines = box.boundingLines.filter { !it.isDrawn }
            if (lines.size == 1){
                lines[0].drawLine()
                lineDrawn = true
                break

            }
        }
        if(!lineDrawn) {
            val possibleMoves = game.lines.filter { !it.isDrawn }
            val chosenMove = possibleMoves.random()
            chosenMove.drawLine()
        }
    }
}
