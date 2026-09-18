package com.theinheritance.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.theinheritance.ui.theme.TheInheritanceTheme
import com.theinheritance.ui.theme.ClayButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class JournalScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun `clay button renders with test tag`() {
        composeRule.setContent { TheInheritanceTheme { ClayButton("Post Entry", {}, testTag = "btn_post_entry") } }
        composeRule.onNodeWithTag("btn_post_entry").assertIsDisplayed()
    }
}
