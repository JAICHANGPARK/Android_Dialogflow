package a01.lab.dialogflow.com.dreamwalker.dialogflow_lab

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_home.*
import lab.dialogflow.com.dreamwalker.backdrop.BackdropBehavior

class HomeActivity : AppCompatActivity() {


    private lateinit var backdropBehavior: BackdropBehavior

    companion object {
        private const val ARG_LAST_MENU_ITEM = "last_menu_item"

        private const val MENU_GALLERY = R.id.menuGallery
        private const val MENU_TEXT = R.id.menuText
        private const val MENU_LIST = R.id.menuList

        private const val FRAGMENT_CONTAINER = R.id.foregroundContainer

        private const val DEFAULT_ITEM = MENU_GALLERY
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Paper.init(this)


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

        if (Paper.book().read("userExp")){

        }
        user_exp_progressbar.progress = 5.0f

    }

    private fun checkMenuPosition(@IdRes menuItemId: Int) {
        when (menuItemId) {
            MENU_GALLERY -> showPage(HomeFragment())
//            MENU_TEXT -> showPage(TextScreen())
//            MENU_LIST -> showPage(ListScreen())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_LAST_MENU_ITEM, navigationView.checkedItem!!.itemId)
        super.onSaveInstanceState(outState)
    }


    override fun onBackPressed() {
        if (!backdropBehavior.close()) {
            finish()
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
        when(item?.itemId){
            R.id.home -> {

                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }




}
