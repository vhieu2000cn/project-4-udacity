package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {


    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var db: RemindersDatabase
    private val reminderDTO1 = ReminderDTO("reminderDTO1", "reminderDTO1 description", "reminderDTO1 location", 21.1858223, 105.4621346)
    private val reminderDTO2 = ReminderDTO("reminderDTO2", "reminderDTO2 description", "reminderDTO2 location", 21.1858223, 105.4621346)

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        db.reminderDao().saveReminder(reminderDTO1)
        val outputReminder = db.reminderDao().getReminderById(reminderDTO1.id)
        assertThat(outputReminder as ReminderDTO, notNullValue())
        assertThat(outputReminder.id, `is`(reminderDTO1.id))
        assertThat(outputReminder.title, `is`(reminderDTO1.title))
        assertThat(outputReminder.description, `is`(reminderDTO1.description))
        assertThat(outputReminder.location, `is`(reminderDTO1.location))
        assertThat(outputReminder.latitude, `is`(reminderDTO1.latitude))
        assertThat(outputReminder.longitude, `is`(reminderDTO1.longitude))
    }

    @Test
    fun insertRemindersAndGetReminders() = runBlockingTest {
        db.reminderDao().saveReminder(reminderDTO1)
        db.reminderDao().saveReminder(reminderDTO2)
        val outputReminders = db.reminderDao().getReminders()
        assertThat(outputReminders, notNullValue())
        assertThat(outputReminders.size, `is`(3))
        assertThat(outputReminders, hasItem(reminderDTO1))
        assertThat(outputReminders, hasItem(reminderDTO2))
    }

    @Test
    fun deleteAllRemindersAndGetNoReminders() = runBlockingTest {
        db.reminderDao().saveReminder(reminderDTO1)
        db.reminderDao().saveReminder(reminderDTO2)
        db.reminderDao().deleteAllReminders()
        val outputReminders = db.reminderDao().getReminders()
        assertThat(outputReminders, notNullValue())
        assertThat(outputReminders.size, `is`(0))
    }
}