package si.uni_lj.fri.pbd.miniapp1

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import java.util.*

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
        setAvatarIfExists()
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
            val encodedImage = bitmapToString(imageBitmap)
            putString(getString(R.string.saved_image_key), encodedImage)
            apply()
        }
    }

    private fun setImageAsAvatar(imageBitmap: Bitmap) {
        val imageView = findViewById<ImageView>(R.id.avatar)
        imageView.setImageBitmap(imageBitmap)
    }

    private fun setAvatarIfExists() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val encodedImage = sharedPref.getString(getString(R.string.saved_image_key), null) ?: return

        val imageBytes = Base64.getDecoder().decode(encodedImage.toByteArray())
        val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//        setImageAsAvatar(imageBitmap) // TODO: fix!!
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val b = stream.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
