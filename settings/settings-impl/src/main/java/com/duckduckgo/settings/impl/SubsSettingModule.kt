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

package com.duckduckgo.settings.impl

import com.duckduckgo.di.DaggerMap
import com.duckduckgo.di.scopes.ActivityScope
import com.duckduckgo.settings.api.SubsSettingsPlugin
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(ActivityScope::class)
abstract class BillingsModule {
    // we use multibinds as the list of plugins can be empty
    @Multibinds
    abstract fun provideSubsSettingsPlugins(): DaggerMap<Int, SubsSettingsPlugin>
}
