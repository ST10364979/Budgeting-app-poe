package com.example.OPCS_PART3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.example.OPCS_PART3.databinding.ActivitySplashBinding
import kotlin.random.Random

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val animationDuration = 12000L
    private var textUpdateHandler: Handler? = null
    private var textUpdateRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide system UI for immersive experience
        hideSystemUI()

        // Start revolutionary animations
        startRevolutionaryAnimations()

        // Navigate after animation
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, animationDuration)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handlers to prevent memory leaks
        textUpdateHandler?.removeCallbacks(textUpdateRunnable ?: return)
        textUpdateHandler = null
        textUpdateRunnable = null
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    private fun startRevolutionaryAnimations() {
        // Start dynamic background animation
        startDynamicBackgroundAnimation()

        // Animate geometric floating elements
        animateGeometricElements()

        // Animate revolutionary logo with orbital effects
        animateRevolutionaryLogo()

        // Animate premium text with sophisticated effects
        animatePremiumText()

        // Animate enhanced progress bar
        animateEnhancedProgressBar()

        // Start premium sparkle animation
        startPremiumSparkleAnimation()

        // Add dynamic loading text changes (FIXED)
        startDynamicLoadingTextFixed()
    }

    private fun startDynamicBackgroundAnimation() {
        // Animate mesh gradient background
        val meshAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 5000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val fraction = animator.animatedValue as Float
                binding.backgroundMesh.alpha = 0.85f + (fraction * 0.15f)
                binding.waveOverlay.alpha = 0.15f + (fraction * 0.25f)
            }
        }
        meshAnimator.start()

        // Add wave animation effect
        val waveAnimator = ObjectAnimator.ofFloat(binding.waveOverlay, "rotation", 0f, 360f).apply {
            duration = 20000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
        waveAnimator.start()
    }

    private fun animateGeometricElements() {
        // Animate geometric shapes with unique patterns
        val geometricElements = listOf(
            binding.hexagon1,
            binding.triangle1,
            binding.diamond1,
            binding.circle1,
            binding.pentagon1
        )

        geometricElements.forEachIndexed { index, element ->
            // Floating animation
            val translateY = ObjectAnimator.ofFloat(element, "translationY", 0f, -80f, 0f).apply {
                duration = 3000 + (index * 400L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val translateX = ObjectAnimator.ofFloat(element, "translationX", 0f, 50f, 0f).apply {
                duration = 4000 + (index * 500L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            // Unique rotation for each shape
            val rotation = ObjectAnimator.ofFloat(element, "rotation", 0f, 360f).apply {
                duration = when(index) {
                    0 -> 12000L // Hexagon - slow
                    1 -> 8000L  // Triangle - medium
                    2 -> 15000L // Diamond - very slow
                    3 -> 10000L // Circle - medium-slow
                    else -> 6000L // Pentagon - fast
                }
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }

            // Scale pulsing
            val scaleX = ObjectAnimator.ofFloat(element, "scaleX", 1f, 1.3f, 1f).apply {
                duration = 2000 + (index * 300L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleY = ObjectAnimator.ofFloat(element, "scaleY", 1f, 1.3f, 1f).apply {
                duration = 2000 + (index * 300L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(translateY, translateX, rotation, scaleX, scaleY)
                start()
            }
        }
    }

    private fun animateRevolutionaryLogo() {
        // Initial setup - make logo invisible and scaled down
        binding.logoContainer.alpha = 0f
        binding.logoContainer.scaleX = 0.2f
        binding.logoContainer.scaleY = 0.2f

        // Spectacular logo entrance
        val scaleX = ObjectAnimator.ofFloat(binding.logoContainer, "scaleX", 0.2f, 1.4f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.logoContainer, "scaleY", 0.2f, 1.4f, 1f)
        val alpha = ObjectAnimator.ofFloat(binding.logoContainer, "alpha", 0f, 1f)
        val rotation = ObjectAnimator.ofFloat(binding.logoContainer, "rotation", 0f, 720f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha, rotation)
            duration = 2000
            interpolator = BounceInterpolator()
            startDelay = 800
            start()
        }

        // Animate orbital elements
        Handler(Looper.getMainLooper()).postDelayed({
            animateOrbitalElements()
        }, 2500)

        // Add premium pulsing effect
        Handler(Looper.getMainLooper()).postDelayed({
            val pulse = ObjectAnimator.ofFloat(binding.logoContainer, "scaleX", 1f, 1.15f, 1f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            val pulseY = ObjectAnimator.ofFloat(binding.logoContainer, "scaleY", 1f, 1.15f, 1f).apply {
                duration = 1500
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }
            AnimatorSet().apply {
                playTogether(pulse, pulseY)
                start()
            }
        }, 3000)
    }

    private fun animateOrbitalElements() {
        // Animate orbital dots around the logo
        val orbitals = listOf(
            binding.orbital1,
            binding.orbital2,
            binding.orbital3,
            binding.orbital4
        )

        orbitals.forEachIndexed { index, orbital ->
            val rotation = ObjectAnimator.ofFloat(orbital, "rotation", 0f, 360f).apply {
                duration = 4000 + (index * 1000L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = LinearInterpolator()
            }

            val scale = ObjectAnimator.ofFloat(orbital, "scaleX", 1f, 2f, 1f).apply {
                duration = 1000 + (index * 200L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            val scaleY = ObjectAnimator.ofFloat(orbital, "scaleY", 1f, 2f, 1f).apply {
                duration = 1000 + (index * 200L)
                repeatCount = ObjectAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
            }

            AnimatorSet().apply {
                playTogether(rotation, scale, scaleY)
                start()
            }
        }
    }

    private fun animatePremiumText() {
        // Animate app name with spectacular entrance
        binding.appName.alpha = 0f
        binding.appName.translationY = 150f
        binding.appName.scaleX = 0.5f
        binding.appName.scaleY = 0.5f

        val nameAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.appName, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.appName, "translationY", 150f, 0f),
                ObjectAnimator.ofFloat(binding.appName, "scaleX", 0.5f, 1f),
                ObjectAnimator.ofFloat(binding.appName, "scaleY", 0.5f, 1f)
            )
            duration = 1200
            startDelay = 1500
            interpolator = OvershootInterpolator()
        }
        nameAnimatorSet.start()

        // Animate tagline with elegant slide
        binding.tagline.alpha = 0f
        binding.tagline.translationY = 80f

        val taglineAnimatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tagline, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tagline, "translationY", 80f, 0f)
            )
            duration = 1000
            startDelay = 2200
            interpolator = OvershootInterpolator()
        }
        taglineAnimatorSet.start()
    }

    private fun animateEnhancedProgressBar() {
        binding.progressBar.alpha = 0f
        binding.loadingText.alpha = 0f

        // Show progress elements with style
        val progressShowAnimator = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.progressBar, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.loadingText, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.progressBar, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(binding.progressBar, "scaleY", 0.8f, 1f)
            )
            duration = 800
            startDelay = 3000
            interpolator = OvershootInterpolator()
        }
        progressShowAnimator.start()

        // Animate progress with smooth acceleration
        ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 100).apply {
            duration = 6000
            startDelay = 3500
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startPremiumSparkleAnimation() {
        val sparkles = listOf(
            binding.sparkle1, binding.sparkle2, binding.sparkle3,
            binding.sparkle4, binding.sparkle5, binding.sparkle6
        )

        sparkles.forEachIndexed { index, sparkle ->
            val delay = index * 300L + 2000L

            Handler(Looper.getMainLooper()).postDelayed({
                animatePremiumSparkle(sparkle)
            }, delay)
        }
    }

    private fun animatePremiumSparkle(sparkle: View) {
        val translateY = ObjectAnimator.ofFloat(sparkle, "translationY", 0f, -300f)
        val translateX = ObjectAnimator.ofFloat(sparkle, "translationX", 0f, Random.nextInt(-50, 51).toFloat())
        val alpha = ObjectAnimator.ofFloat(sparkle, "alpha", 0f, 1f, 0.8f, 0f)
        val scaleX = ObjectAnimator.ofFloat(sparkle, "scaleX", 0f, 2f, 1f, 0f)
        val scaleY = ObjectAnimator.ofFloat(sparkle, "scaleY", 0f, 2f, 1f, 0f)
        val rotation = ObjectAnimator.ofFloat(sparkle, "rotation", 0f, 720f)

        AnimatorSet().apply {
            playTogether(translateY, translateX, alpha, scaleX, scaleY, rotation)
            duration = 3000
            interpolator = AccelerateDecelerateInterpolator()

            doOnEnd {
                // Reset and repeat with random delay
                sparkle.translationY = 0f
                sparkle.translationX = 0f
                sparkle.alpha = 0f
                sparkle.scaleX = 0f
                sparkle.scaleY = 0f
                sparkle.rotation = 0f

                Handler(Looper.getMainLooper()).postDelayed({
                    animatePremiumSparkle(sparkle)
                }, Random.nextLong(2000, 5000))
            }

            start()
        }
    }

    // FIXED: Dynamic Loading Text with proper bounds checking
    private fun startDynamicLoadingTextFixed() {
        val loadingMessages = arrayOf(
            "💎 Initializing your wealth journey...",
            "🚀 Loading premium features...",
            "✨ Preparing financial insights...",
            "🎯 Setting up your dashboard...",
            "💰 Almost ready to track your success..."
        )

        var currentIndex = 0
        textUpdateHandler = Handler(Looper.getMainLooper())

        textUpdateRunnable = object : Runnable {
            override fun run() {
                // FIXED: Proper bounds checking
                if (currentIndex < loadingMessages.size && !isDestroyed && !isFinishing) {
                    try {
                        // Fade out current text
                        ObjectAnimator.ofFloat(binding.loadingText, "alpha", 1f, 0f).apply {
                            duration = 300
                            doOnEnd {
                                // FIXED: Double check bounds before accessing array
                                if (currentIndex < loadingMessages.size && !isDestroyed && !isFinishing) {
                                    // Change text and fade in
                                    binding.loadingText.text = loadingMessages[currentIndex]
                                    ObjectAnimator.ofFloat(binding.loadingText, "alpha", 0f, 1f).apply {
                                        duration = 300
                                        start()
                                    }
                                }
                            }
                            start()
                        }

                        currentIndex++

                        // FIXED: Only schedule next update if within bounds
                        if (currentIndex < loadingMessages.size && !isDestroyed && !isFinishing) {
                            textUpdateHandler?.postDelayed(this, 2000)
                        }
                    } catch (e: Exception) {
                        // Gracefully handle any animation errors
                        e.printStackTrace()
                    }
                }
            }
        }

        // Start dynamic text updates after initial delay
        textUpdateHandler?.postDelayed(textUpdateRunnable!!, 4000)
    }

    private fun navigateToNextScreen() {
        // Clean up handlers before navigation
        textUpdateHandler?.removeCallbacks(textUpdateRunnable ?: return)

        val sessionManager = SessionManager(this)
        val intent = if (sessionManager.isLoggedIn()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}