package si.uni_lj.fri.pbd.miniapp1.ui.contacts

data class ContactDTO(val id: String, val name: String, var isSelected: Boolean = false) {
    var numbers = ArrayList<String>()
    var emails = ArrayList<String>()

    fun toggleIsSelected() {
        isSelected = !isSelected
    }
}
