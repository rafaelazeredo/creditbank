package co.uk.cordacodeclub.flow

import com.template.Responder
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test

class SendStatementFlowTest {

    private val network = MockNetwork(listOf("co.uk.cordacodeclub"))
    private val a = network.createNode()
    private val b = network.createNode()

    init {
        listOf(a, b).forEach {
//            it.registerInitiatedFlow(Responder::class.java)
        }
    }

    @Before
    fun setup(){

        network.runNetwork()

        //Issue 5 Statments with Node A as owner
        //Issue 5 Statments with Node A as owner and Node B as a Participant
    }

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `dummy test`() {

    }

    @Test
    fun flowReturnsFullysignedTransaction() {

    }

    @Test
    fun sendStatementFlowCanOnlyBeRunByOwner() {

    }

    @Test
    fun sendStatementFlowNoStatementsFoundToSend() {

    }
}