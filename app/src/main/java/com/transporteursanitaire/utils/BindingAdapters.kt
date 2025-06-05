package com.transporteursanitaire.utils

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.transporteursanitaire.R

/**
 * BindingAdapter pour afficher le champ DEP.
 * Si le contenu de 'dep' est vide, affiche "Attention DEP" en rouge (récupéré depuis les ressources),
 * sinon affiche le contenu et en noir.
 */
@BindingAdapter("depWarning")
fun setDepWarning(textView: TextView, dep: String?) {
    dep?.let {
        textView.text = it.ifEmpty { textView.context.getString(R.string.attention_dep) }
        textView.setTextColor(if (it.isEmpty()) Color.RED else Color.BLACK)
    } ?: run {
        textView.text = textView.context.getString(R.string.attention_dep)
        textView.setTextColor(Color.RED)
    }
}