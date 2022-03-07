package pinocchio22.p2glet_first.p2glet_sns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import com.p2glet_first.p2glet_sns.R
import kotlinx.android.synthetic.main.activity_agree.*

class AgreeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agree)

        allCheckBtn.setOnClickListener { onCheckChanged(allCheckBtn) }
        firstCheckBtn.setOnClickListener { onCheckChanged(firstCheckBtn) }
        secondCheckBtn.setOnClickListener { onCheckChanged(secondCheckBtn) }

    }

    private fun onCheckChanged(compoundButton: CompoundButton) {
        when(compoundButton.id) {
            R.id.allCheckBtn -> {
                if (allCheckBtn.isChecked) {
                    firstCheckBtn.isChecked = true
                    secondCheckBtn.isChecked = true
                }else {
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
    }
}