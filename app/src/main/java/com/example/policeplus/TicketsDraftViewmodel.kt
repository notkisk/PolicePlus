import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class TicketDraftViewModel : ViewModel() {
    var licenseNumber by mutableStateOf("")
    var selectedFamily by mutableStateOf("")
    var ticketDetails by mutableStateOf("")

    fun clearDraft() {
        licenseNumber = ""
        selectedFamily = ""
        ticketDetails = ""
    }

    val hasDraft: Boolean
        get() = licenseNumber.isNotBlank() || selectedFamily.isNotBlank() || ticketDetails.isNotBlank()
}
