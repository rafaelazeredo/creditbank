package co.uk.cordacodeclub.flows;

import co.paralleluniverse.fibers.Suspendable
import co.uk.cordacodeclub.contract.StatementContract
import co.uk.cordacodeclub.state.StatementState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class SendStatementFlow(val nino: String, val requestor: Party) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val filteredStatementStates = findStatementsByNinoAndRequestor()

        val txBuilder = createTransactionBuilder(filteredStatementStates)

        val fullySignedTransaction = serviceHub.signInitialTransaction(txBuilder)

        return subFlow(FinalityFlow(fullySignedTransaction))
    }

    @Suspendable
    private fun findStatementsByNinoAndRequestor(): List<StateAndRef<StatementState>> {
        val allStatementStates = serviceHub.vaultService.queryBy(StatementState::class.java).states
        val filteredStatementStates = allStatementStates.filter {
            it.state.data.nino.equals(nino) && !it.state.data
                    .participants.contains(requestor)
        }
        return filteredStatementStates
    }

    @Suspendable
    private fun createTransactionBuilder(filteredStatementStates: List<StateAndRef<StatementState>>):
            TransactionBuilder {
        var txBuilder = TransactionBuilder(getNotary())

        //For each found input Statement state add requestor as Participant
        addInputAndOutputStatesToTransaction(filteredStatementStates, txBuilder)

        // Create the addParticipant command.
        val addParticpantCommand = createAddPaticipantCommand(filteredStatementStates)
        txBuilder.addCommand(addParticpantCommand)

        return txBuilder
    }

    @Suspendable
    private fun getNotary(): Party {
        val notary = serviceHub.networkMapCache.notaryIdentities.first();
        return notary
    }

    @Suspendable
    private fun addInputAndOutputStatesToTransaction(filteredStatementStates: List<StateAndRef<StatementState>>,
                                                     txBuilder: TransactionBuilder) {
        filteredStatementStates.forEach {
            val inputStateAndRef = it
            val inputStatement = inputStateAndRef.state.data
            var outputStatement = inputStatement.addParticipants(requestor)

            //Create TransactionBuilder adding all found input states and the respectives outputstates
            txBuilder.addInputState(inputStateAndRef)
            txBuilder.addOutputState(outputStatement, StatementContract.STATEMENT_CONTRACT_ID)
        }
    }

    @Suspendable
    private fun createAddPaticipantCommand(filteredStatementStates: List<StateAndRef<StatementState>>):
            Command<StatementContract.Commands
    .AddParticipant> {
        val signer = filteredStatementStates.first().state.data.owner.owningKey
        val addParticpantCommand = Command(StatementContract.Commands.AddParticipant(), signer)
        return addParticpantCommand
    }
}