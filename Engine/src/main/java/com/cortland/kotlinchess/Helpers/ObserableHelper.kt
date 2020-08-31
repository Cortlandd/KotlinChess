package com.cortland.kotlinchess.Helpers

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty


/*
* 100% Overkill but i like things to look fancy
* */

/* Example

// 5 = initial value
var x: Int by varWithObservableSetter(5)
       .willSet { println("before") }
       .didSet { println("after") }

// "abc" = initial value
var y: String by varWithObservableSetter("abc")
       .didSet { println("after") }

* */

class VarWithObservableSetter<T, out TWillSet : (() -> Unit)?, out TDidSet : (() -> Unit)?>

internal constructor(val initialValue: T, internal val willSet: TWillSet, internal val didSet: TDidSet) : ObservableProperty<T>(initialValue) {
    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        willSet?.invoke()
        return true
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        didSet?.invoke()
    }
}

fun <T> varWithObservableSetter(initialValue: T) =
    VarWithObservableSetter(initialValue, null, null)

fun <T, R : (() -> Unit)?>
        VarWithObservableSetter<T, Nothing?, R>.willSet(action: () -> Unit) =
    VarWithObservableSetter(initialValue, action, didSet)

fun <T, R : (() -> Unit)?>
        VarWithObservableSetter<T, R, Nothing?>.didSet(action: () -> Unit) =
    VarWithObservableSetter(initialValue, willSet, action)
