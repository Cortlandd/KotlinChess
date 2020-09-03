package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color


class BoardRaterBoardDominance(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {

        val squareValue: Double = 1.toDouble()

        var rating: Double = 0.toDouble()

        val boardLocations =
            BoardLocation.all()

        // Check this color pieces
        for (sourcelocation in boardLocations) {

            val piece = board.getPiece(sourcelocation) ?: continue

            for (targetLocation in boardLocations) {
                if (piece.movement.canPieceMove(sourcelocation, targetLocation, board)) {
                    rating += if (piece.color == color) squareValue else squareValue.unaryMinus()
                }
            }

        }

        return rating * configuration.boardRaterBoardDominanceWeighting.value
    }

}