package com.cortland.kotlinchess

import com.cortland.kotlinchess.AI.AIPlayer
import com.cortland.kotlinchess.Interfaces.GameListener
import com.cortland.kotlinchess.Interfaces.PlayerListener

class Game: PlayerListener {



    sealed class State {
        object InProgress: State()
        data class StaleMate(val color: Color): State()
        data class Won(val color: Color): State()

        // TODO: Reevaluate this

    }

    enum class GameType {
        humanVsHuman,
        humanVsComputer,
        computerVsComputer
    }

    var board: Board
        internal set
    var whitePlayer: Player
    var blackPlayer: Player
    var currentPlayer: Player
        internal set
    var state: State = State.InProgress
        internal set
    var gameListener: GameListener? = null

    fun gameType(): GameType {
        return when(Pair(whitePlayer, blackPlayer)) {
            Pair(Human::class, Human::class) -> GameType.humanVsHuman
            Pair(AIPlayer::class, AIPlayer::class) -> GameType.computerVsComputer
            else -> GameType.humanVsComputer
        }
    }

    constructor(firstPlayer: Player, secondPlayer: Player, board: Board = Board(Board.InitialState.newGame), colorToMove: Color = Color.white) {

        this.board = board

        // Assign to correct colors
        if (firstPlayer.color == secondPlayer.color) {
            throw Exception("Both players cannot have the same color")
        }

        this.whitePlayer = if (firstPlayer.color == Color.white) firstPlayer else secondPlayer
        this.blackPlayer = if (firstPlayer.color == Color.black) firstPlayer else secondPlayer

        // Setup Players
        this.whitePlayer.playerListener = this
        this.blackPlayer.playerListener = this
        this.whitePlayer.game = this
        this.blackPlayer.game = this
        this.currentPlayer = (if (colorToMove == Color.white) this.whitePlayer else this.blackPlayer)
    }

    override fun playerDidMakeMove(player: Player, boardOperations: ArrayList<BoardOperation>) {

        // This shouldn't happen, but we'll print a message in case it does
        if (player !== currentPlayer) {
            println("Warning - Wrong player took turn")
        }

        this.gameListener?.gameWillBeginUpdates(this)

        // Process board operations
        processBoardOperations(boardOperations)

        // Check for game ended
        if (board.isColorInCheckMate(currentPlayer.color.opposite)) {
            this.state = State.Won(currentPlayer.color)
            gameListener?.gameWonByPlayer(this, currentPlayer)
            return
        }

        // Check for stalemate
        if (board.isColorInStalemate(currentPlayer.color.opposite)) {
            this.state = State.StaleMate(currentPlayer.color.opposite)
            gameListener?.gameEndedInStaleMate(this)
            return
        }

        gameListener?.gameDidEndUpdates(this)

        // Switch to the other player
        if (player === whitePlayer) {
            currentPlayer = blackPlayer
        } else {
            currentPlayer = whitePlayer
        }

        this.gameListener?.gameDidChangeCurrentPlayer(this)

    }

    fun processBoardOperations(boardOperations: ArrayList<BoardOperation>) {

        for (boardOperation in boardOperations) {
            when(boardOperation.type) {
                BoardOperation.OperationType.movePiece -> this.gameListener?.gameDidMovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.removePiece -> this.gameListener?.gameDidRemovePiece(this, boardOperation.piece, boardOperation.location)
                BoardOperation.OperationType.transformPiece -> this.gameListener?.gameDidTransformPiece(this, boardOperation.piece, boardOperation.location)
            }

        }
    }

    override fun equals(other: Any?): Boolean {
        var lhs = this
        var rhs = (other as Game)

        fun arePlayersEqual(p1: Player, p2: Player): Boolean {
            val h1 = p1 as? Human
            val h2 = p2 as? Human
            val ai1 = p1 as? AIPlayer
            val ai2 = p2 as? AIPlayer

            return if (p1 == h1 && p2 == h2) {
                h1 == h2
            } else if (p1 == ai1 && p2 == ai2) {
                ai1 == ai2
            } else {
                false
            }
        }

        if (arePlayersEqual(lhs.whitePlayer, rhs.whitePlayer)
            && arePlayersEqual(lhs.blackPlayer, rhs.blackPlayer)
            && arePlayersEqual(lhs.currentPlayer, rhs.currentPlayer)
            && lhs.board == rhs.board
                && lhs.state == rhs.state) {
            return true
        } else {
            return false
        }

    }

}