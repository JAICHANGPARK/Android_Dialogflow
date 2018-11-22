package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.diary

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity.HomeActivity
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.model.Glucose
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomappbar.BottomAppBar
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_edit_glucose.*
import org.jetbrains.anko.toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class EditGlucoseActivity : AppCompatActivity() {

    var realm: Realm? = null

    internal lateinit var userSelectedType: String

    internal lateinit var userGlucoseValue: String
    var receivedDatetime: String? = null
    var userDateChanged = ""
    var userTimeChanged = ""

    var userTypeTimeChanged = ""
    var userTypeChanged = ""
    var userTypeBeforeAfterChanged = ""

    lateinit var dateSimpleDateFormat: SimpleDateFormat
    lateinit var timeSimpleDateFormat: SimpleDateFormat
    lateinit var simpleDateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_glucose)

        receivedDatetime = intent.getStringExtra("datetime")
        dateSimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        timeSimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

        initSetting()

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val second = c.get(Calendar.SECOND)

        //TODO 수정 불가능하게 해서 아이콘만을 눌렀을 때 수정가능하도록 해야한다. - 박제창
        date_edt.isClickable = false
        date_edt.isFocusableInTouchMode = false
        time_edt.isClickable = false
        time_edt.isFocusableInTouchMode = false
        date_edt.setOnClickListener {
            toast("오른쪽 아이콘을 눌러 날짜를 수정하세요")
        }
        time_edt.setOnClickListener {
            toast("오른쪽 아이콘을 눌러 시간을 수정하세요")
        }

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
//            lblDate.setText("" + dayOfMonth + " " + MONTHS[monthOfYear] + ", " + year)
            toast("${monthOfYear + 1} - $dayOfMonth - $year")
            userDateChanged = "$year-${monthOfYear + 1}-$dayOfMonth"
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            val selectDate = formatter.parse(userDateChanged)
            date_edt.setText(DateFormat.getDateInstance().format(selectDate))
        }, year, month, day)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hourOfDays, minutes ->
            toast("$hourOfDays:$minutes")
            val selectTime = "$hourOfDays:$minutes"
            val formatter = SimpleDateFormat("HH:mm", Locale.KOREA)
            val selectDate = formatter.parse(selectTime)
            userTimeChanged = timeSimpleDateFormat.format(selectDate)
            time_edt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(selectDate))

        }, hourOfDay, minute, true)

        date_button.setOnClickListener {
            dpd.show()
        }

        time_button.setOnClickListener {
            tpd.show()
        }

        home.setOnClickListener {
            startActivity(Intent(this@EditGlucoseActivity, HomeActivity::class.java))
            finish()
        }

        done.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("알림")
            builder.setMessage("수정하신 정보를 저장 하시겠어요?")
            builder.setPositiveButton(android.R.string.yes) { dialogInterface, _ ->

                val date = simpleDateFormat.parse("$userDateChanged $userTimeChanged")
                val lastDate = dateSimpleDateFormat.format(date)
                val lastTime = timeSimpleDateFormat.format(date)
                val dateTime = simpleDateFormat.format(date)
                val userGlucoseValue = glucose_value_edt.text.toString()


                Logger.getLogger(EditGlucoseActivity::class.java.name).warning("$userTypeTimeChanged + $userSelectedType  + $userTypeChanged")
                Logger.getLogger(EditGlucoseActivity::class.java.name).warning("$lastDate | $lastTime | $dateTime")

                val userType = "$userSelectedType $userTypeChanged"
                updateRealmData(realm!!, date, dateTime, lastDate, lastTime, userGlucoseValue, userType, userTypeTimeChanged)

                dialogInterface.dismiss()
                startActivity(Intent(this@EditGlucoseActivity, HomeActivity::class.java))
                finish()

            }

            builder.setNegativeButton(android.R.string.no) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            builder.show()
        }


        val resultValue = realm?.where(Glucose::class.java)?.equalTo("datetime", receivedDatetime)?.findFirst()
        if (resultValue != null) {
            val date = resultValue.date
            date_edt.setText(DateFormat.getDateInstance().format(date))
            time_edt.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date))
            glucose_value_edt.setText(resultValue.userGlucoValue.toString())
            userDateChanged = resultValue.rawDate!!
            userTimeChanged = resultValue.rawTime!!
            val userTypeTime = resultValue.userTypeTime
            val userType = resultValue.userType
            placeIndexSpinner(userTypeTime, userType)

        }

    }

    private fun updateRealmData(realm: Realm, date: Date, dt: String, rd: String, rt: String, gv: String, ut: String, utt: String) {
        val userOriginValue = realm.where(Glucose::class.java).equalTo("datetime", receivedDatetime).findFirst()
        realm.beginTransaction()
        if (userOriginValue != null) {
            userOriginValue.date = date
            userOriginValue.datetime = dt
            userOriginValue.rawDate = rd
            userOriginValue.rawTime = rt
            userOriginValue.userGlucoValue = gv.toFloat()
            userOriginValue.userType = ut
            userOriginValue.userTypeTime = utt
            userOriginValue.userDetailTime = ""
        }
        realm.commitTransaction()
    }

    private fun placeIndexSpinner(userTypeTime: String?, userType: String?) {

        val detailDataSet = LinkedList(Arrays.asList("전", "후"))
        val blankDataSet = LinkedList(Arrays.asList("없음"))

        when (userTypeTime) {
            "아침" -> {
                userTypeTimeChanged = "아침"
                nice_spinner.selectedIndex = 0
            }
            "점심" -> {
                userTypeTimeChanged = "점심"
                nice_spinner.selectedIndex = 1
            }
            "저녁" -> {
                userTypeTimeChanged = "저녁"
                nice_spinner.selectedIndex = 2
            }
        }

        when (userType) {
            "공복" -> {
                userSelectedType = "공복"
                nice_spinner_2.selectedIndex = 0
                nice_spinner_3.attachDataSource(blankDataSet)
            }
            "취침 전" -> {
                userSelectedType = "취침 전"
                nice_spinner_2.selectedIndex = 1
                nice_spinner_3.attachDataSource(blankDataSet)
            }

            "취침전" -> {
                userSelectedType = "취침 전"
                nice_spinner_2.selectedIndex = 1
                nice_spinner_3.attachDataSource(blankDataSet)
            }

            "운동전" -> {
                userSelectedType = "운동"
                nice_spinner_2.selectedIndex = 2
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 0
            }
            "운동후" -> {
                userSelectedType = "운동"
                nice_spinner_2.selectedIndex = 2
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 1
            }
            "운동 전" -> {
                userSelectedType = "운동"
                nice_spinner_2.selectedIndex = 2
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 0
            }
            "운동 후" -> {
                userSelectedType = "운동"
                nice_spinner_2.selectedIndex = 2
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 1
            }

            "식사전" -> {
                userSelectedType = "식사"
                nice_spinner_2.selectedIndex = 3
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 0
            }
            "식사후" -> {
                userSelectedType = "식사"
                nice_spinner_2.selectedIndex = 3
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 1
            }
            "식사 전" -> {
                userSelectedType = "식사"
                nice_spinner_2.selectedIndex = 3
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 0
            }
            "식사 후" -> {
                userSelectedType = "식사"
                nice_spinner_2.selectedIndex = 3
                nice_spinner_3.attachDataSource(detailDataSet)
                nice_spinner_3.selectedIndex = 1
            }
        }
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
        bottomAppBar.setNavigationOnClickListener {
            toast("준비중...")
        }
    }


    private fun setNiceSpinner() {
        val TAG = "wowwo"
        val preSet = LinkedList(Arrays.asList("아침", "점심", "저녁"))
        val dataset = LinkedList(Arrays.asList("공복", "취침 전", "운동", "식사", "투약"))
        val detailDataSet = LinkedList(Arrays.asList("전", "후"))
        val blankDataSet = LinkedList(Arrays.asList("없음"))

        nice_spinner.attachDataSource(preSet)
        nice_spinner_2.attachDataSource(dataset)
        nice_spinner_3.attachDataSource(detailDataSet)
        userSelectedType = "공복"
        userTypeTimeChanged = "아침"
        userTypeChanged = "전"
        /**
         *  var userTypeTimeChanged = ""
        var userTypeChanged = ""
        var userTypeBeforeAfterChanged = ""
         */

        nice_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                userTypeTimeChanged = preSet[position]
                Log.e(TAG, "onItemSelected: $userTypeTimeChanged")
                when (position) {
                    0 //아침
                    -> {
                        Log.e(TAG, "onItemSelected: $userTypeTimeChanged")
//                        nice_spinner_3.attachDataSource(blankDataSet)
                    }
                    1 // 점심
                    -> {
                        Log.e(TAG, "onItemSelected: $userTypeTimeChanged")
//                        nice_spinner_3.attachDataSource(blankDataSet)
                    }
                    2 // 저녁
                    -> {
                        Log.e(TAG, "onItemSelected: $userTypeTimeChanged")
                    }
                }
                Log.e(TAG, "onItemSelected: $position,$id")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })

        nice_spinner_2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                userSelectedType = dataset[position]
                Log.e(TAG, "onItemSelected: $userSelectedType")
                when (position) {
                    0 //공복
                    -> {
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_3.attachDataSource(blankDataSet)
                        userTypeChanged = ""
                    }
                    1 // 취칮전
                    -> {

                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_3.attachDataSource(blankDataSet)
                        userTypeChanged = ""
                    }
                    2 // 운동
                    -> {
                        userSelectedType = "운동"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_3.attachDataSource(detailDataSet)
                    }
                    3 // 식사
                    -> {
                        userSelectedType = "식사"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_3.attachDataSource(detailDataSet)
                    }
                    4 //투약
                    -> {
                        userSelectedType = "투약"
                        Log.e(TAG, "onItemSelected: $userSelectedType")
                        nice_spinner_3.attachDataSource(detailDataSet)
                    }

                }
                Log.e(TAG, "onItemSelected: $position,$id")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        nice_spinner_3.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.e(TAG, "niceSpinner2 onItemSelected: -->  $position")
                Log.e(TAG, "niceSpinner2  onItemSelected: userSelectedType -->  $userSelectedType")

                userTypeChanged = detailDataSet[position]

                when (position) {
                    0 -> when (userTypeChanged) {
                        "전" -> {
                            userTypeChanged = "전"
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }
                        "후" -> {
                            userTypeChanged = "후"
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }
                        "없음" -> {
                            userTypeChanged = ""
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }

                    }
                    1 -> when (userTypeChanged) {

                        "전" -> {
                            userTypeChanged = "전"
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }
                        "후" -> {
                            userTypeChanged = "후"
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }
                        "없음" -> {
                            userTypeChanged = ""
                            Log.e(TAG, "onItemSelected: $userTypeChanged")
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })
    }
}
