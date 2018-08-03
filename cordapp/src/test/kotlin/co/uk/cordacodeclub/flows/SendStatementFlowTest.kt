package co.uk.cordacodeclub.flows

import co.uk.cordacodeclub.ALICE
import co.uk.cordacodeclub.state.AccountType
import co.uk.cordacodeclub.state.Responsibility
import co.uk.cordacodeclub.state.StatementState
import net.corda.core.transactions.SignedTransaction
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import org.junit.Test
import java.util.*

class SendStatementFlowTest {

    private val network = MockNetwork(listOf("co.uk.cordacodeclub"))
    private val a = network.createNode()
    private val b = network.createNode()

    @Test
    fun `dummy test`() {

    }
// One Issued nino without participant A and add PArticpant A
// One Issued nino without participant A and another with participant A and add Particpant A
    
    @Test
    fun flowReturnsFullysignedTransaction() {
        val owner = a.info.chooseIdentityAndCert().party
        val newParticipant = b.info.chooseIdentityAndCert().party
        val issueStatement = StatementState.issue(owner, )
        val flow = IssueFlow(issueStatement)
        val future = a.startFlow(flow)
        mockNetwork.runNetwork()
        // Return the unsigned(!) SignedTransaction object from the IOUIssueFlow.
        val ptx: SignedTransaction = future.getOrThrow()
        // Print the transaction for debugging purposes.
        println(ptx.tx)
        // Check the transaction is well formed...
        // No outputs, one input IOUState and a command with the right properties.
        assert(ptx.tx.inputs.isEmpty())
        assert(ptx.tx.outputs.single().data is IOUState)
        val command = ptx.tx.commands.single()
        assert(command.value is IOUContract.Commands.Issue)
        assert(command.signers.toSet() == issueStatement.participants.map { it.owningKey }.toSet())
        ptx.verifySignaturesExcept(borrower.owningKey,
                mockNetwork.defaultNotaryNode.info.legalIdentitiesAndCerts.first().owningKey)

    }

    @Test
    fun sendStatementFlowCanOnlyBeRunByOwner() {

    }

    @Test
    fun sendStatementFlowNoStatementsFoundToSend() {

    }

    private fun issueStatementStateWithZeroAmount() = issueStatementState("ST123DT",0.0)

    private fun issueStatementStateEmptyNino() = issueStatementState("")

    private fun issueStatementState (nino : String? = "ST123DT", amountOrLastBalance: Double? = 50.0): StatementState {
        return StatementState.issue(ALICE.party,
                nino!!,
                Date(),
                AccountType.CREDIT,
                "0123456", Date(), amountOrLastBalance, 100.0, 200.0, Responsibility.INDIVIDUAL)
    }
}