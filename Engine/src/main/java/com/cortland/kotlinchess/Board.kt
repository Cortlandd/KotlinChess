package com.cortland.kotlinchess

class Square {

    var piece: Piece? = null

}

class Board {

    var squares: ArrayList<Square> = ArrayList()

    init {

        for (i in 0..64) {
            squares.add(Square())
        }

        // Setup white bottom row
        this.squares[0].piece = Piece(type = PieceType.rook, color = Color.white)
        this.squares[1].piece = Piece(type = PieceType.knight, color = Color.white)
        this.squares[2].piece = Piece(type = PieceType.bishop, color = Color.white)
        this.squares[3].piece = Piece(type = PieceType.queen, color = Color.white)
        this.squares[4].piece = Piece(type = PieceType.king, color = Color.white)
        this.squares[5].piece = Piece(type = PieceType.bishop, color = Color.white)
        this.squares[6].piece = Piece(type = PieceType.knight, color = Color.white)
        this.squares[7].piece = Piece(type = PieceType.rook, color = Color.white)

        // Setup white pawn row
        for (i in 8..15) {
            this.squares[i].piece = Piece(type = PieceType.pawn, color = Color.white)
        }

        // Setup black bottom row
        this.squares[63].piece = Piece(type = PieceType.rook, color = Color.black)
        this.squares[62].piece = Piece(type = PieceType.knight, color = Color.black)
        this.squares[61].piece = Piece(type = PieceType.bishop, color = Color.black)
        this.squares[60].piece = Piece(type = PieceType.queen, color = Color.black)
        this.squares[59].piece = Piece(type = PieceType.king, color = Color.black)
        this.squares[58].piece = Piece(type = PieceType.bishop, color = Color.black)
        this.squares[57].piece = Piece(type = PieceType.knight, color = Color.black)
        this.squares[56].piece = Piece(type = PieceType.rook, color = Color.black)

        // Setup black pawn row
        for (i in 48..55) {
            this.squares[i].piece = Piece(type = PieceType.pawn, color = Color.black)
        }
    }

}