package com.cortland.kotlinchess

enum class PieceType {
    pawn,
    rook,
    knight,
    bishop,
    queen,
    king
}

enum class Color {
    white,
    black
}

class Piece(type: PieceType, color: Color) {

    var type: PieceType
    var color: Color

    init {
        this.type = type
        this.color = color
    }


}