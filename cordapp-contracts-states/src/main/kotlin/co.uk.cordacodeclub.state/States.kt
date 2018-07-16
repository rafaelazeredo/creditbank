// *********
// * State *
// *********
data class TemplateState(val data: String, val nino: String, val operation: Boolean, val amount: Int, val creationDate: String) : ContractState {
    override val participants: List<AbstractParty> get() = listOf()
}
