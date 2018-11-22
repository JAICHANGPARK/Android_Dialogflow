package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.activity

import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.R
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.findBehavior
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment.AnalysisFragment
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.fragment.HomeFragment
import a01.lab.dialogflow.com.dreamwalker.dialogflow_lab.util.LevelDesign
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import io.fabric.sdk.android.Fabric
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_home.*
import lab.dialogflow.com.dreamwalker.backdrop.BackdropBehavior
import org.jetbrains.anko.toast


class HomeActivity : AppCompatActivity() {


    private lateinit var backdropBehavior: BackdropBehavior

    companion object {
        private const val ARG_LAST_MENU_ITEM = "last_menu_item"

        private const val MENU_GALLERY = R.id.menuGallery
        private const val MENU_DIARY = R.id.menuDairy
        private const val MENU_SETTING = R.id.menuSetting
//        private const val MENU_LIST = R.id.menuList

        private const val FRAGMENT_CONTAINER =
            R.id.foregroundContainer

        private const val DEFAULT_ITEM =
            MENU_DIARY
    }

    var userExp: Int = 0
    var userLevel: Int = 1

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Paper.init(this)
        Fabric.with(this, Crashlytics())
//        val androidIDs = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
//        toast(androidIDs.toString())

        showInitTargetView()

        backdropBehavior = foregroundContainer.findBehavior()
        with(backdropBehavior) {
            attachBackContainer(R.id.backContainer)
            attachToolbar(R.id.toolbar)
        }
        with(toolbar) {
            setTitle(R.string.app_name)
        }


        showPage(HomeFragment())

        navigationView.setNavigationItemSelectedListener { item ->
            checkMenuPosition(item.itemId)
            backdropBehavior.close()
            true
        }

        val currentItem = savedInstanceState?.getInt(ARG_LAST_MENU_ITEM) ?: DEFAULT_ITEM
        navigationView.setCheckedItem(currentItem)
        checkMenuPosition(navigationView.checkedItem!!.itemId)

        fab.setOnClickListener {
            val intent = Intent(this, MainActivityV2::class.java)
            startActivity(intent)
        }

        processUserLevel()


//        user_exp_progressbar.progress = 5.0f


    }

    fun processUserLevel() {
        val levelList = LevelDesign.getLevelAndExp()
        levelList.forEach { i ->
            println("Out: array 사이즈는 ${levelList.size} 요청한 x = ${i.level}")
//            Logger.getLogger(this::class.java.name).warning(i.level.toString())
        }

        if (Paper.book("user").read<Int>("exp") == null) {
            Paper.book("user").write("exp", 0)
            Paper.book("user").write("level", 1)
            userExp = 0
            userLevel = 1
            val needExp = LevelDesign.getNeedsExp(userLevel)

            user_exp_progressbar.max = needExp!!.toFloat()
            user_exp_progressbar.progress = userExp.toFloat()
            user_level_text_view.text = LevelDesign.getUserLevelText(userLevel) + " (Level: $userLevel)"
            user_exp_text_view.text = "$userExp / $needExp"
        } else {
            userExp = Paper.book("user").read<Int>("exp")
            userLevel = Paper.book("user").read<Int>("level")
            userLevel = LevelDesign.getUserLevel(userExp)
            val needExp = LevelDesign.getNeedsExp(userLevel)

            user_exp_progressbar.max = needExp!!.toFloat()
            user_exp_progressbar.progress = userExp.toFloat()
            user_level_text_view.text = LevelDesign.getUserLevelText(userLevel) + " (Level: $userLevel)"
            user_exp_text_view.text = "$userExp / $needExp"
        }
    }

    private fun checkMenuPosition(@IdRes menuItemId: Int) {
        when (menuItemId) {


            MENU_DIARY -> showPage(HomeFragment())
            MENU_GALLERY -> showPage(AnalysisFragment())
            MENU_SETTING -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }
//            MENU_TEXT -> showPage(TextScreen())
//            MENU_LIST -> showPage(ListScreen())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_LAST_MENU_ITEM, navigationView.checkedItem!!.itemId)
        super.onSaveInstanceState(outState)
    }


    private var time: Long = 0
    override fun onBackPressed() {
        if (!backdropBehavior.close()) {
//            finish()
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis()
                toast("뒤로 가기 버튼을 한번 더 누르면 종료합니다.")
            } else if (System.currentTimeMillis() - time < 2000) {
                finish()
            }
        }

    }

    private fun showPage(page: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(FRAGMENT_CONTAINER, page)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.home -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInitTargetView() {
        val flag = Paper.book("user").read("first_tap_target", false) as Boolean
        if (flag) {
            toast("어서오세요")
        } else {
            TapTargetView.showFor(this, // `this` is an Activity
                TapTarget.forView(findViewById(R.id.fab), "당뇨모리만의 특별한 혈당 기록", "당뇨모리 에이전트와 대화도 가능하며 혈당 기록도 가능합니다.")
                    // All options below are optional
                    .outerCircleColor(R.color.red)      // Specify a color for the outer circle
                    .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                    .titleTextColor(R.color.white)      // Specify the color of the title text
                    .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                    .descriptionTextColor(R.color.red)  // Specify the color of the description text
                    .textColor(R.color.white)            // Specify a color for both the title and description text
                    .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                    .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                    .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                    .tintTarget(true)                   // Whether to tint the target view's color
                    .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                    .icon(getDrawable(R.drawable.ic_chat_black_24dp))                     // Specify a custom drawable to draw as the target
                    .targetRadius(60), // Specify the target radius (in dp)
                object :
                    TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    override fun onTargetClick(view: TapTargetView) {
                        super.onTargetClick(view)      // This call is optional
//                        toast("clicked")
                        Paper.book("user").write("first_tap_target", true)
                    }
                })
        }
    }
}
