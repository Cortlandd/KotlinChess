package com.cortland.kotlinchess.Interfaces

import com.cortland.kotlinchess.*

interface GameListener {
    fun gameDidChangeCurrentPlayer(game: Game)
    fun gameWonByPlayer(game: Game, player: Player)
    fun gameEndedInStaleMate(game: Game)

    fun gameWillBeginUpdates(game: Game) // Updates will begin
    fun gameDidAddPiece(game: Game) // A new piece was added to the board (do we catually need to include this funtionality?)
    fun gameDidRemovePiece(game: Game, piece: Piece, location: BoardLocation) // A piece was removed from the board
    fun gameDidMovePiece(game: Game, piece: Piece, toLocation: BoardLocation) // A piece was moved on the board
    fun gameDidTransformPiece(game: Game, piece: Piece, toLocation: BoardLocation) // A piece was transformed (eg. pawn was promoted to another piece)
    fun gameDidEndUpdates(game: Game) // Updates will end

    // Callbacks from player
    fun promotedTypeForPawn(location: BoardLocation, player: Human, possiblePromotions: ArrayList<Piece.PieceType>,callback: (Piece.PieceType) -> Unit)
}