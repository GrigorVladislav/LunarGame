package com.example.lunargame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import stanford.androidlib.graphics.*
import java.util.jar.Attributes

class Canvas(context: Context, attrs: AttributeSet) : GCanvas(context, attrs)
{
    companion object {
        private const val FRAMES_PER_SECOND = 30
        private const val MAX_SAFE_LANDING_VELOCITY = 9.0f
        private const val MAX_ALTITUDE = -1000f
        private const val GRAVITY_ACCELERATION = .5f
        private const val THRUST_ACCELERATION = -.3f
    }

    private var crushFrames = 0
    private lateinit var rocketImage: Bitmap
    private var rocketImageThrusts = ArrayList<Bitmap>()
    private var rocketCrushImages = ArrayList<Bitmap>()
    private lateinit var rocket: GSprite
    private lateinit var moonSurface: GSprite
    private lateinit var label: GLabel

    private var isGameOver = false
    private var isRocketDestroyed = false

    override fun init() {

        backgroundColor = GColor.BLACK
        loadSurfaceImage()
        loadRocketImage()
        loadRocketThrusts()
        loadRocketCrushImage()


        setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true        }
    }
    private fun loadSurfaceImage() {
        var moonSurfaceImage = BitmapFactory.decodeResource(resources, R.drawable.moonsurface)
        moonSurfaceImage = moonSurfaceImage.scaleToWidth(this.width.toFloat())

        moonSurface = GSprite(moonSurfaceImage)
        moonSurface.bottomY = this.height.toFloat()
        moonSurface.collisionMarginTop = moonSurface.height / 3
        add(moonSurface)
    }

    private fun loadRocketImage() {
        rocketImage = BitmapFactory.decodeResource(resources, R.drawable.rocket1)
        rocketImage = rocketImage.scaleToWidth(this.width / 10f)

        rocket = GSprite(rocketImage)
        rocket.rightX = this.width.toFloat() / 2
        rocket.velocityY = 10f

        rocket.accelerationY = GRAVITY_ACCELERATION
        add(rocket)


    }

    private fun loadRocketThrusts() {
        val rocketImageThrust1 = BitmapFactory.decodeResource(resources, R.drawable.rocket1)
        val rocketImageThrust2 = BitmapFactory.decodeResource(resources, R.drawable.rocket2)
        val rocketImageThrust3 = BitmapFactory.decodeResource(resources, R.drawable.rocket3)
        val rocketImageThrust4 = BitmapFactory.decodeResource(resources, R.drawable.rocket4)
        val rocketImageThrust5 = BitmapFactory.decodeResource(resources, R.drawable.rocket5)
        val rocketImageThrust6 = BitmapFactory.decodeResource(resources, R.drawable.rocket6)
        val rocketImageThrust7 = BitmapFactory.decodeResource(resources, R.drawable.rocket7)

        rocketImageThrusts.add(rocketImageThrust1)
        rocketImageThrusts.add(rocketImageThrust2)
        rocketImageThrusts.add(rocketImageThrust3)
        rocketImageThrusts.add(rocketImageThrust4)
        rocketImageThrusts.add(rocketImageThrust5)
        rocketImageThrusts.add(rocketImageThrust6)
        rocketImageThrusts.add(rocketImageThrust7)


        for(i in rocketImageThrusts.indices) {
            rocketImageThrusts[i] = rocketImageThrusts[i].scaleToWidth(this.width / 10f)
        }
    }

    private fun loadRocketCrushImage() {
        val rocketCrushImage1 = BitmapFactory.decodeResource(resources, R.drawable.crush1)
        val rocketCrushImage2  = BitmapFactory.decodeResource(resources, R.drawable.crush2)
        val rocketCrushImage3 = BitmapFactory.decodeResource(resources, R.drawable.crush3)
        val rocketCrushImage4 = BitmapFactory.decodeResource(resources,R.drawable.rip)
        val rocketCrushImage5 = BitmapFactory.decodeResource(resources,R.drawable.rip)
        val rocketCrushImage6 = BitmapFactory.decodeResource(resources,R.drawable.rip)
        val rocketCrushImage7 = BitmapFactory.decodeResource(resources,R.drawable.rip)

        rocketCrushImages.add(rocketCrushImage1)
        rocketCrushImages.add(rocketCrushImage2)
        rocketCrushImages.add(rocketCrushImage3)
        rocketCrushImages.add(rocketCrushImage4)
        rocketCrushImages.add(rocketCrushImage5)
        rocketCrushImages.add(rocketCrushImage6)
        rocketCrushImages.add(rocketCrushImage7)

        for(i in rocketCrushImages.indices) {
            rocketCrushImages[i] = rocketCrushImages[i].scaleToWidth(this.width / 10f)
        }
    }

    private fun handleTouchEvent(event: MotionEvent) {
        if(!isGameOver) {
            val x = event.x
            val y = event.y
            if (event.action == MotionEvent.ACTION_DOWN) {
                rocket.accelerationY = THRUST_ACCELERATION
                rocket.bitmaps = rocketImageThrusts
                rocket.framesPerBitmap = FRAMES_PER_SECOND / 10
            } else if (event.action == MotionEvent.ACTION_UP) {
                rocket.accelerationY = GRAVITY_ACCELERATION
                rocket.bitmap = rocketImage
            }
        }
    }

    private fun tick() {
        rocket.update()
        doCollisions()
        goAway()

        if(isGameOver && isRocketDestroyed){
            if(crushFrames >= FRAMES_PER_SECOND - 5) {
                animationStop()
                remove(rocket)
            }
            crushFrames++
        }
    }

    private fun displayTheMessage(message: String) {
        label = GLabel(message)
        label.color = GColor.GREEN
        label.fontSize = 150f
        label.rightX = (this.width.toFloat() / 2) + (label.width / 2)
        label.bottomY = this.height.toFloat() / 2
        add(label)
    }

    private fun goAway() {
        if(rocket.y <= MAX_ALTITUDE){
            animationStop()
            isGameOver = false
            displayTheMessage("GAME OVER!!!")
        }
    }

    private fun doCollisions() {
        if(rocket.collidesWith(moonSurface)) {

            if(!isGameOver) {
                if (rocket.velocityY <= MAX_SAFE_LANDING_VELOCITY) {
                    displayTheMessage("YOU WIN!!!")
                } else {
                    rocket.bitmaps = rocketCrushImages
                    rocket.framesPerBitmap = FRAMES_PER_SECOND / 7
                    isRocketDestroyed = true
                    displayTheMessage("GAME OVER!!!")
                }
                isGameOver = true
            }
            rocket.velocityY = 0f
            rocket.accelerationY = 0f
        }
    }

    fun startGame() {
        if(isGameOver)
        {
            animationStop()

            crushFrames = 0

            if(isRocketDestroyed) {
                loadRocketImage()
                isRocketDestroyed = false
            }

            rocket.y = 0f
            rocket.velocityY = 10f

            remove(label)
            isGameOver = false
        }
        else {
            animate(FRAMES_PER_SECOND) {
                tick()
            }
        }
    }

    fun stopGame() {
        animationStop()
    }
}