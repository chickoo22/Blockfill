package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.concurrent.thread
import kotlin.math.sin

class SoundHapticsManager(context: Context) {
    private val appContext = context.applicationContext

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var hapticsEnabled: Boolean = true
    var soundEnabled: Boolean = true

    fun playStepHaptic() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(18)
            }
        } catch (_: Exception) {}
    }

    fun playBacktrackHaptic() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    fun playBlockedHaptic() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    fun playWinHaptic() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 60, 60, 90, 60, 150)
                val amplitudes = intArrayOf(0, 180, 0, 220, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 70, 70, 120), -1)
            }
        } catch (_: Exception) {}
    }

    fun playTone(freqHz: Float, durationMs: Int = 80) {
        if (!soundEnabled) return
        thread(start = true, isDaemon = true) {
            try {
                val sampleRate = 22050
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i * freqHz / sampleRate
                    // Smooth envelope to prevent audio clicking
                    val envelope = when {
                        i < sampleRate * 0.01 -> i / (sampleRate * 0.01)
                        i > numSamples - sampleRate * 0.02 -> (numSamples - i) / (sampleRate * 0.02)
                        else -> 1.0
                    }
                    buffer[i] = (sin(angle) * 25000.0 * envelope).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                Thread.sleep(durationMs.toLong() + 15)
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }

    fun playStepSound(stepIndex: Int) {
        val baseFreq = 330.0f
        val pentatonicRatios = listOf(1.0f, 1.125f, 1.25f, 1.5f, 1.666f, 2.0f, 2.25f, 2.5f)
        val ratio = pentatonicRatios[stepIndex % pentatonicRatios.size]
        val octaveMultiplier = 1.0f + (stepIndex / pentatonicRatios.size) * 0.35f
        playTone(baseFreq * ratio * octaveMultiplier, 65)
    }

    fun playUndoSound() {
        if (!soundEnabled) return
        thread(start = true, isDaemon = true) {
            playTone(440f, 45)
            try { Thread.sleep(40) } catch (_: Exception) {}
            playTone(330f, 55)
        }
    }

    fun playResetSound() {
        if (!soundEnabled) return
        thread(start = true, isDaemon = true) {
            playTone(420f, 50)
            try { Thread.sleep(45) } catch (_: Exception) {}
            playTone(280f, 70)
        }
    }

    fun playHintSound() {
        if (!soundEnabled) return
        thread(start = true, isDaemon = true) {
            val notes = listOf(659.25f, 880.0f, 1318.5f) // E5, A5, E6
            for (note in notes) {
                playTone(note, 70)
                try { Thread.sleep(60) } catch (_: Exception) {}
            }
        }
    }

    fun playButtonSound() {
        playTone(520f, 35)
    }

    fun playBlockedSound() {
        playTone(180f, 60)
    }

    fun playPurchaseSound() {
        if (!soundEnabled) return
        thread(start = true, isDaemon = true) {
            playTone(587.33f, 80) // D5
            try { Thread.sleep(70) } catch (_: Exception) {}
            playTone(880.0f, 120) // A5
        }
    }

    fun playWinSound() {
        thread(start = true, isDaemon = true) {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f) // C5, E5, G5, C6
            for (note in notes) {
                playTone(note, 110)
                try { Thread.sleep(85) } catch (_: Exception) {}
            }
        }
    }
}
