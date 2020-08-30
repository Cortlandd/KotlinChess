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

class Piece(var type: PieceType, var color: Color)