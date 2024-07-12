package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var db: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private val reminderDTO1 = ReminderDTO("reminderDTO1", "reminderDTO1 description", "reminderDTO1 location", 21.1858223, 105.4621346)
    private val reminderDTO2 = ReminderDTO("reminderDTO2", "reminderDTO2 description", "reminderDTO2 location", 21.1858223, 105.4621346)

    @Before
    fun init_dataBase() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository =
            RemindersLocalRepository(db.reminderDao(), TestCoroutineDispatcher())
    }

    @After
    fun close_dataBase() {
        db.close()
    }

    @Test
    fun inserting_then_getById() = runBlocking {
        remindersLocalRepository.saveReminder(reminderDTO1)
        val outputReminder = (remindersLocalRepository.getReminder(reminderDTO1.id) as Result.Success<ReminderDTO>).data
        assertThat(outputReminder.longitude, `is`(reminderDTO1.longitude))
        assertThat(outputReminder.latitude, `is`(reminderDTO1.latitude))
        assertThat(outputReminder, CoreMatchers.notNullValue())
        assertThat(outputReminder.id, `is`(reminderDTO1.id))
        assertThat(outputReminder.description, `is`(reminderDTO1.description))
        assertThat(outputReminder.location, `is`(reminderDTO1.location))
        assertThat(outputReminder.title, `is`(reminderDTO1.title))
    }

    @Test
    fun get_Reminder_byId_whenNotFound() = runBlocking {
        val outputReminder = remindersLocalRepository.getReminder(reminderDTO1.id) as Result.Error
        assertThat(outputReminder.message, `is`("Reminder not found!"))

    }

    @Test
    fun add_then_delete_singleReminder() = runBlocking {
        remindersLocalRepository.saveReminder(reminderDTO1)
        remindersLocalRepository.deleteAllReminders()
        assertThat(remindersLocalRepository.getReminder(reminderDTO1.id) is Result.Error, `is`(true))
    }

    @Test
    fun delete_all_reminders() = runBlocking {
        remindersLocalRepository.deleteAllReminders()
        val outputReminders = remindersLocalRepository.getReminders() as Result.Success
        assertThat(outputReminders.data, `is`(emptyList()))
    }

    @Test
    fun reminders_or_null() = runBlocking {
        db.reminderDao().saveReminder(reminderDTO1)
        db.reminderDao().saveReminder(reminderDTO2)
        val outputReminders: Result<List<ReminderDTO>> = remindersLocalRepository.getReminders()
        assertThat(outputReminders is Result.Success, `is`(true))
        if (outputReminders is Result.Success) assertThat(outputReminders.data.isNotEmpty(), `is`(true))
    }
}