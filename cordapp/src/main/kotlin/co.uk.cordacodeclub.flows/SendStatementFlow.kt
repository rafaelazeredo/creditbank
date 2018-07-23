package co.uk.cordacodeclub.flows;

import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.nodeapi.internal.bridging.BridgeControl
import org.apache.commons.jexl3.parser.ParserConstants.and

@InitiatingFlow
@StartableByRPC
class SendStatementFlow (val nin:String, val requestor: Party) : FlowLogic<Unit>(){


    override fun call(): Unit {
        val notary = serviceHub.networkMapCache.notaryIdentities.first();

        //Query Statement Input State by nino,requestor not in participants and ourIdentity == owner

        //For each found input Statement state add requestor as Participant
            // State add Participant method

        //Create TransactionBuilder adding all found input states and the respectives outputstates

        //We sign the transaction

        //FinalityFlow

        return Unit
    }


}