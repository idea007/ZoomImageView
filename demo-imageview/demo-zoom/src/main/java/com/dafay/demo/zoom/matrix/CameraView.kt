package com.dafay.demo.zoom.matrix

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.dafay.demo.lib.base.utils.dp2px

/**
 * TODO tagbyli
 * < a herf="https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B07%5DPath_Over.md">>
 *     <a herf="https://blog.csdn.net/u013651026/article/details/79151600></a>
 */
class CameraView : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    // 画笔
    private var mPaint: Paint? = null        //画笔


    // View 宽高
    private var mViewWidth = 0f
    private var mViewHeight = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    private var roundSize = 0f

    private var rectFBg:RectF?=null

    private var mCamera: Camera? = null
    private var mMatrix: Matrix? = null
    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.parseColor("#a29fbf")
        mPaint!!.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w.toFloat()
        mViewHeight = h.toFloat()

        mCenterX = mViewWidth / 2
        mCenterY = mViewHeight / 2

        mCamera = Camera()
        mMatrix = Matrix()
        roundSize= 5.dp2px.toFloat()

        rectFBg= RectF(mViewWidth/4,mViewHeight*5/16,mViewWidth*3/4,mViewHeight*11/16)


    }


    private var rotateXDegree = 0f
    private var rotateYDegree = 0f




    //绕X轴旋转
    fun setRotateXDegree(rotate:Float){
        rotateXDegree=rotate
        invalidate()
    }

    //绕Y轴旋转
    fun setRotateYDegree(rotate:Float){
        rotateYDegree=rotate
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        var  camera= mCamera ?: return
        camera.save()
        camera.rotateY(-rotateXDegree)
        camera.rotateX(-rotateYDegree)
        camera.getMatrix(mMatrix)
        camera.restore()

        //改变旋转的中心点
        mMatrix?.preTranslate(-mViewWidth/2, -mViewHeight/2)
        mMatrix?.postTranslate(mViewWidth/2, mViewHeight/2)


//        canvas.matrix=mMatrix


//        canvas.drawCircle(mCenterX,mCenterY,mCenterX/2,mPaint);


        mPaint?.let { rectFBg?.let { it1 -> canvas.drawOval(it1, it) } }

        canvas.restore()


    }


}
