package co.uk.cordacodeclube.contract

import net.corda.core.contracts.*
import net.corda.testing.node.MockServices
import org.junit.Test
import org.testng.annotations.Test

/**
 * Practical exercise instructions for Contracts Part 1.
 * The objective here is to write some contract code that verifies a transaction to issue an [StatementState].
 * As with the [StatementStateTests] uncomment each unit test and run them one at a time. Use the body of the tests and the
 * task description to determine how to get the tests to pass.
 */
class AddParticipantStatementStateTest {

    // A pre-defined dummy command.
    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("net.corda.training"))


    /**
     * Task
     *
     * TODO: Add a contract constraint to check non recognised Commands.

     */
    @Test
    fun mustRejectDummyCommand() {

    }


    /**
     * Task 1.
     * Recall that Commands are required to hint to the intention of the transaction as well as take a list of
     * public keys as parameters which correspond to the required signers for the transaction.
     * Commands also become more important later on when multiple actions are possible with an StatementState, e.g.
     * AddParticipant.
     * TODO: Add an "AddParticipant" command to the StatementContract and check for the existence of the command in the
     * verify
     * function.
     * Hint:
     * - For the create command we only care about the existence of it in a transaction, therefore it should subclass
     *   the [TypeOnlyCommandData] class.
     * - The command should be defined inside [StatementContract].
     * - You can use the [requireSingleCommand] function on [tx.commands] to check for the existence and type of the specified command
     *   in the transaction. [requireSingleCommand] requires a generic type to identify the type of command required.
     *
     *   requireSingleCommand<REQUIRED_COMMAND>()
     *
     * - We usually encapsulate our commands around an interface inside the contract class called [Commands] which
     *   implements the [CommandData] interface. The [Create] command itself should be defined inside the [Commands]
     *   interface as well as implement it, for example:
     *
     *     interface Commands : CommandData {
     *         class X : TypeOnlyCommandData(), Commands
     *     }
     *
     * - We can check for the existence of any command that implements [StatementContract.Commands] by using the
     *   [requireSingleCommand] function which takes a type parameter.

     */
    @Test
    fun mustIncludeAddParticipantCommand() {
     
    }

    /**
     * Task 2.
     * As previously observed, issue transactions should not have any input state references. Therefore we must check to
     * ensure that no input states are included in a transaction to issue an Statement.
     * TODO: Write a contract constraint that ensures a transaction to add participant to a Statement does include
     * TODO: one input
     * states.
     * Hint: use a [requireThat] block with a constraint to inside the [StatemtnContract.verify] function to 
     * encapsulate your
     * constraints:
     *
     *     requireThat {
     *         "Message when constraint fails" using (boolean constraint expression)
     *     }
     *
     * Note that the unit tests often expect contract verification failure with a specific message which should be
     * defined with your contract constraints. If not then the unit test will fail!
     *
     * You can access the list of inputs via the [LedgerTransaction] object which is passed into
     * [StatementContract.verify].
     */
    @Test
    fun addParticipantTransactionMustHaveOutputsFromRespectiveInputs() {
        
    }

    /**
     * Task 3.
     * Now we need to ensure that only one [StatementState] is issued per transaction.
     * TODO: Write a contract constraint that ensures only one output state is created in a transaction.
     * Hint: Write an additional constraint within the existing [requireThat] block which you created in the previous
     * task.
     */
    @Test
    fun addParticipantTransactionMustHaveOneOutput() {
    }

    /**
     * Task 4.
     * Now we need to consider the properties of the [StatementState]. We need to ensure that an Statement should always have a
     * positive value.
     * TODO: Write a contract constraint that ensures that Statement State hasn't change it's property values
     * TODO: apart from the newParticipants list
     *
     * Hint: You will nee da number of hints to complete this task!
     * - Use the Kotlin keyword 'val' to create a new constant which will hold a reference to the output Statement state.
     * - You can use the Kotlin function [single] to either grab the single element from the list or throw an exception
     *   if there are 0 or more than one elements in the list. Note that we have already checked the outputs list has
     *   only one element in the previous task.
     * - We need to obtain a reference to the proposed Statement for issuance from the [LedgerTransaction.outputs] list.
     *   This list is typed as a list of [ContractState]s, therefore we need to cast the [ContractState] which we return
     *   from [single] to an [StatementState]. You can use the Kotlin keyword 'as' to cast a class. E.g.
     *
     *       val state = tx.outputStates.single() as XState
     *
     * - When checking the [StatementState.amount] property is greater than zero, you need to check the
     *   [StatementState.amount.quantity] field.
     */
    @Test
    fun addParticipantTransactionMustHaveEqualInputAndOutputPropertiesApartFromParticipantsList() {
    }

    /**
     * Task 5.
     * The list of public keys which the commands hold should contain all of the newParticipants defined in the [StatementState].
     * This is because the Statement is a bilateral agreement where both parties involved are required to sign to issue an
     * Statement or change the properties of an existing Statement.
     * TODO: Add a contract constraint to check that all the required signers are [StatementState] newParticipants.
     * Hint:
     * - In Kotlin you can perform a set equality check of two sets with the == operator.
     * - We need to check that the signers for the transaction are a subset of the newParticipants list.
     * - We don't want any additional public keys not listed in the Statements newParticipants list.
     * - You will need a reference to the Issue command to get access to the list of signers.
     * - [requireSingleCommand] returns the single required command - you can assign the return value to a constant.
     *
     * Kotlin Hints
     * Kotlin provides a map function for easy conversion of a [Collection] using map
     * - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/map.html
     * [Collection] can be turned into a set using toSet()
     * - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/to-set.html
     */
    @Test
    fun ownerMustSignAddParticipantTransaction() {

    }

    /**
     * Task 6.
     *
     * TODO: Add a contract constraint to check that output [StatementState] list of newParticipants size is equls input
     * TODO: [StatementState] list of newParticipants size + 1
     * is mandatory.

     */
    @Test
    fun addParticipantTransactionOutputParticipantsSizeEqualsInputParticipantsSizePlustOne() {

    }

}
