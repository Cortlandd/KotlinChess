package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color
import kotlin.math.abs
import kotlin.math.sqrt

class BoardRaterCenterOwnership(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {
        var rating: Double = 0.toDouble()

        for (location in BoardLocation.all()) {

            val piece = board.getPiece(location) ?: continue

            val distance = dominanceValueFor(location)

            rating += if (piece.color == color) distance else distance.unaryMinus()
        }

        return rating * configuration.boardRaterCenterOwnershipWeighting.value
    }

    fun dominanceValueFor(location: BoardLocation): Double {

        val axisMiddle = 3.5

        val x: Double = location.x.toDouble()
        val y = location.y.toDouble()

        val xDiff = abs(axisMiddle - x)
        val yDiff = abs(axisMiddle - y)

        val distance = sqrt((xDiff*xDiff)+(yDiff*yDiff))
        return (axisMiddle - distance).toDouble()
    }

}