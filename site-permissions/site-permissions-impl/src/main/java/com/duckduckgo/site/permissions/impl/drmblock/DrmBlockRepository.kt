/*
 * Copyright (c) 2023 DuckDuckGo
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

package com.duckduckgo.site.permissions.impl.drmblock

import com.duckduckgo.app.di.AppCoroutineScope
import com.duckduckgo.app.di.IsMainProcess
import com.duckduckgo.common.utils.DispatcherProvider
import com.duckduckgo.di.scopes.AppScope
import com.duckduckgo.feature.toggles.api.FeatureExceptions.FeatureException
import com.duckduckgo.site.permissions.store.drmblock.DrmBlockDao
import com.duckduckgo.site.permissions.store.drmblock.DrmBlockExceptionEntity
import com.duckduckgo.site.permissions.store.drmblock.toFeatureException
import com.squareup.anvil.annotations.ContributesBinding
import dagger.SingleInstanceIn
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface DrmBlockRepository {
    fun updateAll(exceptions: List<DrmBlockExceptionEntity>)
    val exceptions: CopyOnWriteArrayList<FeatureException>
}

@ContributesBinding(AppScope::class)
@SingleInstanceIn(AppScope::class)
class RealDrmBlockRepository @Inject constructor(
    val drmBlockDao: DrmBlockDao,
    @AppCoroutineScope appCoroutineScope: CoroutineScope,
    dispatcherProvider: DispatcherProvider,
    @IsMainProcess isMainProcess: Boolean,
) : DrmBlockRepository {

    override val exceptions = CopyOnWriteArrayList<FeatureException>()

    init {
        appCoroutineScope.launch(dispatcherProvider.io()) {
            if (isMainProcess) {
                loadToMemory()
            }
        }
    }

    override fun updateAll(exceptions: List<DrmBlockExceptionEntity>) {
        drmBlockDao.updateAll(exceptions)
        loadToMemory()
    }

    private fun loadToMemory() {
        exceptions.clear()
        drmBlockDao.getAll().map {
            exceptions.add(it.toFeatureException())
        }
    }
}
