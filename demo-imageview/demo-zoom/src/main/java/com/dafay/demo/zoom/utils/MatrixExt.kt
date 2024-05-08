package com.dafay.demo.zoom.utils

import android.graphics.Matrix

fun Matrix.toPrint(): String {
    val values = FloatArray(9)
    this.getValues(values)
    return "Matrix:\n" +
            "[${values[0]},${values[1]},${values[2]}]\n" +
            "[${values[3]},${values[4]},${values[5]}]\n" +
            "[${values[6]},${values[7]},${values[8]}]"

}

fun Matrix.transX(): Float {
    val values = FloatArray(9)
    this.getValues(values)
    return values[Matrix.MTRANS_X]
}

fun Matrix.transY(): Float {
    val values = FloatArray(9)
    this.getValues(values)
    return values[Matrix.MTRANS_Y]
}

