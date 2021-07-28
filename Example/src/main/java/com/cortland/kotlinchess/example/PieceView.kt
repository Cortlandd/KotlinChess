package com.cortland.kotlinchess.example

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import com.cortland.kotlinchess.BoardLocation
import com.cortland.kotlinchess.Color
import com.cortland.kotlinchess.Piece
import kotlin.properties.Delegates
import com.cortland.kotlinchess.Piece.PieceType.*
import com.cortland.kotlinchess.R

class PieceView(piece: Piece, var location: BoardLocation, context: Context): View(context) {

    var piece: Piece by Delegates.observable(piece) { _, _, _ ->
        updateImage()
    }

    var hasBeenUpdated = false

    var mPieceImage: Drawable? = null
    var pieceSelected: Boolean = false
        set(value) {
            field = value
            updateImage()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        updateImage()
        if (mPieceImage != null) {
            if (canvas != null) {
                mPieceImage!!.bounds = this.clipBounds
                mPieceImage!!.draw(canvas)
            }
        }
    }

    fun updateImage() {

        var imageName: Int

        imageName =
            when(Pair(piece.type, piece.color)) {
                Pair(rook,
                    Color.white
                ) -> R.drawable.whiterook
                Pair(pawn,
                    Color.white
                ) -> R.drawable.whitepawn
                Pair(knight,
                    Color.white
                ) -> R.drawable.whiteknight
                Pair(king,
                    Color.white
                ) -> R.drawable.whiteking
                Pair(queen,
                    Color.white
                ) -> R.drawable.whitequeen
                Pair(bishop,
                    Color.white
                ) -> R.drawable.whitebishop
                Pair(rook,
                    Color.black
                ) -> R.drawable.blackrook
                Pair(pawn,
                    Color.black
                ) -> R.drawable.blackpawn
                Pair(knight,
                    Color.black
                ) -> R.drawable.blackknight
                Pair(king,
                    Color.black
                ) -> R.drawable.blackking
                Pair(queen,
                    Color.black
                ) -> R.drawable.blackqueen
                Pair(bishop,
                    Color.black
                ) -> R.drawable.blackbishop
            else -> return
        }

        mPieceImage = this.context.getDrawable(imageName)

        val backgroundColor = if (pieceSelected) android.graphics.Color.RED else android.graphics.Color.TRANSPARENT
        this.mPieceImage!!.setTintMode(PorterDuff.Mode.SCREEN)
        this.mPieceImage!!.setTint(backgroundColor)


    }

}