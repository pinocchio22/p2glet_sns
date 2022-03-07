package pinocchio22.p2glet_first.p2glet_sns

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.p2glet_first.p2glet_sns.R
import kotlinx.android.synthetic.main.activity_agree.*
import kotlin.system.exitProcess


class AgreeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agree)

//        allCheckBtn.setOnClickListener { onCheckChanged(allCheckBtn) }
//        firstCheckBtn.setOnClickListener { onCheckChanged(firstCheckBtn) }
//        secondCheckBtn.setOnClickListener { onCheckChanged(secondCheckBtn) }
//
//        firstCheckBtn_add.setOnClickListener {
//            // 서비스 이용약관
//            var intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("https://velog.io/@pinocchio22/%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%9D%B4%EC%9A%A9-%EC%95%BD%EA%B4%80")
//            )
//            startActivity(intent)
//        }
//        secondCheckBtn_add.setOnClickListener {
//            // 개인정보처리지침
//            var intent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("https://velog.io/@pinocchio22/%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4%EC%B2%98%EB%A6%AC%EB%B0%A9%EC%B9%A8")
//            )
//            startActivity(intent)
//        }

        val pref = getSharedPreferences("checkFirst", MODE_PRIVATE)
        val checkFirst = pref.getBoolean("checkFirst", false)
        if (!checkFirst) {
            // 앱 최초 실행
            val editor = pref.edit()
            editor.putBoolean("checkFirst", true)
            editor.apply()

            allCheckBtn.setOnClickListener { onCheckChanged(allCheckBtn) }
            firstCheckBtn.setOnClickListener { onCheckChanged(firstCheckBtn) }
            secondCheckBtn.setOnClickListener { onCheckChanged(secondCheckBtn) }

            firstCheckBtn_add.setOnClickListener {
                // 서비스 이용약관
                var intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://velog.io/@pinocchio22/%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%9D%B4%EC%9A%A9-%EC%95%BD%EA%B4%80")
                )
                startActivity(intent)
            }
            secondCheckBtn_add.setOnClickListener {
                // 개인정보처리지침
                var intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://velog.io/@pinocchio22/%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4%EC%B2%98%EB%A6%AC%EB%B0%A9%EC%B9%A8")
                )
                startActivity(intent)
            }

        } else {
            // 앱 최초 실행x
//            Log.d("또실행", checkFirst.toString())
            finish()
        }
    }

    private fun onCheckChanged(compoundButton: CompoundButton) {
        when(compoundButton.id) {
            R.id.allCheckBtn -> {
                if (allCheckBtn.isChecked) {
                    firstCheckBtn.isChecked = true
                    secondCheckBtn.isChecked = true
                } else {
                    firstCheckBtn.isChecked = false
                    secondCheckBtn.isChecked = false
                }
            }
            else -> {
                allCheckBtn.isChecked = (
                        firstCheckBtn.isChecked
                                && secondCheckBtn.isChecked)
            }
        }
        next_Btn_positive.isEnabled = firstCheckBtn.isChecked && secondCheckBtn.isChecked
        next_Btn_positive.setOnClickListener {
            finish()
        }
    }
}