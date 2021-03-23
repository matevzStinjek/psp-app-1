package si.uni_lj.fri.pbd.miniapp1.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.*

fun serialize(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    val b = stream.toByteArray()
    return Base64.getEncoder().encodeToString(b)
}

fun deserialize(encodedImage: String): Bitmap {
    val imageBytes = Base64.getDecoder().decode(encodedImage.toByteArray())
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
