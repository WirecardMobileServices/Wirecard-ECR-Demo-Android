package de.wirecard.ecr.demo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.wirecard.ecr.demo.R
import de.wirecard.ecr.demo.dialog.abs.BaseDialogFragment
import de.wirecard.ecr.model.EchoRequest
import de.wirecard.ecr.model.GetLastSettlementRequest
import de.wirecard.ecr.model.GetLastTransactionRequest
import de.wirecard.ecr.model.SettlementRequest
import kotlinx.android.synthetic.main.fragment_simple.*

private const val ARG_REQUEST = "ARG_REQUEST"

class SimpleRequestFragment : BaseDialogFragment() {

    private lateinit var request: SimpleRequestType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            request = SimpleRequestType.values()[it.getInt(ARG_REQUEST)]
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_simple, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = when (request) {
            SimpleRequestType.ECHO -> "Echo"
            SimpleRequestType.SETTLEMENT -> "Settlement"
            SimpleRequestType.GET_LAST_TRANSACTION -> "Get Last Transaction"
            SimpleRequestType.GET_LAST_SETTLEMENT -> "Get Last Settlement"
        }
        sendButton.setOnClickListener {
            listener?.onFragmentSendClicked(
                    when (request) {
                        SimpleRequestType.ECHO -> EchoRequest()
                        SimpleRequestType.SETTLEMENT -> SettlementRequest()
                        SimpleRequestType.GET_LAST_TRANSACTION -> GetLastTransactionRequest()
                        SimpleRequestType.GET_LAST_SETTLEMENT -> GetLastSettlementRequest()
                    }
            )
            dialog?.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(request: SimpleRequestType) =
                SimpleRequestFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_REQUEST, request.ordinal)
                    }
                }
    }

}

enum class SimpleRequestType {
    ECHO,
    SETTLEMENT,
    GET_LAST_TRANSACTION,
    GET_LAST_SETTLEMENT
}
