package si.uni_lj.fri.pbd.miniapp1.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

// source https://github.com/kednaik/Coroutines-Contact-Fetching/blob/master/app/src/main/java/com/kedar/coroutinescontactsfetching/Utils.kt

fun Context.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissionWithRationale(
    permission: String,
    requestCode: Int,
    rationale: String,
    title: String = "Permission",
) {
    val provideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    val permissions = arrayOf(permission)

    if (provideRationale) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(rationale)
            setPositiveButton("Ok") { _, _ ->
                ActivityCompat.requestPermissions(
                    this@requestPermissionWithRationale,
                    permissions,
                    requestCode
                )
            }
            create()
            show()
        }
    } else {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
}

