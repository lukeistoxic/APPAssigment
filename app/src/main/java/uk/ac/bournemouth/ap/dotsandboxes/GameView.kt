package uk.ac.bournemouth.ap.dotsandboxes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class GameView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
                                                                                   )

    //Declaring Colour Variables
    private var mBackgroundColour: Paint
    private var mDotPaint: Paint
    private var mLinePaint: Paint
    private var mPlayerPaint: Paint
    private var mComputerPaint: Paint

    //Declaring Circle Grid Variables
    private val radius = 10
    private val diameter = radius * 2
    private val padding = 200


    //Colours for the shapes to be displayed on Canvas assigned here
    init {
        mBackgroundColour = Paint().apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#ffdbba")
        }
        mDotPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.GRAY
        }
        mLinePaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.GRAY
        }
        mPlayerPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.RED
        }
        mComputerPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.YELLOW
        }
    }

    //Drawing a grid of Circles, that connect the Lines.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidth: Float = width.toFloat()
        val viewHeight: Float = height.toFloat()
        canvas.drawRect(0F, 0F, viewWidth, viewHeight, mBackgroundColour)

        for (xValue in 1 until 6) {
            for (yValue in 1 until 6) {
                //circle (dots)'s placements
                val cx = diameter * xValue + radius + (padding * xValue)
                val cy = diameter * yValue + radius + (padding * yValue)
                //line's placements

                canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), mDotPaint)
            }
        }

        //Horizontal Lines
        for (yValue in 0 until 5) {
            val cy = diameter * yValue + radius + (padding * yValue)
            for (xValue in 1 until 4) {
                val cx = diameter * xValue + radius + (padding * xValue)
                canvas.drawLine(cx.toFloat(),
                                padding.toFloat()+cy,
                                cx.toFloat() + padding,
                                padding.toFloat()+ cy,
                                mLinePaint)
            }
        }

        //Vertical Lines
        for (yValue in 1 until 5) {
            val cy = diameter * yValue + radius + (padding * yValue)
            for (xValue in 0 until 5) {
                val cx = diameter * xValue + radius + (padding * xValue)
                canvas.drawLine(padding.toFloat()+cx,
                                       cy.toFloat(),
                                padding.toFloat()+cx ,
                                cy.toFloat() + padding,
                                mLinePaint)
            }
        }
    }
}

