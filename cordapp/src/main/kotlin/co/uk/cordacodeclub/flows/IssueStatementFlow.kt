package co.uk.cordacodeclub.flows
import co.paralleluniverse.fibers.Suspendable
import co.uk.cordacodeclub.api.CustomerTransaction
import co.uk.cordacodeclub.contract.StatementContract
import co.uk.cordacodeclub.state.AccountType
import co.uk.cordacodeclub.state.Responsibility
import co.uk.cordacodeclub.state.StatementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.sql.Statement
import java.util.*

@InitiatingFlow
@StartableByRPC
@StartableByService
class IssueStatementFlow(val customerTransaction : CustomerTransaction) : FlowLogic<SignedTransaction>() {

    // FLOW WILL RECIEVER TRANSACTION OBJECT
    // CREATE STATE FORM OBJECT
    // CREATE TRANSACTION BUILDER
    // ADD STORE IN VAULT COMMAND TO TRANSACTION
    // SIGN TRANSACTION
    // FINALITY FLOW


    @Suspendable
    override fun call(): SignedTransaction {

        val state = initialiseStatementState()

        val txBuilder = createTransactionBuilder(state)

        val fullySignedTransaction = serviceHub.signInitialTransaction(txBuilder)

        return subFlow(FinalityFlow(fullySignedTransaction))
    }

    @Suspendable
    private fun initialiseStatementState(): StatementState {

        val myParty = ourIdentity
        // some of this data that is currently null would either be included in data received or taken from accounts DB
        return StatementState.issue(owner = myParty,
                nino = customerTransaction.nino,
                dateReported = customerTransaction.date,
                accountType = customerTransaction.accountType,
                accountNumber = null,
                dateAccountOpened = null,
                amountOrLastBalance = customerTransaction.amount,
                amountOverdue = null,
                originalCreditAmountOrLimit = null)
    }

    @Suspendable
    private fun createTransactionBuilder(state: StatementState): TransactionBuilder {
        var txBuilder = TransactionBuilder(getNotary())

        addOutputStateToTransaction(state, txBuilder)

        val addStoreStatementCommand = createStoreStatementCommand()

        txBuilder.addCommand(addStoreStatementCommand)

        return txBuilder
    }

    @Suspendable
    private fun getNotary(): Party {
        val notary = serviceHub.networkMapCache.notaryIdentities.first();
        return notary
    }

    @Suspendable
    private fun createStoreStatementCommand(): Command<StatementContract.Commands.Issue> {
        val signer = ourIdentity.owningKey
        val storeStatementCommand = Command(StatementContract.Commands.Issue(), signer)
        return storeStatementCommand
    }

    @Suspendable
    private fun addOutputStateToTransaction(state: StatementState, txBuilder: TransactionBuilder) {
            txBuilder.addOutputState(state, StatementContract.STATEMENT_CONTRACT_ID)  // co.uk.cordacodeclub.contract.StatementContract
    }


}