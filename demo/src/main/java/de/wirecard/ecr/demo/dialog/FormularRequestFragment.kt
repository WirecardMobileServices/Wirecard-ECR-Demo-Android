package de.wirecard.ecr.demo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import de.wirecard.ecr.demo.R
import de.wirecard.ecr.demo.dialog.abs.BaseDialogFragment
import de.wirecard.ecr.model.*
import kotlinx.android.synthetic.main.fragment_sale.*

private const val ARG_REQUEST = "ARG_REQUEST"

class FormularRequestFragment : BaseDialogFragment() {

    private lateinit var request: FormularRequestType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            request = FormularRequestType.values()[it.getInt(ARG_REQUEST)]
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sale, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleTextView.text = when (request) {
            FormularRequestType.SALE -> "Sale"
            FormularRequestType.INSTALLMENT -> "Installment"
            FormularRequestType.REFUND -> "Refund"
            FormularRequestType.PRE_AUTH -> "Pre-Auth"
            FormularRequestType.SALE_COMPLETION -> "Sale Completion"
            FormularRequestType.VOID -> "Void"
            FormularRequestType.TOKENIZATION -> "Tokenization"
        }

        transactionAmountLayout.changeVisibility(request != FormularRequestType.TOKENIZATION)
        paymentTypeInput.changeVisibility(request != FormularRequestType.TOKENIZATION)
        if (request != FormularRequestType.TOKENIZATION) {
            paymentTypeInput.setAdapter(
                    ArrayAdapter(
                            requireContext(),
                            R.layout.dropdown_menu_popup_item,
                            PaymentType.values().map { "${it.id} - ${it.definition}" })
            )
            paymentTypeInput.setSelection(0)
        }
        tenureLayout.changeVisibility(request == FormularRequestType.INSTALLMENT)
        invoiceNumberLayout.changeVisibility(request == FormularRequestType.VOID)
        rnnLayout.changeVisibility(request == FormularRequestType.SALE_COMPLETION)

        sendSale.setOnClickListener {
            listener?.onFragmentSendClicked(
                    when (request) {
                        FormularRequestType.SALE -> SaleRequest(
                                SaleRequest.SaleRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.INSTALLMENT -> InstallmentRequest(
                                InstallmentRequest.InstallmentRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        tenureInput.text.toString(),
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.REFUND -> RefundRequest(
                                RefundRequest.RefundRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.PRE_AUTH -> PreAuthRequest(
                                PreAuthRequest.PreAuthRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.SALE_COMPLETION -> CompletionRequest(
                                CompletionRequest.CompletionRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        rnnInput.text.toString(),
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.VOID -> VoidRequest(
                                VoidRequest.VoidRequestData(
                                        transactionAmountInput.text.toString(),
                                        PaymentType.values()[paymentTypeInput.selectedItemPosition],
                                        invoiceNumberInput.text.toString(),
                                        orderIdInput.text.toString()
                                )
                        )
                        FormularRequestType.TOKENIZATION -> TokenizationRequest(
                                TokenizationRequest.TokenizationRequestData(
                                        orderIdInput.text.toString()
                                )
                        )
                    }
            )
            dialog?.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(request: FormularRequestType) =
                FormularRequestFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_REQUEST, request.ordinal)
                    }
                }
    }

    private fun View.changeVisibility(visible: Boolean) {
        this.visibility = when {
            visible -> View.VISIBLE
            else -> View.GONE
        }
    }
}

enum class FormularRequestType {
    SALE,
    REFUND,
    PRE_AUTH,
    VOID,

    INSTALLMENT,
    SALE_COMPLETION,
    TOKENIZATION
}