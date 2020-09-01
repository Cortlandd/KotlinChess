package com.cortland.kotlinchess

data class BoardOperation(internal val type: OperationType, internal val piece: Piece, internal val location: BoardLocation) {

    enum class OperationType {
        movePiece,
        removePiece,
        transformPiece;
    }

}