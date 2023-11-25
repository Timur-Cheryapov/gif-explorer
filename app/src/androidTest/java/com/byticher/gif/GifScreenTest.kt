package com.byticher.gif

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.byticher.gif.const.TestTags
import com.byticher.gif.data.FakeGifs
import com.byticher.gif.presentation.screens.ChosenGifsScreen
import com.byticher.gif.presentation.screens.GifScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun animatedTitleWorksOnSwipes() {
        // Set compose content
        composeTestRule.setContent {
            var firstId by remember { mutableIntStateOf(1) }
            val gifs = FakeGifs.getGifs(firstId)

            GifScreen(
                visibleGifs = gifs,
                onNextItem = { firstId += 1 }
            )
        }

        // Check the Title of the Gif
        composeTestRule.onNodeWithText(
            FakeGifs.gifTitle(1)).assertExists()

        // Swipe right the gif
        composeTestRule.onNodeWithTag(
            TestTags.SWIPABLE).performTouchInput { swipeRight() }

        // Check the Title of the Gif
        composeTestRule.onNodeWithText(
            FakeGifs.gifTitle(2)).assertExists()

        // Swipe left the gif
        composeTestRule.onNodeWithTag(
            TestTags.SWIPABLE).performTouchInput { swipeLeft() }

        // Check the Title of the Gif
        composeTestRule.onNodeWithText(
            FakeGifs.gifTitle(3)).assertExists()
    }

    @Test
    fun gifReview_CorrectTitle() {
        // Set compose content
        composeTestRule.setContent {
            val gifs = FakeGifs.getGifs(1)

            ChosenGifsScreen(
                title = stringResource(R.string.disliked_gifs),
                chosenGifs = gifs
            )
        }

        // Click on gif for reviewing
        composeTestRule.onNodeWithTag(
            TestTags.getGifCardTagWithId(3), useUnmergedTree = true).performClick()

        // Check if it is displayed
        composeTestRule.onNodeWithTag(
            TestTags.GIF_IN_REVIEW).assertExists()

        // Check that the title is correct
        composeTestRule.onNodeWithText(
            FakeGifs.gifTitle(3)).assertExists()

        // Close it
        composeTestRule.onNodeWithTag(
            TestTags.GIF_IN_REVIEW).performClick()

        // Check that it isn't displayed
        composeTestRule.onNodeWithTag(
            TestTags.GIF_IN_REVIEW).assertDoesNotExist()
    }
}