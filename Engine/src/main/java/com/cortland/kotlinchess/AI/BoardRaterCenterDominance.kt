package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color
import kotlin.math.sqrt

class BoardRaterCenterDominance(configuration: AIConfiguration) : BoardRater(configuration) {

    override fun ratingFor(board: Board, color: Color): Double {

        var rating = 0.toDouble()

        for (sourceLocation in BoardLocation.all()) {
            val piece = board.getPiece(sourceLocation) ?: continue

            for (targetLocation in BoardLocation.all()) {

                if (sourceLocation == targetLocation || piece.movement.canPieceMove(sourceLocation, targetLocation, board)) {
                    val value = dominanceValueFor(targetLocation)
                    rating += if (piece.color == color) value else value.unaryMinus()
                }

            }

        }
        
        return rating * configuration.boardRaterCenterDominanceWeighting.value

    }

    fun dominanceValueFor(location: BoardLocation): Double {

        val axisMiddle = 3.5

        val x = location.x.toDouble()
        val y = location.y.toDouble()

        val xDiff = Math.abs(axisMiddle - x)
        val yDiff = Math.abs(axisMiddle - y)

        val distance = sqrt((xDiff*xDiff)+(yDiff*yDiff))
        return (axisMiddle - distance).toDouble()
    }

}