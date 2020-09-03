package com.cortland.kotlinchess

class CastleMove {
    var yPos: Int = 0
    var kingStartXPos: Int = 0
    var rookStartXPos: Int = 0
    var kingEndXPos: Int = 0
    var rookEndXPos: Int = 0

    fun kingStartLocation(): BoardLocation {
        return BoardLocation(kingStartXPos, yPos)
    }

    fun kingEndLocation(): BoardLocation {
        return BoardLocation(kingEndXPos, yPos)
    }

    fun rookStartLocation(): BoardLocation {
        return BoardLocation(rookStartXPos, yPos)
    }

    fun rookEndLocation(): BoardLocation {
        return BoardLocation(rookEndXPos, yPos)
    }

    constructor(color: Color, side: CastleSide) {
        when(Pair(color, side)) {
            Pair(Color.white, CastleSide.kingSide) -> {
                yPos = 0
                kingStartXPos = 4
                rookStartXPos = 7
                kingEndXPos = 6
                rookEndXPos = 5
            }
            Pair(Color.white, CastleSide.queenSide) -> {
                yPos = 0
                kingStartXPos = 4
                rookStartXPos = 0
                kingEndXPos = 2
                rookEndXPos = 3
            }
            Pair(Color.black, CastleSide.kingSide) -> {
                yPos = 7
                kingStartXPos = 4
                rookStartXPos = 7
                kingEndXPos = 6
                rookEndXPos = 5
            }
            Pair(Color.black, CastleSide.queenSide) -> {
                yPos = 7
                kingStartXPos = 4
                rookStartXPos = 0
                kingEndXPos = 2
                rookEndXPos = 3
            }
        }
    }
}