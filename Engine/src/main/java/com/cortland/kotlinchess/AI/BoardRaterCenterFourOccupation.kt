package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color

class BoardRaterCenterFourOccupation(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {

        val value = 1.toDouble()
        var rating = 0.toDouble()

        val locations = listOf(
            BoardLocation(4, 4), // NE
            BoardLocation(4, 3), // SE
            BoardLocation(3, 3), // SW
            BoardLocation(3, 4)  // NW
        )

        for (location in locations) {
            val piece = board.getPiece(location) ?: continue
            rating += if (piece.color == color) value else value.unaryMinus()
        }

        return rating * configuration.boardRaterCenterFourOccupationWeighting.value
    }

}