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

    val movement: PieceMovement = PieceMovement.pieceMovement(this.type)

    fun value(): Float {

        when(type) {
            PieceType.pawn -> return 1f
            PieceType.rook-> return 2f
            PieceType.knight-> return 3f
            PieceType.bishop-> return 4f
            PieceType.queen-> return 5f
            PieceType.king-> return 6f
        }
    }

    override fun equals(other: Any?): Boolean {
        val left = this
        val right = (other as Piece)
        return left.type == right.type && left.color == right.color

        //return super.equals(other)
    }

}