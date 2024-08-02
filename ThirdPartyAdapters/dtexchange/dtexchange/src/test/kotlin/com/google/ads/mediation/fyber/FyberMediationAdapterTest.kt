// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.ads.mediation.fyber

import android.app.Activity
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fyber.inneractive.sdk.external.InneractiveAdManager
import com.fyber.inneractive.sdk.external.OnFyberMarketplaceInitializedListener
import com.fyber.inneractive.sdk.external.OnFyberMarketplaceInitializedListener.FyberInitStatus
import com.google.ads.mediation.adaptertestkit.AdapterTestKitConstants
import com.google.ads.mediation.adaptertestkit.assertGetSdkVersion
import com.google.ads.mediation.adaptertestkit.assertGetVersionInfo
import com.google.ads.mediation.adaptertestkit.createMediationConfiguration
import com.google.android.gms.ads.AdFormat
import com.google.android.gms.ads.mediation.InitializationCompleteCallback
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class FyberMediationAdapterTest {

  // Subject of testing.
  private lateinit var adapter: FyberMediationAdapter
  private lateinit var mockInneractiveAdManager: MockedStatic<InneractiveAdManager>

  private val serverParameters =
    bundleOf(FyberMediationAdapter.KEY_APP_ID to AdapterTestKitConstants.TEST_APP_ID)
  private val mockInitializationCompleteCallback: InitializationCompleteCallback = mock()
  private val mockSdkWrapper: SdkWrapper = mock { on { isInitialized() } doReturn false }

  private val activity = Robolectric.buildActivity(Activity::class.java).get()

  // region Setup
  @Before
  fun setUp() {
    adapter = FyberMediationAdapter()
    mockInneractiveAdManager = mockStatic(InneractiveAdManager::class.java)
    FyberSdkWrapper.delegate = mockSdkWrapper
  }

  @After
  fun tearDown() {
    mockInneractiveAdManager.close()
  }

  // endregion

  // region Version Tests
  @Test
  fun getSdkVersion_returnsCorrectVersion() {
    mockStatic(FyberAdapterUtils::class.java).use {
      whenever(FyberAdapterUtils.getSdkVersion()) doReturn "1.2.3"

      adapter.assertGetSdkVersion(expectedValue = "1.2.3")
    }
  }

  @Test
  fun getSdkVersion_withMissingValues_returnsZeroes() {
    mockStatic(FyberAdapterUtils::class.java).use {
      whenever(FyberAdapterUtils.getSdkVersion()) doReturn "1.2"

      adapter.assertGetSdkVersion(expectedValue = "0.0.0")
    }
  }

  @Test(expected = NumberFormatException::class)
  fun getSdkVersion_withInvalidValues_returnsZeroes() {
    mockStatic(FyberAdapterUtils::class.java).use {
      whenever(FyberAdapterUtils.getSdkVersion()) doReturn "INVALID-1.2.3"

      adapter.assertGetSdkVersion(expectedValue = "0.0.0")
    }
  }

  @Test
  fun getVersionInfo_returnsSameVersion() {
    mockStatic(FyberAdapterUtils::class.java).use {
      whenever(FyberAdapterUtils.getAdapterVersion()) doReturn "1.2.3.0"

      adapter.assertGetVersionInfo(expectedValue = "1.2.300")
    }
  }

  @Test
  fun getVersionInfo_withMissingValue_returnsZeroes() {
    mockStatic(FyberAdapterUtils::class.java).use {
      whenever(FyberAdapterUtils.getAdapterVersion()) doReturn "1.2.3"

      adapter.assertGetVersionInfo(expectedValue = "0.0.0")
    }
  }

  // endregion

  // region Initialization Tests
  @Test
  fun initialize_initializesInneractiveAdManager() {
    val initializationParameters = createMediationConfiguration(AdFormat.BANNER, serverParameters)
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters),
    )

    mockInneractiveAdManager.verify {
      InneractiveAdManager.initialize(eq(activity), eq(AdapterTestKitConstants.TEST_APP_ID), any())
    }
  }

  @Test
  fun initialize_invokesOnInitializationSucceeded() {
    val listener = argumentCaptor<OnFyberMarketplaceInitializedListener>()
    val initializationParameters = createMediationConfiguration(AdFormat.BANNER, serverParameters)
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters),
    )

    mockInneractiveAdManager.verify {
      InneractiveAdManager.initialize(
        eq(activity),
        eq(AdapterTestKitConstants.TEST_APP_ID),
        listener.capture(),
      )
    }
    listener.firstValue.onFyberMarketplaceInitialized(FyberInitStatus.SUCCESSFULLY)
    verify(mockInitializationCompleteCallback).onInitializationSucceeded()
  }

  @Test
  fun initialize_withFyberFailedStatus_invokesOnInitializationFailed() {
    val listener = argumentCaptor<OnFyberMarketplaceInitializedListener>()
    val initializationParameters = createMediationConfiguration(AdFormat.BANNER, serverParameters)
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters),
    )

    mockInneractiveAdManager.verify {
      InneractiveAdManager.initialize(
        eq(activity),
        eq(AdapterTestKitConstants.TEST_APP_ID),
        listener.capture(),
      )
    }

    listener.firstValue.onFyberMarketplaceInitialized(FyberInitStatus.FAILED)
    verify(mockInitializationCompleteCallback)
      .onInitializationFailed(
        "DT Exchange failed to initialize with reason: ${FyberInitStatus.FAILED}"
      )
  }

  @Test
  fun initialize_withFyberAlreadyInitialized_invokesOnInitializationSucceeded() {
    whenever(mockSdkWrapper.isInitialized()) doReturn true

    val initializationParameters = createMediationConfiguration(AdFormat.BANNER, serverParameters)
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters),
    )

    verify(mockInitializationCompleteCallback).onInitializationSucceeded()
  }

  @Test
  fun initialize_withNoValidAppId_invokesOnInitializationFailed() {
    val invalidServerParameters = bundleOf(FyberMediationAdapter.KEY_APP_ID to "")
    val initializationParameters =
      createMediationConfiguration(AdFormat.BANNER, invalidServerParameters)
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters),
    )

    verify(mockInitializationCompleteCallback)
      .onInitializationFailed("DT Exchange SDK requires an appId to be configured on the AdMob UI.")
  }

  @Test
  fun initialize_withMultipleAppIds_initializesOnlyOnceAndInvokesOnInitializationSucceeded() {
    val serverParameters1 = bundleOf(FyberMediationAdapter.KEY_APP_ID to TEST_APP_ID_1)
    val serverParameters2 = bundleOf(FyberMediationAdapter.KEY_APP_ID to TEST_APP_ID_2)
    val initializationParameters1 = createMediationConfiguration(AdFormat.BANNER, serverParameters1)
    val initializationParameters2 = createMediationConfiguration(AdFormat.BANNER, serverParameters2)

    val listener = argumentCaptor<OnFyberMarketplaceInitializedListener>()
    adapter.initialize(
      activity,
      mockInitializationCompleteCallback,
      listOf(initializationParameters1, initializationParameters2),
    )

    mockInneractiveAdManager.verify(
      { InneractiveAdManager.initialize(eq(activity), eq(TEST_APP_ID_2), listener.capture()) },
      times(1),
    )
    mockInneractiveAdManager.verify(
      { InneractiveAdManager.initialize(eq(activity), eq(TEST_APP_ID_1), listener.capture()) },
      never(),
    )

    listener.firstValue.onFyberMarketplaceInitialized(FyberInitStatus.SUCCESSFULLY)
    verify(mockInitializationCompleteCallback).onInitializationSucceeded()
  }

  // endregion

  private companion object {
    const val TEST_APP_ID_1 = "testAppID1"
    const val TEST_APP_ID_2 = "testAppID2"
  }
}
