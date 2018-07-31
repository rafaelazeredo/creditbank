package co.uk.cordacodeclub.contract

import co.uk.cordacodeclub.state.StatementState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class StatementContract : Contract {

    companion object {
        @JvmStatic
        val STATEMENT_CONTRACT_ID = "co.uk.cordacodeclub.contract.StatementContract"
    }

    // A transaction is considered valid if the verify() function of the contract of each of the transaction's input
    // and output states does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<StatementContract.Commands>()
        when (command.value) {
            is Commands.Issue -> requireThat {
                "Issue command cannot have inputs." using (tx.inputs.isEmpty())
                "Issue Command cannot have more than 1 output." using (tx.outputs.size == 1)
                val statement = tx.outputStates.single() as StatementState

                "A newly issued Statement must have a positive amount." using (statement.amountOrLastBalance!!.compareTo
                (0.0) > 0)
                "Only owner can sign." using (command.signers.toSet() == setOf(statement.owner.owningKey))
            }
            is Commands.AddParticipant -> requireThat {

                val groups = tx.groupStates(StatementState::linearId)
                for ((inputs, outputs, key) in groups) {
                    "Add Participant command can only have one input for each output and vice-versa." using (inputs
                            .size == 1 && outputs.size == 1)
                    "Add Participant command output state participant list must be input state participant list plus " +
                            "1" using (inputs.single().participants.size + 1 == outputs.single().participants.size)
                }
                val statement = tx.outputStates.first() as StatementState
                "Only owner can sign." using (command.signers.toSet() == setOf(statement.owner.owningKey))
            }
            else -> { // Note the block
                throw IllegalArgumentException("Command not found.")
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Issue : Commands
        class AddParticipant : Commands
    }
}

