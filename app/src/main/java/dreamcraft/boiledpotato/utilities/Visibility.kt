package dreamcraft.boiledpotato.utilities

import android.view.View
import androidx.annotation.IntDef

/** for specifying that a method parameter should only hold View visibility int values */
@IntDef(View.GONE, View.VISIBLE, View.INVISIBLE)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Visibility