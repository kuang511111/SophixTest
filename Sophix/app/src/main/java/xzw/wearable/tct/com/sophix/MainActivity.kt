package xzw.wearable.tct.com.sophix

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv1.text = "第2个版本！"

    }
    private fun showText() {
        Handler().postDelayed({
            tv1.text = "第四个版本！"
        },5000)
    }
}
