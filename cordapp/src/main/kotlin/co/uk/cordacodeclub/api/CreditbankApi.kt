package co.uk.cordacodeclub.api

import co.uk.cordacodeclub.state.AccountType
import net.corda.core.node.AppServiceHub
import net.corda.core.serialization.CordaSerializable
import java.util.Date

@CordaSerializable
class CustomerTransaction(val nino : String, val date: Date, val accountType: AccountType, val amount : Double, val description : String,val debit : Boolean)

interface CreditBankApi {
  fun getLastTransaction() : CustomerTransaction
  fun addTransaction(customerTransaction: CustomerTransaction)
}

class CreditbankApiImpl(serviceHub: AppServiceHub) : CreditBankApi {
  override fun getLastTransaction() : CustomerTransaction {
    return CustomerTransaction("NB808883D", java.util.Date(), AccountType.DEBIT, 10.0,"mobile phone top up", true) }
  override fun addTransaction(customerTransaction: CustomerTransaction) {
    println("Gonna stick stuff in the ledger $customerTransaction")
  }
}