package net.corda.training.state


import net.corda.core.contracts.CommandAndState
import net.corda.core.contracts.OwnableState
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.QueryableState
import java.util.*

// *********
// * State *
// *********
data class StatementState(override val owner :AbstractParty,
                          val nino: String,
                          val dateReported: Date,
                          val accountType: AccountType,
                          val accountNumber: String?,
                          val dateAccountOpened: Date?,
                          val amountOrLastBalance: Double?,
                          val amountOverdue: Double?,
                          val originalCreditAmountOrLimit: Double?,
                          val responsibility: Responsibility = Responsibility.INDIVIDUAL
) : OwnableState, QueryableState{

    override fun withNewOwner(newOwner: AbstractParty): CommandAndState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val participants: List<AbstractParty> get() = listOf(owner)


}


enum class AccountType {
    DEBIT, CREDIT, MORTGAGE, AUTOLOAN,
}

enum class Responsibility{
    INDIVIDUAL, JOINT
}