package subrata.project.android.myshoppal.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import subrata.project.android.myshoppal.R
import subrata.project.android.myshoppal.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)

        Handler(Looper.getMainLooper()).postDelayed({
                val i = Intent(this@SplashActivity, DashboardActivity::class.java)
                startActivity(i)
                finish()
        }, 2000)
    }
}