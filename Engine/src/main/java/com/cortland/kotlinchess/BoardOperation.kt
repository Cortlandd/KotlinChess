package com.cortland.kotlinchess

class BoardOperation {

    internal enum class OperationType {
        movePiece,
        removePiece,
        transformPiece;
    }

    internal var type: OperationType
    internal var piece: Piece
    internal var location: BoardLocation

    internal constructor(type: OperationType, piece: Piece, location: BoardLocation) {
        this.type = type
        this.piece = piece
        this.location = location
    }

}