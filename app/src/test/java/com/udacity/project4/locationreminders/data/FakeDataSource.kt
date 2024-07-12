package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Error
import com.udacity.project4.locationreminders.data.dto.Result.Success

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false
    fun setShouldReturnError(value: Boolean) { shouldReturnError = value }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError){
            Error("Error: get reminder fail")
        }else{
            Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminderDto = reminders.find { it.id == id }
        return when {
            shouldReturnError -> Error("Error: get reminder fail")
            reminderDto == null -> Error("Error: not found return")
            else -> Success(reminderDto)
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}