package com.odev.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ShakeDetector(context: Context, private val onShake: () -> Unit) : SensorEventListener {

    private val threshold = 15f // Bu değeri ihtiyaca göre ayarlayabilirsiniz.
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastTime: Long = 0
    private var lastAccel: FloatArray? = FloatArray(3)

    init {
        lastAccel?.fill(0f)
        registerListener()
    }

    private fun registerListener() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - lastTime

        if (timeDifference > 100) { // 100 milisaniye içinde birden fazla algılama yapılmasını önlemek için
            lastTime = currentTime
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val accel = FloatArray(3)
            lastAccel?.let {
                accel[0] = x - it[0]
                accel[1] = y - it[1]
                accel[2] = z - it[2]
            }

            val speed = Math.sqrt((accel[0] * accel[0] + accel[1] * accel[1] + accel[2] * accel[2]).toDouble())
            if (speed > threshold) {
                onShake.invoke()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Sensör hassasiyeti değiştiğinde burada işlem yapabilirsiniz.
    }

    fun startListening() {
        registerListener()
    }

    fun stopListening() {
        unregisterListener()
    }
}