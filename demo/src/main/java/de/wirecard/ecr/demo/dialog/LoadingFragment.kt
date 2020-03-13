package de.wirecard.ecr.demo.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wirecard.ecr.demo.R
import de.wirecard.ecr.demo.dialog.abs.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_loading.*

class LoadingFragment : BaseDialogFragment() {

    private val handler = Handler()
    private val cancelButtonRunnable: Runnable = Runnable {
        cancelButton.visibility = View.VISIBLE
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_loading, container, false)

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        handler.postDelayed(cancelButtonRunnable, 30000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }
}