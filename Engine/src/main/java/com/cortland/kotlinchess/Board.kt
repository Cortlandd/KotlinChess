package com.cortland.kotlinchess

class Square {

    var piece: Piece? = null

}

class Board {

    enum class InitialState {
        empty, newGame
    }

    constructor(state: InitialState) {
        // Setup squares
        (0..63).forEach {
            squares.add(Square())
        }

        // Setup for new game?
        if (state == InitialState.newGame) {
            setupForNewGame()
        }
    }

    var squares: ArrayList<Square> = ArrayList()

    fun setupForNewGame() {

        // Setup white bottom row
        squares[0].piece = Piece(type = PieceType.rook, color = Color.white)
        squares[1].piece = Piece(type = PieceType.knight, color = Color.white)
        squares[2].piece = Piece(type = PieceType.bishop, color = Color.white)
        squares[3].piece = Piece(type = PieceType.queen, color = Color.white)
        squares[4].piece = Piece(type = PieceType.king, color = Color.white)
        squares[5].piece = Piece(type = PieceType.bishop, color = Color.white)
        squares[6].piece = Piece(type = PieceType.knight, color = Color.white)
        squares[7].piece = Piece(type = PieceType.rook, color = Color.white)

        // Setup white pawn row
        for (i in 8..15) {
            squares[i].piece = Piece(type = PieceType.pawn, color = Color.white)
        }

        // Setup black bottom row
        squares[63].piece = Piece(type = PieceType.rook, color = Color.black)
        squares[62].piece = Piece(type = PieceType.knight, color = Color.black)
        squares[61].piece = Piece(type = PieceType.bishop, color = Color.black)
        squares[60].piece = Piece(type = PieceType.queen, color = Color.black)
        squares[59].piece = Piece(type = PieceType.king, color = Color.black)
        squares[58].piece = Piece(type = PieceType.bishop, color = Color.black)
        squares[57].piece = Piece(type = PieceType.knight, color = Color.black)
        squares[56].piece = Piece(type = PieceType.rook, color = Color.black)

        // Setup black pawn row
        for (i in 48..55) {
            squares[i].piece = Piece(type = PieceType.pawn, color = Color.black)
        }
    }

    // Pieces

    fun setPiece(piece: Piece, location: BoardLocation) {
        squares[location.index].piece = piece
    }

    fun getPiece(location: BoardLocation): Piece? {
        return squares[location.index].piece
    }

    fun pieceAtIndex(index: Int): Piece? {
        return squares[index].piece
    }

    fun movePiece(fromLocation: BoardLocation, toLocation: BoardLocation) {
        squares[toLocation.index].piece = this.squares[fromLocation.index].piece
        squares[fromLocation.index].piece = null
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