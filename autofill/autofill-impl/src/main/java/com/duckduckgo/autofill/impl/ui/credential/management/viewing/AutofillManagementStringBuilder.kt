/*
 * Copyright (c) 2024 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.autofill.impl.ui.credential.management.viewing

import android.content.Context
import android.content.res.Resources
import com.duckduckgo.autofill.impl.R
import com.duckduckgo.di.scopes.FragmentScope
import com.duckduckgo.sync.api.DeviceSyncState
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface AutofillManagementStringBuilder {
    fun stringForDeleteLoginsConfirmationDialogMessage(numberToDelete: Int): String
    fun stringForDeleteAllPasswordsDialogConfirmationDialogTitle(numberToDelete: Int): String
}

@ContributesBinding(FragmentScope::class)
class AutofillManagementStringBuilderImpl @Inject constructor(
    private val context: Context,
    private val deviceSyncState: DeviceSyncState,
) : AutofillManagementStringBuilder {

    override fun stringForDeleteAllPasswordsDialogConfirmationDialogTitle(numberToDelete: Int): String {
        return context.resources.getQuantityString(
            R.plurals.credentialManagementDeleteAllPasswordsDialogConfirmationTitle,
            numberToDelete,
            numberToDelete,
        )
    }

    override fun stringForDeleteLoginsConfirmationDialogMessage(numberToDelete: Int): String {
        with(context.resources) {
            val firstMessage = deleteAllPasswordsWarning(numberToDelete)
            val secondMessage = getQuantityString(R.plurals.credentialManagementDeleteAllSecondInstruction, numberToDelete)

            return "$firstMessage $secondMessage"
        }
    }

    private fun Resources.deleteAllPasswordsWarning(numberToDelete: Int): String {
        val stringResId = if (deviceSyncState.isUserSignedInOnDevice()) {
            R.plurals.credentialManagementDeleteAllPasswordsFirstInstructionSynced
        } else {
            R.plurals.credentialManagementDeleteAllPasswordsDialogFirstInstructionNotSynced
        }
        return getQuantityString(stringResId, numberToDelete)
    }
}
