package de.wirecard.ecr.demo.dialog.abs

import android.content.Context
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment : DialogFragment() {
    protected var listener: OnDialogClick? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDialogClick) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}