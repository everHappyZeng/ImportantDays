package com.example.importantdays.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.usecase.ImportContactsBirthdayUseCase
import com.example.importantdays.domain.usecase.ImportResult
import com.example.importantdays.util.ContactInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImportContactsViewModel(
    private val importContactsUseCase: ImportContactsBirthdayUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportContactsUiState())
    val uiState: StateFlow<ImportContactsUiState> = _uiState.asStateFlow()

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val contacts = importContactsUseCase.readContacts()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    contacts = contacts,
                    totalCount = contacts.size
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "读取通讯录失败"
                )
            }
        }
    }

    fun toggleContactSelection(contactId: String) {
        val currentSelected = _uiState.value.selectedContacts.toMutableSet()
        if (currentSelected.contains(contactId)) {
            currentSelected.remove(contactId)
        } else {
            currentSelected.add(contactId)
        }
        _uiState.value = _uiState.value.copy(selectedContacts = currentSelected)
    }

    fun selectAll() {
        _uiState.value = _uiState.value.copy(
            selectedContacts = _uiState.value.contacts.map { it.id }.toSet()
        )
    }

    fun deselectAll() {
        _uiState.value = _uiState.value.copy(selectedContacts = emptySet())
    }

    fun toggleLunarForContact(contactId: String) {
        val currentLunar = _uiState.value.lunarContacts.toMutableSet()
        if (currentLunar.contains(contactId)) {
            currentLunar.remove(contactId)
        } else {
            currentLunar.add(contactId)
        }
        _uiState.value = _uiState.value.copy(lunarContacts = currentLunar)
    }

    fun importSelectedContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true)
            
            val selectedContacts = _uiState.value.contacts.filter {
                _uiState.value.selectedContacts.contains(it.id)
            }
            
            val results = importContactsUseCase.importAllContacts(
                selectedContacts,
                _uiState.value.lunarContacts
            )

            val successCount = results.count { it is ImportResult.Success }
            val skippedCount = results.count { it is ImportResult.Skiped }

            _uiState.value = _uiState.value.copy(
                isImporting = false,
                importSuccessCount = successCount,
                importSkippedCount = skippedCount,
                isImportComplete = true
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetImportState() {
        _uiState.value = _uiState.value.copy(
            isImportComplete = false,
            importSuccessCount = 0,
            importSkippedCount = 0
        )
    }
}

data class ImportContactsUiState(
    val isLoading: Boolean = false,
    val contacts: List<ContactInfo> = emptyList(),
    val selectedContacts: Set<String> = emptySet(),
    val lunarContacts: Set<String> = emptySet(),  // 标记哪些联系人的生日是农历
    val totalCount: Int = 0,
    val isImporting: Boolean = false,
    val isImportComplete: Boolean = false,
    val importSuccessCount: Int = 0,
    val importSkippedCount: Int = 0,
    val errorMessage: String? = null
)
