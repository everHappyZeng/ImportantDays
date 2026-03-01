package com.example.importantdays.presentation.persons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.importantdays.domain.model.Person
import com.example.importantdays.domain.model.PersonWithNextDay
import com.example.importantdays.domain.repository.PersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonsViewModel(
    private val personRepository: PersonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonsUiState>(PersonsUiState.Loading)
    val uiState: StateFlow<PersonsUiState> = _uiState.asStateFlow()

    init {
        loadPersons()
    }

    private fun loadPersons() {
        viewModelScope.launch {
            personRepository.getPersonsWithNextDay().collect { persons ->
                _uiState.value = if (persons.isEmpty()) {
                    PersonsUiState.Empty
                } else {
                    PersonsUiState.Success(persons)
                }
            }
        }
    }

    fun addPerson(name: String, notes: String, onDone: () -> Unit) {
        if (name.isBlank()) {
            return
        }
        viewModelScope.launch {
            val person = Person(
                name = name.trim(),
                avatar = null,
                notes = notes.trim()
            )
            personRepository.insertPerson(person)
            onDone()
        }
    }

    fun savePerson(person: Person, onDone: () -> Unit) {
        if (person.name.isBlank()) {
            return
        }
        viewModelScope.launch {
            if (person.id == 0L) {
                personRepository.insertPerson(person.copy(name = person.name.trim(), notes = person.notes.trim()))
            } else {
                personRepository.updatePerson(person.copy(name = person.name.trim(), notes = person.notes.trim()))
            }
            onDone()
        }
    }

    fun deletePerson(personId: Long) {
        viewModelScope.launch {
            personRepository.deletePersonById(personId)
        }
    }
}

sealed class PersonsUiState {
    object Loading : PersonsUiState()
    object Empty : PersonsUiState()
    data class Success(val persons: List<PersonWithNextDay>) : PersonsUiState()
}
