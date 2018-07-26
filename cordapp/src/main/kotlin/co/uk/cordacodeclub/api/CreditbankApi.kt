package co.uk.cordacodeclub.api

import net.corda.core.node.AppServiceHub
import net.corda.core.serialization.CordaSerializable

@CordaSerializable
class CustomerTransaction(val amount : Int, val description : String,val debit : Boolean)

interface CreditBankApi {
  fun getLastTransaction() : CustomerTransaction
  fun addTransaction(customerTransaction: CustomerTransaction)
}

class CreditbankApiImpl(serviceHub: AppServiceHub) : CreditBankApi {
  override fun getLastTransaction() : CustomerTransaction {
    return CustomerTransaction(10,"mobile phone top up", true) }
  override fun addTransaction(customerTransaction: CustomerTransaction) {
    println("Gonna stick stuff in the ledger $customerTransaction")
  }
}