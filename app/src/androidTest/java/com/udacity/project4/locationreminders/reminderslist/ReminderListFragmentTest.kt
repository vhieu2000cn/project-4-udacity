package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val reminderDTO1 = ReminderDTO(
        "reminderDTO1",
        "reminderDTO1 description",
        "reminderDTO1 location",
        21.1858223,
        105.4621346
    )
    private val module = module {
        viewModel {
            SaveReminderViewModel(appContext, get() as ReminderDataSource)
        }
        viewModel {
            RemindersListViewModel(
                appContext,
                get() as ReminderDataSource
            )
        }
        single {
            SaveReminderViewModel(
                appContext,
                get() as ReminderDataSource
            )
        }
        single<ReminderDataSource> { RemindersLocalRepository(get()) }
        single { LocalDB.createRemindersDao(appContext) }
    }


    @Before
    fun init() {
        stopKoin()
        appContext = getApplicationContext()
        startKoin { modules(listOf(module)) }
        repository = get()
        runBlocking { repository.deleteAllReminders() }
    }

    @Test
    fun display_ui_with_data() {
        runBlocking {
            repository.saveReminder(reminderDTO1)
        }
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.reminderssRecyclerView))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(reminderDTO1.title))
                )
            )
    }

    @Test
    fun display_ui_without_data() {
        runBlocking {
            repository.deleteAllReminders()
        }
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withText(appContext.getString(R.string.no_data))).check(matches(isDisplayed()))
    }

    @Test
    fun click_FAB() {
        val navController = mock(NavController::class.java)
        val sec = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        sec.onFragment { Navigation.setViewNavController(it.view!!, navController) }
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}