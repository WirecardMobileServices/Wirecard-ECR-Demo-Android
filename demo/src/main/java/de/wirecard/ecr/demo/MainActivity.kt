package de.wirecard.ecr.demo

import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.wirecard.ecr.EcrSdk
import de.wirecard.ecr.EcrSdkFactory
import de.wirecard.ecr.demo.dialog.FormularRequestFragment
import de.wirecard.ecr.demo.dialog.FormularRequestType
import de.wirecard.ecr.demo.dialog.LoadingFragment
import de.wirecard.ecr.demo.dialog.abs.OnDialogClick
import de.wirecard.ecr.model.*
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

private const val PREF_IP = "ipAddress"
private const val PREF_PORT = "port"

class MainActivity : AppCompatActivity(), OnDialogClick {
    lateinit var ecr: EcrSdk
    private var ecrIp: String = ""
    private var ecrPort: Int = -1

    private var autoScrollConsole = true

    private val loading = LoadingFragment()
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scrollConsoleDown()
        sheetBehavior = BottomSheetBehavior.from(buttons)
        KeyboardVisibilityEvent.setEventListener(this) { if (it) hideBottomSheet() }

        ipAddress.setText(preferences.getString(PREF_IP, "10.247.1.3"))
        port.setText(preferences.getInt(PREF_PORT, 7890).toString())

        pairingButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.PAIRING)
        }
        echoButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.ECHO)
        }
        saleButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.SALE)
        }
        refundButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.REFUND)
        }
        preAuthButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.PRE_AUTH)
        }
        voidButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.VOID)
        }
        settlementButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.SETTLEMENT)
        }
        getLastTransactionButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.GET_LAST_TRANSACTION)
        }
        getLastSettlementButton.setOnClickListener {
            showFormularRequestFragment(FormularRequestType.GET_LAST_SETTLEMENT)
        }
    }

    private fun showFormularRequestFragment(request: FormularRequestType) {
        hideBottomSheet()
        consoleScrollView.postDelayed({
            FormularRequestFragment.newInstance(request).show(supportFragmentManager, request.toString())
        }, 200)
    }

    private fun scrollConsoleDown() {
        consoleScrollView.post {
            consoleScrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun hideBottomSheet() {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun addToConsole(inputText: String) {
        val text = try {
            StringBuilder().apply {
                val indentChars = "    "
                var indent = 0
                inputText.forEach { c ->
                    if (c == '(') {
                        indent++
                        append(' ')
                        append(c)
                        append('\n')
                        append(indentChars.repeat(indent))
                    } else if (c == ')') {
                        indent--
                        append('\n')
                        append(indentChars.repeat(indent))
                        append(c)
                    } else if (c == ',') {
                        append(c)
                        append('\n')
                        append(indentChars.repeat(indent))
                        deleteCharAt(length - 1)
                    } else if (c == '=') {
                        append(" = ")
                    } else {
                        append(c)
                    }
                }
            }.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            inputText
        }

        val index = text.indexOfFirst { it == ':' }
        if (index > -1 && index < 12) {
            console.append(SpannableString("\n$text").apply {
                setSpan(
                        StyleSpan(BOLD),
                        0,
                        index + 2,  //2 because include : and \n
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            })
        } else {
            console.append("\n$text")
        }
        if (autoScrollConsole)
            scrollConsoleDown()
    }

    override fun onStart() {
        super.onStart()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun refreshSdkIfNeeded() {
        val newEcrIp = ipAddress.text.toString()
        val newEcrPort = port.text.toString().toInt()
        if (newEcrIp != ecrIp || newEcrPort != ecrPort) {
            ecrIp = newEcrIp
            ecrPort = newEcrPort
            preferences.edit().apply {
                putString(PREF_IP, ecrIp)
                putInt(PREF_PORT, ecrPort)
            }.apply()
            ecr = EcrSdkFactory.createTcpIp(ecrIp, ecrPort)
            Log.w("MainActivity", "create new ecr")
        }
    }

    override fun onFragmentSendClicked(request: EcrRequestModel) {
        refreshSdkIfNeeded()
        addToConsole("\nsend: $request")
        loading.show(supportFragmentManager, "loading")
        when (request) {
            is PairingRequest -> {
                ecr.pairing(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is EchoRequest -> {
                ecr.echo(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is SaleRequest -> {
                ecr.sale(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is RefundRequest -> {
                ecr.refund(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is PreAuthRequest -> {
                ecr.preAuth(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is VoidRequest -> {
                ecr.void(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is SettlementRequest -> {
                ecr.settlement(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is GetLastTransactionRequest -> {
                ecr.getLastTransaction(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            is GetLastSettlementRequest -> {
                ecr.getLastSettlement(request.data).subscribeIoObserveMain().subscribe { response, throwable -> logResponse(response ?: throwable) }
            }
            else -> {
                addToConsole("UNKNOWN operation")
                loading.dismiss()
            }
        }
    }

    private fun <T> Single<T>.subscribeIoObserveMain(): Single<T> {
        return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun logResponse(result: Any) {
        addToConsole("received: $result")
        loading.dismiss()
    }
}
