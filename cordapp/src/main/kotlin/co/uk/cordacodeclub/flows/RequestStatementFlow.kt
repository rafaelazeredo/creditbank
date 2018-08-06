package co.uk.cordacodeclub.flows

import co.paralleluniverse.fibers.Suspendable
import co.uk.cordacodeclub.contract.RequestContract
import co.uk.cordacodeclub.state.RequestState
import co.uk.cordacodeclub.state.StatementState
import net.corda.core.contracts.StateAndRef
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.unwrap


@StartableByService
@StartableByRPC
@InitiatingFlow
class RequestStatementFlow(val nino: String) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        println("Sending request to other nodes from: $ourIdentity")

        // Get a list of all identities from the network map cache.
        val everyone = serviceHub.networkMapCache.allNodes.flatMap { it.legalIdentities }

        // Filter out the notary identities and remove our identity.
        val everyoneButMeAndNotary = everyone.filter { serviceHub.networkMapCache.isNotary(it).not() } - ourIdentity

        // Create a session for each remaining party.
        val sessions = everyoneButMeAndNotary.map { initiateFlow(it) }

        // Send the transaction to all the remaining parties.
        sessions.forEach {
            it.send(nino)
        }
    }
}

//@InitiatedBy(BroadcastTransaction::class)
@InitiatedBy(RequestStatementFlow::class)
class RequestResponderFlow(val counterPartySession : FlowSession) : FlowLogic<Unit>() {

    @Suspendable
    override fun call() {
        println("${ourIdentity} received request from ${counterPartySession.counterparty}")
        val nino:String  = counterPartySession.receive(String::class.java).unwrap({it})
        if (checkForStatementInVaultForRequestor(nino, counterPartySession.counterparty))
            println("${ourIdentity} found statement, sending back to ${counterPartySession.counterparty}")
            subFlow(SendStatementFlow(nino, counterPartySession.counterparty))

    }

    @Suspendable
    private fun checkForStatementInVaultForRequestor(nino:String, counterParty : Party): Boolean {
        return !findStatementsByNinoAndRequestor(nino,counterParty).isEmpty()
    }

    @Suspendable
    private fun findStatementsByNinoAndRequestor(nino:String, requestor:Party): List<StateAndRef<StatementState>> {
        val allStatementStates = serviceHub.vaultService.queryBy(StatementState::class.java).states
        val filteredStatementStates = allStatementStates.filter {
            it.state.data.nino.equals(nino) && !it.state.data
                    .participants.contains(requestor)
        }
        return filteredStatementStates
    }
}
