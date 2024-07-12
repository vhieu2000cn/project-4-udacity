package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import net.bytebuddy.implementation.FixedValue.nullValue
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    private val reminderDTO1 = ReminderDTO("reminderDTO1", "reminderDTO1 description", "reminderDTO1 location", 21.1858223, 105.4621346)
    private val reminderDTO2 = ReminderDTO("reminderDTO2", "reminderDTO2 description", "reminderDTO2 location", 21.1858223, 105.4621346)
    private val reminderDTO3 = ReminderDTO("reminderDTO3", "reminderDTO2 description", "reminderDTO3 location", 21.1858223, 105.4621346)

    @get:Rule var instantExecutorRule = InstantTaskExecutorRule()
    @get: Rule var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun initData() {
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun cleanup() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun load_reminders_remindersList() = runBlockingTest {
        fakeDataSource.saveReminder(reminderDTO1)
        fakeDataSource.saveReminder(reminderDTO2)
        fakeDataSource.saveReminder(reminderDTO3)
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.getOrAwaitValue().size, CoreMatchers.`is`(3))
        assertThat(viewModel.remindersList.getOrAwaitValue().first().location,
            CoreMatchers.`is`("reminderDTO1 location")
        )
    }

    @Test
    fun loadReminders_showSnackBar() = runBlockingTest {
        fakeDataSource.setShouldReturnError(true)
        viewModel.loadReminders()
        assertThat(viewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Error: get reminder fail")
        )
    }

    @Test
    fun load_reminders_showNoData() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        viewModel.loadReminders()
        assertThat(viewModel.showNoData.getOrAwaitValue(), CoreMatchers.`is`(true))
    }




}