package com.cortland.kotlinchess.Helpers

import com.cortland.kotlinchess.BoardOperation

public operator fun Array<BoardOperation>.plus(elements: Array<BoardOperation?>): Array<BoardOperation?> {
    val thisSize = size
    val arraySize = elements.size
    val result = this.copyOf(thisSize + arraySize)
    System.arraycopy(elements, 0, result, thisSize, arraySize)
    return result
}
