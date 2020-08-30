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

class Piece(var type: PieceType, var color: Color)