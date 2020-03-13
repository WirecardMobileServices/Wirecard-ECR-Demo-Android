package de.wirecard.ecr.demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.wirecard.ecr.EcrSdkFactory
import de.wirecard.ecr.model.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

const val TIMEOUT = 120L

@RunWith(AndroidJUnit4::class)
class EcrSdkTest {
    //change IP to your device IP
    private val ecrSdk = EcrSdkFactory.createTcpIp("192.168.43.115", 7890)

    @Test
    fun echo() {
        ecrSdk.echo()
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun sale() {
        val saleRequest = SaleRequest.SaleRequestData("1", PaymentType.CARD_DEBIT, "1")
        ecrSdk.sale(saleRequest)
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }


    @Test
    fun preAuth() {
        val preAuthRequest = PreAuthRequest.PreAuthRequestData("1", PaymentType.CARD_DEBIT, "1")
        ecrSdk.preAuth(preAuthRequest)
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }

    @Test
    fun void() {
        val voidRequest = VoidRequest.VoidRequestData("1", PaymentType.CARD_DEBIT, "1", "1")
        ecrSdk.void(voidRequest)
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }

    @Test
    fun settlement() {
        ecrSdk.settlement()
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }

    @Test
    fun refund() {
        val refundRequest = RefundRequest.RefundRequestData("1", PaymentType.CARD_DEBIT, "1")

        ecrSdk.refund(refundRequest)
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }

    @Test
    fun getLastTransaction() {
        ecrSdk.getLastTransaction()
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }

    @Test
    fun getLastSettlement() {
        ecrSdk.getLastSettlement()
                .test()
                .awaitDone(TIMEOUT, TimeUnit.SECONDS)
                .assertNoErrors()
    }
}