package com.cortland.kotlinchess.AI

import com.cortland.kotlinchess.Board
import com.cortland.kotlinchess.Color

open class BoardRater {

    val configuration: AIConfiguration

    constructor(configuration: AIConfiguration) {
        this.configuration = configuration
    }

    open fun ratingFor(board: Board, color: Color): Double {
        throw Exception("Override ratingFor method in subclasses")
    }

}