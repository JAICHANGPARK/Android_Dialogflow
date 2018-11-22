package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.diary

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_edit_glucose.*
import java.util.*

class EditGlucoseActivity : AppCompatActivity() {
    var realm: Realm? = null

    internal lateinit var userSelectedType: String
    internal lateinit var userGlucoseValue: String
    internal lateinit var dpd: DatePickerDialog
    internal lateinit var tpd: TimePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_glucose)
        initSetting()


    }

    private fun initSetting() {
        initToolbar()
        initRealm()
        setNiceSpinner()
    }

    private fun initRealm() {
        val realmConfig = RealmConfiguration.Builder().name("glucose.realm").build()
        realm = Realm.getInstance(realmConfig)
    }

    private fun initToolbar() {
        setSupportActionBar(bottomAppBar)
        bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        //        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
    }



    private fun setNiceSpinner() {
        val TAG ="wowwo"
        val dataset = LinkedList(Arrays.asList("공복", "취침 전", "운동", "아침 식사", "점심 식사", "저녁 식사"))
        val detailDataSet = LinkedList(Arrays.asList("전", "후"))
        val blankDataSet = LinkedList(Arrays.asList("없음"))

        nice_spinner.attachDataSource(dataset)
        nice_spinner_2.attachDataSource(blankDataSet)

        userSelectedType = "공복"

        nice_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                userSelectedType = dataset[position]
                Log.e(TAG, "onItemSelected: $userSelectedType")
                when (position) {
                    0 //공복
                    -> {
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(blankDataSet)
                    }
                    1 // 취칮전
                    -> {
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(blankDataSet)
                    }
                    2 // 운동
                    -> {
                        userSelectedType = "운동 전"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(detailDataSet)
                    }
                    3 // 아침 식사
                    -> {
                        userSelectedType = "아침 식전"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(detailDataSet)
                    }
                    4 //점심식사
                    -> {
                        userSelectedType = "점심 식전"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(detailDataSet)
                    }
                    5 // 저녁 식사
                    -> {
                        userSelectedType = "저녁 식전"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_2.attachDataSource(detailDataSet)
                    }
                }
                Log.e(TAG, "onItemSelected: $position,$id")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        nice_spinner_2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.e(TAG, "niceSpinner2 onItemSelected: -->  $position")
                Log.e(TAG, "niceSpinner2  onItemSelected: userSelectedType -->  $userSelectedType")
                when (position) {
                    0 -> when (userSelectedType) {
                        "운동 전" -> {
                            userSelectedType = "운동 전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "아침 식전" -> {
                            userSelectedType = "아침 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "점심 식전" -> {
                            userSelectedType = "점심 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "저녁 식전" -> {
                            userSelectedType = "저녁 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }

                        "운동 후" -> {
                            userSelectedType = "운동 전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "아침 식후" -> {
                            userSelectedType = "아침 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "점심 식후" -> {
                            userSelectedType = "점심 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "저녁 식후" -> {
                            userSelectedType = "저녁 식전"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                    }
                    1 -> when (userSelectedType) {

                        "운동 전" -> {
                            userSelectedType = "운동 후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "아침 식전" -> {
                            userSelectedType = "아침 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "점심 식전" -> {
                            userSelectedType = "점심 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "저녁 식전" -> {
                            userSelectedType = "저녁 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }

                        "운동 후" -> {
                            userSelectedType = "운동 후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "아침 식후" -> {
                            userSelectedType = "아침 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "점심 식후" -> {
                            userSelectedType = "점심 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                        "저녁 식후" -> {
                            userSelectedType = "저녁 식후"
                            Log.e(TAG, "onItemSelected: $userSelectedType")
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
    }
}
