package com.dafay.demo.zoom.imageview

import android.graphics.Matrix


private val matrixValues = FloatArray(9)


/**
 * @Des
 * @Author m1studio
 * @Date 2024/4/25
 * <a href=" ">相关链接</a>
 */
fun Matrix.scaleX():Float{
    this.getValues(matrixValues)
    return matrixValues[Matrix.MSCALE_X]
}