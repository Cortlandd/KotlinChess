# KotlinChess

[![License](https://img.shields.io/cocoapods/l/SwiftChess.svg?style=flat)](http://github.com/Cortlandd/KotlinChess)

Kotlin Chess is a Chess Engine and Game written in Kotlin for Android. It is  largely based on the iOS counterpart  _[SwiftChess](https://github.com/SteveBarnegren/SwiftChess/)_

99.9 percent line by line Swift to Kotlin

# Installation
```kotlin
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

```kotlin
dependencies {
  implementation 'com.github.Cortlandd:KotlinChess:1.0'
}
```

# Known Issues
- AIPlayer won't move. Can't ever find king. did some investigating and found that the board switches to black player, some pieces are removed. For example, in debugger, once it was black turn, i looked at the board and its indexes and some were null or issues like the king being set as a pawn.

- Player.kt#_board.movePiece(fromLocation, toLocation)_ is causing player to not be able to move. So i commented it out and its everything is working. i commented the same thing out on ios to test and same result.
