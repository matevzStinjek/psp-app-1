package si.uni_lj.fri.pbd.miniapp1

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import si.uni_lj.fri.pbd.miniapp1.utils.deserialize
import si.uni_lj.fri.pbd.miniapp1.utils.serialize

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val destinationIds = setOf(R.id.nav_home, R.id.nav_contacts, R.id.nav_message)
        appBarConfiguration = AppBarConfiguration(destinationIds, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()
        lifecycle.coroutineScope.launch {
            async { setAvatarIfExists() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onAvatarClick(view: View) {
        // early return in case we cannot access the camera
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            showToast("Cannot access camera")
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            showToast("Error accessing the camera")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK -> onCameraActivityResult(data)
        }
    }

    private fun onCameraActivityResult(data: Intent?) {
        // get the image
        val imageBitmap = data?.extras?.get("data") as Bitmap
        setImageAsAvatar(imageBitmap)

        // save the image
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            val encodedImage = serialize(imageBitmap)
            putString(getString(R.string.saved_image_key), encodedImage)
            apply()
        }
    }

    private fun setImageAsAvatar(imageBitmap: Bitmap) {
        val imageView = findViewById<ImageView>(R.id.avatar)
        imageView.setImageBitmap(imageBitmap)
    }

    private suspend fun setAvatarIfExists() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val encodedImage = sharedPref.getString(getString(R.string.saved_image_key), null) ?: return
        val imageBitmap = deserialize(encodedImage)

        // please ignore this abomination, didn't have time to debug why it doesn't find the imageView --> hackerman 😎
        attemptSetImageAsAvatar(imageBitmap)
    }

    private suspend fun attemptSetImageAsAvatar(imageBitmap: Bitmap) {
        var attempts = 0
        var isSet = false
        while (!isSet && attempts < 3) {
            try {
                setImageAsAvatar(imageBitmap)
                isSet = true
            } catch (e: Exception) {
                delay(500)
                attempts++
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
