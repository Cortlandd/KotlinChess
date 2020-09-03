package com.cortland.kotlinchess

enum class Color(val colorName: String) {
    white("White"),
    black("White");

    fun opposite(): Color {
        return if (this == white) black else white
    }

    var string = {
        this.colorName.toLowerCase()
    }

    var stringWithCapital = {
        this.colorName
    }

}

data class Piece(var type: PieceType, var color: Color, var tag: Int = 0) {

    public enum class PieceType {
        pawn,
        rook,
        knight,
        bishop,
        queen,
        king;

        fun value(): Double {
            return when(this) {
                pawn -> 1.toDouble()
                rook -> 5.toDouble()
                knight -> 3.toDouble()
                bishop -> 3.toDouble()
                queen -> 9.toDouble()
                king -> 0.toDouble()
            }
        }

        companion object {
            fun possiblePawnPromotionResultingTypes(): ArrayList<PieceType> {
                return arrayListOf(queen, knight, rook, bishop)
            }
        }

    }

    var hasMoved = false
        get() = field
        set(value) {
            field = value
        }

    var canBeTakenByEnPassant = false
        get() = field
        set(value) {
            field = value
        }

    var location = BoardLocation(0)

    fun withOppositeColor(): Piece {
        return Piece(type, color.opposite())
    }

    val movement: PieceMovement = PieceMovement.pieceMovement(this.type)

    var value = type.value()

    fun byChangingType(newType: PieceType): Piece {
        return Piece(newType, color, tag)
    }

    fun isSameTypeAndColor(other: Piece): Boolean {
        return this.type == other.type && this.color == other.color
    }

    override fun equals(other: Any?): Boolean {
        val left = this
        val right = (other as Piece)
        return left.type == right.type
                && left.color == right.color
                && left.tag == right.tag
                && left.hasMoved == right.hasMoved
                && left.canBeTakenByEnPassant == right.canBeTakenByEnPassant
                && left.location == right.location

        //return super.equals(other)
    }

}