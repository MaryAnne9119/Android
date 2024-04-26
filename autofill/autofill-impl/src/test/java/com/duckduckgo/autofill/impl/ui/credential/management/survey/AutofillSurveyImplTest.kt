package com.duckduckgo.autofill.impl.ui.credential.management.survey

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duckduckgo.appbuildconfig.api.AppBuildConfig
import com.duckduckgo.autofill.impl.configuration.integration.JavascriptCommunicationSupport
import com.duckduckgo.common.test.CoroutineTestRule
import java.util.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AutofillSurveyImplTest {

    @get:Rule
    val coroutineTestRule: CoroutineTestRule = CoroutineTestRule()

    private val autofillSurveyStore: AutofillSurveyStore = mock()
    private val appBuildConfig: AppBuildConfig = mock()
    private val javascriptCommunicationSupport: JavascriptCommunicationSupport = mock()
    private val testee: AutofillSurveyImpl = AutofillSurveyImpl(
        statisticsStore = mock(),
        userBrowserProperties = mock(),
        appBuildConfig = appBuildConfig,
        appDaysUsedRepository = mock(),
        dispatchers = coroutineTestRule.testDispatcherProvider,
        autofillSurveyStore = autofillSurveyStore,
        javascriptCommunicationSupport = javascriptCommunicationSupport,
    )

    @Before
    fun setup() {
        whenever(appBuildConfig.deviceLocale).thenReturn(Locale("en"))
        whenever(javascriptCommunicationSupport.supportsModernIntegration()).thenReturn(true)
    }

    @Test
    fun whenSurveyHasNotBeenShownBeforeThenFirstUnusedSurveyReturnsIt() = runTest {
        whenever(autofillSurveyStore.hasSurveyBeenTaken("autofill-2024-04-26")).thenReturn(false)
        val survey = testee.firstUnusedSurvey()
        assertEquals("autofill-2024-04-26", survey!!.id)
    }

    @Test
    fun whenSurveyHasNotBeenShownBeforeButWebViewNotCompatibleThenDoesNotReturnIt() = runTest {
        whenever(autofillSurveyStore.hasSurveyBeenTaken("autofill-2024-04-26")).thenReturn(false)
        whenever(javascriptCommunicationSupport.supportsModernIntegration()).thenReturn(false)
        assertNull(testee.firstUnusedSurvey())
    }

    @Test
    fun whenSurveyHasNotBeenShownBeforeButLocaleNotEnglishThenFirstUnusedSurveyDoesNotReturnIt() = runTest {
        whenever(appBuildConfig.deviceLocale).thenReturn(Locale("fr"))
        whenever(autofillSurveyStore.hasSurveyBeenTaken("autofill-2024-04-26")).thenReturn(false)
        assertNull(testee.firstUnusedSurvey())
    }

    @Test
    fun whenSurveyHasBeenShownBeforeThenFirstUnusedSurveyDoesNotReturnIt() = runTest {
        whenever(autofillSurveyStore.hasSurveyBeenTaken("autofill-2024-04-26")).thenReturn(true)
        assertNull(testee.firstUnusedSurvey())
    }

    @Test
    fun whenSurveyRecordedAsUsedThenPersisted() = runTest {
        testee.recordSurveyAsUsed("surveyId-1")
        verify(autofillSurveyStore).recordSurveyWasShown("surveyId-1")
    }
}
