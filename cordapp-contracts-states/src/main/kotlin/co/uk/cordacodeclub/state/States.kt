package co.uk.cordacodeclub.state

import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.OwnableState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.util.*

// *********
// * State *
// *********
data class StatementState private constructor (override val owner :AbstractParty,
                                               val nino: String,
                                               val dateReported: Date,
                                               val accountType: AccountType,
                                               val accountNumber: String?,
                                               val dateAccountOpened: Date?,
                                               val amountOrLastBalance: Double?,
                                               val amountOverdue: Double?,
                                               val originalCreditAmountOrLimit: Double?,
                                               val responsibility: Responsibility = Responsibility.INDIVIDUAL,
                                               var pariticpants : List<AbstractParty>
) : OwnableState{

    companion object{
        @JvmStatic
        fun issue(owner : Party,
                  nino: String,
                  dateReported: Date,
                  accountType: AccountType,
                  accountNumber: String?,
                  dateAccountOpened: Date?,
                  amountOrLastBalance: Double?,
                  amountOverdue: Double?,
                  originalCreditAmountOrLimit: Double?,
                  responsibility: Responsibility = Responsibility.INDIVIDUAL):StatementState {

            return StatementState(owner, nino, dateReported, accountType, accountNumber, dateAccountOpened,
                    amountOrLastBalance, amountOverdue, originalCreditAmountOrLimit, responsibility, listOf(owner))
        }
    }

    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addParticipants(newParticipant: Party):StatementState {
        return copy(pariticpants = participants.plus(newParticipant));
    }



    override val participants : List<AbstractParty> get() = pariticpants

}


enum class AccountType {
    DEBIT, CREDIT, MORTGAGE, AUTOLOAN
}

enum class Responsibility{
    INDIVIDUAL, JOINT
}