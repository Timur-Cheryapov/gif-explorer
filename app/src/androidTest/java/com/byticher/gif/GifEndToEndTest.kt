package com.byticher.gif

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.byticher.gif.const.TestTags
import com.byticher.gif.presentation.screens.MainScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before

import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GifEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        // Inject dependencies
        hiltRule.inject()
        // Set compose content
        composeTestRule.activity.setContent {
            MainScreen()
        }
        // Wait for the data to load
        // I don't use ...mainClock.advanceTimeBy
        // because it doesn't affect the real time
        runBlocking { delay(3000L) }
    }

    @Test
    fun goToSectionsWithNoGifsThenAddGifs() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Go to the liked section of gifs
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.favourite_section_button)).performClick()

        // Check that there are no gifs and the text about that is shown
        composeTestRule.onNodeWithText(
            context.getString(R.string.there_are_no_gifs)).assertExists()

        // Navigate back to the gif screen
        Espresso.pressBack()

        // Go to the disliked section of gifs
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.disliked_section_button)).performClick()

        // Check that there are no gifs and the text about that is shown
        composeTestRule.onNodeWithText(
            context.getString(R.string.there_are_no_gifs)).assertExists()

        // Navigate back to the gif screen
        Espresso.pressBack()

        // Swipe right the gif on the main screen
        composeTestRule.onNodeWithTag(
            TestTags.SWIPABLE).performTouchInput { swipeRight() }

        // Go to the liked section of gifs
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.favourite_section_button)).performClick()

        // Check if the gif with id = 1 was added
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(1), true).assertExists()

        // Navigate back to the gif screen
        Espresso.pressBack()

        // Swipe left the gif on the main screen
        composeTestRule.onNodeWithTag(
            TestTags.SWIPABLE).performTouchInput { swipeLeft() }

        // Swipe left the gif on the main screen
        composeTestRule.onNodeWithTag(
            TestTags.SWIPABLE).performTouchInput { swipeLeft() }

        // Go to the disliked section of gifs
        composeTestRule.onNodeWithContentDescription(
            context.getString(R.string.disliked_section_button)).performClick()

        // Check if the gifs with id = 2 and 3 were added
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(2), true).assertExists()
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(3), true).assertExists()

        // Check that no extra gifs are shown
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(4), true).assertDoesNotExist()

        // Review (open) the gif with id = 3
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(3), true).performClick()

        // See if the gif for review has opened
        composeTestRule.onNodeWithTag(TestTags.GIF_IN_REVIEW).assertExists()

        // Close the gif by clicking it
        composeTestRule.onNodeWithTag(TestTags.GIF_IN_REVIEW).performClick()

        // See if the gif for review has closed
        composeTestRule.onNodeWithTag(TestTags.GIF_IN_REVIEW).assertDoesNotExist()
    }

    @Test // Requires fresh start on a restarted device (for best speeds)
    fun multipleSwipes_CheckGifPagination() {
        // Default number of downloaded gifs is approximately 40

        repeat(50) { id ->
            // Swipes wouldn't be possible if gifs are not loading
            // (no gif cards wouldn't be shown)

            // Swipe
            composeTestRule.onNodeWithTag(
                TestTags.SWIPABLE).performTouchInput { swipeRight() }

            // Give time to fetch data from network for some gifs
            if (id % 10 == 7) {
                runBlocking { delay(1000L) }
            }
        }
    }
}