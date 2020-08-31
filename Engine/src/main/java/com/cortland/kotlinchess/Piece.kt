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
    black;

    fun opposite(): Color {
        return if (this == white) black else white
    }
}

class Piece(var type: PieceType, var color: Color) {

    val movement: PieceMovement by lazy { PieceMovement.pieceMovement(this.type) }

    override fun equals(other: Any?): Boolean {
        val left = this
        val right = (other as Piece)
        return left.type == right.type && left.color == right.color

        //return super.equals(other)
    }

}