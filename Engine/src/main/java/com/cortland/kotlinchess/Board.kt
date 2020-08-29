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

    fun printBoardColors() {

        printBoard { square ->
            square.piece.let { piece ->
                if (piece?.color == Color.white) 'W' else 'B'
            }
        }

    }

    fun printBoardPieces() {

        printBoard { square ->
            var character: Char? = null

            val piece = square.piece

            when (piece?.type) {
                PieceType.pawn -> character = 'P'
                PieceType.rook -> character = 'R'
                PieceType.knight -> character = 'N'
                PieceType.bishop -> character = 'B'
                PieceType.queen -> character = 'Q'
                PieceType.king -> character = 'K'
            }

            character
        }
    }

    fun printBoardState() {

        printBoard { square ->
            var character: Char? = null

            var piece = square.piece

            when(piece?.type) {
                PieceType.pawn -> {
                    character = if (piece.color == Color.white) 'P' else 'p'
                }
                PieceType.rook -> {
                    character = if (piece.color == Color.white) 'R' else 'r'
                }
                PieceType.knight -> {
                    character = if (piece.color == Color.white) 'N' else 'n'
                }
                PieceType.bishop -> {
                    character = if (piece.color == Color.white) 'B' else 'b'
                }
                PieceType.queen -> {
                    character = if (piece.color == Color.white) 'Q' else 'q'
                }
                PieceType.king -> {
                    character = if (piece.color == Color.white) 'K' else 'k'
                }
                null -> {
                    character = null
                }
            }

            character
        }

    }



    fun printBoard(squarePrinter: (Square) -> Char?) {

        var printString = StringBuilder()

        for (y in (0..7).reversed()) {
            for (x in 0..7) {
                val index = y * 8 + x
                val character = squarePrinter(squares[index])
                printString.append(character ?: "-")
            }

            printString.append("\n")
        }

        println(printString)
    }

}