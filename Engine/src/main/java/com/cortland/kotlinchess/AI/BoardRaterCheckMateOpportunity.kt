package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color

class BoardRaterCheckMateOpportunity(configuration: AIConfiguration) : BoardRater(configuration) {


    override fun ratingFor(board: Board, color: Color): Double {

        val value = 1.toDouble()
        var rating = 0.toDouble()

        for ((index, square) in board.squares.withIndex()) {

            val piece = square.piece ?: continue

            for (location in BoardLocation.all()) {

                val sourceLocation = BoardLocation(index)

                if (piece.movement.canPieceMove(sourceLocation, location, board)) {

                    var movedBoard = board
                    movedBoard.movePiece(sourceLocation, location)

                    if (piece.color == color && movedBoard.isColorInCheckMate(color.opposite)) {
                        rating += value
                    } else if (piece.color == color.opposite && movedBoard.isColorInCheckMate(color)) {
                        rating -= value
                    }
                }
            }
        }

        return rating * configuration.boardRaterCheckMateOpportunityWeighting.value
    }

}