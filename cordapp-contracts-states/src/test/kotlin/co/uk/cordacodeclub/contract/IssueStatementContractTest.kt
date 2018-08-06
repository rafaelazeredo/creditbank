package co.uk.cordacodeclub.contract


import co.uk.cordacodeclub.state.AccountType
import co.uk.cordacodeclub.state.Responsibility
import co.uk.cordacodeclub.state.StatementState
import co.uk.cordacodeclub.state.ALICE
import co.uk.cordacodeclub.state.BOB
import net.corda.core.contracts.*
import net.corda.testing.contracts.DummyState
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test
import java.lang.Compiler.command
import java.util.*

/**
 * Practical exercise instructions for Contracts Part 1.
 * The objective here is to write some contract code that verifies a transaction to issue an [StatementState].
 * As with the [StatementStateTests] uncomment each unit test and run them one at a time. Use the body of the tests and the
 * task description to determine how to get the tests to pass.
 */
class IssueStatementTest {
    // A pre-defined dummy command.
    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("net.corda.training","co.uk.cordacodeclub"))


    /**
     * Task
     *
     * TODO: Add a contract constraint to check non recognised Commands.

     */
    @Test
    fun mustRejectDummyCommand() {
        val statement = issueStatementState()
        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, statement)
                command(listOf(ALICE.publicKey), DummyCommand())
                this.failsWith("Command not found.")
            }
        }
    }


    /**
     * Task 1.
     * Recall that Commands are required to hint to the intention of the transaction as well as take a list of
     * public keys as parameters which correspond to the required signers for the transaction.
     * Commands also become more important later on when multiple actions are possible with an StatementState, e.g.
     * AddParticipant.
     * TODO: Add an "Issue" command to the StatementContract and check for the existence of the command in the verify
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
    fun mustIncludeIssueCommand() {
        val statement = issueStatementState()

        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, statement)
                command(listOf(ALICE.publicKey),StatementContract.Commands.Issue())
                this.verifies()
            }
        }
    }



    /**
     * Task 2.
     * As previously observed, issue transactions should not have any input state references. Therefore we must check to
     * ensure that no input states are included in a transaction to issue an Statement.
     * TODO: Write a contract constraint that ensures a transaction to issue an Statement does not include any input 
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
    fun issueTransactionMustHaveNoInputs() {
        val statement = issueStatementState()

        ledgerServices.ledger {
            transaction {
                input(StatementContract.STATEMENT_CONTRACT_ID, DummyState())
                output(StatementContract.STATEMENT_CONTRACT_ID, statement)
                command(listOf(ALICE.publicKey),StatementContract.Commands.Issue())
                this.failsWith("Issue command cannot have inputs.")
            }
        }
    }

    /**
     * Task 3.
     * Now we need to ensure that only one [StatementState] is issued per transaction.
     * TODO: Write a contract constraint that ensures only one output state is created in a transaction.
     * Hint: Write an additional constraint within the existing [requireThat] block which you created in the previous
     * task.
     */
    @Test
    fun issueTransactionMustHaveOneOutput() {
        val output1 = issueStatementState()
        val output2 = issueStatementState()

        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, output1)
                output(StatementContract.STATEMENT_CONTRACT_ID, output2)
                command(listOf(ALICE.publicKey),StatementContract.Commands.Issue())
                this.failsWith("Issue Command cannot have more than 1 output.")
            }
        }
    }

    /**
     * Task 4.
     * Now we need to consider the properties of the [StatementState]. We need to ensure that an Statement should always have a
     * positive value.
     * TODO: Write a contract constraint that ensures newly issued Statements always have a positive value.
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
    fun cannotCreateZeroValueStatements() {
        val output = issueStatementStateWithZeroAmount()

        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, output)
                command(listOf(ALICE.publicKey),StatementContract.Commands.Issue())
                this.failsWith("Cannot issue statement with amount <= 0.")
            }
        }
    }

    /**
     * Task 5.
     * The list of public keys which the commands hold should contain all of the participants defined in the [StatementState].
     * This is because the Statement is a bilateral agreement where both parties involved are required to sign to issue an
     * Statement or change the properties of an existing Statement.
     * TODO: Add a contract constraint to check that all the required signers are [StatementState] participants.
     * Hint:
     * - In Kotlin you can perform a set equality check of two sets with the == operator.
     * - We need to check that the signers for the transaction are a subset of the participants list.
     * - We don't want any additional public keys not listed in the Statements participants list.
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
    fun ownerMustSignIssueTransaction() {
        val output = issueStatementStateWithZeroAmount()

        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, output)
                command(listOf(BOB.publicKey),StatementContract.Commands.Issue())
                this.failsWith("Only owner can sign.")
            }
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, output)
                command(listOf(ALICE.publicKey),StatementContract.Commands.Issue())
                this.verifies()
            }
        }
    }

    /**
     * Task 6.
     *
     * TODO: Add a contract constraint to check that owner is in [StatementState] participants.

     */
    @Test
    fun participantsListHasOwnerIssueTransaction() {
    //How to create a Statement where owner is not a participant?
    }


    /**
     * Task 7.
     *
     * TODO: Add a contract constraint to check that [StatementState] NINO is mandatory.

     */
    @Test
    fun ninoIsMandatoryIssueTransaction() {
        val output = issueStatementStateEmptyNino()

        ledgerServices.ledger {
            transaction {
                output(StatementContract.STATEMENT_CONTRACT_ID, output)
                command(listOf(BOB.publicKey),StatementContract.Commands.Issue())
                this.failsWith("Only owner can sign.")
            }
        }
    }


    /**
     * Task 8.
     *
     * TODO: Add a contract constraint to check that [StatementState] dateReported is mandatory.

     */
    @Test
    fun dateReportedIsMandatoryIssueTransaction() {
        // how to create Statement with null dateReported??
    }


    /**
     * Task 7.
     *
     * TODO: Add a contract constraint to check that [StatementState] accountType is mandatory.

     */
    @Test
    fun accountTypeIsMandatoryIssueTransaction() {
        // how to create Statement with null AccountType??
    }

    private fun issueStatementStateWithZeroAmount() = issueStatementState(null,0.0)

    private fun issueStatementStateEmptyNino() = issueStatementState("")

    private fun issueStatementState (nino : String? = "ST123DT", amountOrLastBalance: Double? = 50.0): StatementState {
        return StatementState.issue(ALICE.party,
                                nino!!,
                                Date(),
                                AccountType.CREDIT,
                                "0123456", Date(), amountOrLastBalance, 100.0, 200.0, Responsibility.INDIVIDUAL)
    }
}
