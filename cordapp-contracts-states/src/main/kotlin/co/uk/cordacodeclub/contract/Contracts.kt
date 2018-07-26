package co.uk.cordacodeclub.contract

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

val STATEMENT_CONTRACT_ID = "co.uk.cordacodeclub.contract.StatementContract"

class StatementContract : Contract {
    // A transaction is considered valid if the verify() function of the contract of each of the transaction's input
    // and output states does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Issue : Commands
        class AddParticipant : Commands
    }
}

