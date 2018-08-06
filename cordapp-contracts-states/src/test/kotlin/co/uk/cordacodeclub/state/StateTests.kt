package co.uk.cordacodeclub.state

import org.junit.Test

import net.corda.core.identity.AbstractParty
import org.junit.Assert.assertEquals
import java.util.*

class StateTests{

    fun getStatementStateFieldType(field:String) = StatementState::class.java.getDeclaredField(field).type

    @Test
    fun `Should have an owner field of type AbstractParty`(){
        val ownerFieldType = getStatementStateFieldType("owner")
        assertEquals(ownerFieldType, AbstractParty::class.java)
    }

    @Test
    fun `Should have a nino field of type String`(){
        val ninoFieldType = getStatementStateFieldType("nino")
        assertEquals(ninoFieldType, String::class.java)
    }

    @Test
    fun `Should have a dateReported field of type Date`(){
        val field = getStatementStateFieldType("dateReported")
        assertEquals(field, Date::class.java)
    }

    @Test
    fun `Should have a accountType field of type AccountType`(){
        val field = getStatementStateFieldType("accountType")
        assertEquals(field, AccountType::class.java)

    }

    @Test
    fun `Should have an accountNumber field of type String`(){
        val field = getStatementStateFieldType("accountNumber")
        assertEquals(field, String::class.java)
    }

    @Test
    fun `Should have a dateAccountOpened field of type Date`(){
        val field = getStatementStateFieldType("dateAccountOpened")
        assertEquals(field, Date::class.java)
    }

    @Test
    fun `Should have a amountOrLastBalance field of type Double`(){
        val field = getStatementStateFieldType("amountOrLastBalance")
        assertEquals(field,java.lang.Double::class.java)
    }

    @Test
    fun `Should have a amountOverdue field of type Double`(){
        val field = getStatementStateFieldType("amountOverdue")
        assertEquals(field, java.lang.Double::class.java)
    }

    @Test
    fun `Should have a originalCreditAmountorLimit field of type Double`(){
        val field = getStatementStateFieldType("originalCreditAmountOrLimit")
        assertEquals(field, java.lang.Double::class.java)
    }

    @Test
    fun `Should have a responsibility field of enum type Responsibility`(){
        val field = getStatementStateFieldType("responsibility")
        assertEquals(field, Responsibility::class.java)
    }

    @Test
    fun `Issue command should create a state`(){
        val initialState = StatementState.issue(ALICE .party,"12345",Date(),AccountType.AUTOLOAN,"123",Date(),
                123.3,55.5,1500.00,Responsibility.INDIVIDUAL)
        assertEquals(initialState::class.java, StatementState::class.java)
    }

    @Test
    fun `Add participant command should add a participant`(){
        val initialState = StatementState.issue(ALICE .party,"12345",Date(),AccountType.AUTOLOAN,"123",Date(),
                123.3,55.5,1500.00,Responsibility.INDIVIDUAL)
        assert(initialState.pariticpants.size == 1)
        val newState = initialState.addParticipants(BOB.party)
        assert(newState.pariticpants.size == 2)
    }


}