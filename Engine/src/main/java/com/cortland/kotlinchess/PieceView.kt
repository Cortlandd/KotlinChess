package com.cortland.kotlinchess

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import kotlin.properties.Delegates

class PieceView(piece: Piece, var location: BoardLocation, var context: Context) {

    var piece: Piece by Delegates.observable(piece) { _, _, _ ->
        updateImage()
    }

    init {
        updateImage()
    }

    var mPieceImage: Drawable? = null
    var pieceSelected: Boolean = false
        set(value) {
            field = value
            updateImage()
        }

    fun updateImage() {

        var imageName: Int

        imageName =
            when(Pair(piece.type, piece.color)) {
                Pair(PieceType.rook, Color.white) -> R.drawable.whiterook
                Pair(PieceType.pawn, Color.white) -> R.drawable.whitepawn
                Pair(PieceType.knight, Color.white) -> R.drawable.whiteknight
                Pair(PieceType.king, Color.white) -> R.drawable.whiteking
                Pair(PieceType.queen, Color.white) -> R.drawable.whitequeen
                Pair(PieceType.bishop, Color.white) -> R.drawable.whitebishop
                Pair(PieceType.rook, Color.black) -> R.drawable.blackrook
                Pair(PieceType.pawn, Color.black) -> R.drawable.blackpawn
                Pair(PieceType.knight, Color.black) -> R.drawable.blackknight
                Pair(PieceType.king, Color.black) -> R.drawable.blackking
                Pair(PieceType.queen, Color.black) -> R.drawable.blackqueen
                Pair(PieceType.bishop, Color.black) -> R.drawable.blackbishop
            else -> return
        }

        mPieceImage = context.getDrawable(imageName)

        val backgroundColor = if (pieceSelected) android.graphics.Color.RED else android.graphics.Color.TRANSPARENT
        this.mPieceImage!!.setTintMode(PorterDuff.Mode.SCREEN)
        this.mPieceImage!!.setTint(backgroundColor)

    }

}