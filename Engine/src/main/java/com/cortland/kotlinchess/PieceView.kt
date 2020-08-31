package com.cortland.kotlinchess

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.cortland.kotlinchess.Helpers.didSet
import com.cortland.kotlinchess.Helpers.varWithObservableSetter
import com.cortland.kotlinchess.Helpers.willSet

class PieceView @JvmOverloads constructor(context: Context): View(context) {

    lateinit var piece: Piece
    lateinit var location: BoardLocation

    constructor(piece: Piece, location: BoardLocation, context: Context): this(context) {
        this.piece = piece
        this.location = location

        updateImage()
    }

    var mPieceImage: Drawable? = null
    var pieceSelected: Boolean by varWithObservableSetter(false).willSet { updateImage() }

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