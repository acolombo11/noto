package com.noto.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val storage: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SettingsRepository {

    override val config: Flow<SettingsConfig> = storage.data.map {
        SettingsConfig(
            theme.first(),
            font.first(),
            language.first(),
            vaultPasscode.first(),
            vaultTimeout.first(),
            scheduledVaultTimeout.first(),
            isVaultOpen.first(),
            isBioAuthEnabled.first(),
            lastVersion.first(),
            sortingType.first(),
            sortingOrder.first(),
            isShowNotesCount.first(),
            mainFolderId.first()
        )
    }.flowOn(dispatcher)

    override val theme: Flow<Theme> = storage.data
        .map { preferences -> preferences[SettingsKeys.Theme] }
        .map { if (it != null) Theme.valueOf(it) else Theme.System }
        .flowOn(dispatcher)

    override val font: Flow<Font> = storage.data
        .map { preferences -> preferences[SettingsKeys.Font] }
        .map { if (it != null) Font.valueOf(it) else Font.Nunito }
        .flowOn(dispatcher)

    override val language: Flow<Language> = storage.data
        .map { preferences -> preferences[SettingsKeys.Language] }
        .map { if (it != null) Language.valueOf(it) else Language.System }
        .flowOn(dispatcher)

    override val vaultPasscode: Flow<String?> = storage.data
        .map { preferences -> preferences[SettingsKeys.VaultPasscode] }
        .flowOn(dispatcher)

    override val vaultTimeout: Flow<VaultTimeout> = storage.data
        .map { preferences -> preferences[SettingsKeys.VaultTimeout] }
        .map { if (it != null) VaultTimeout.valueOf(it) else VaultTimeout.Immediately }
        .flowOn(dispatcher)

    override val scheduledVaultTimeout: Flow<VaultTimeout?> = storage.data
        .map { preferences -> preferences[SettingsKeys.ScheduledVaultTimeout] }
        .map { if (it != null) VaultTimeout.valueOf(it) else null }
        .flowOn(dispatcher)

    override val isVaultOpen: Flow<Boolean> = storage.data
        .map { preferences -> preferences[SettingsKeys.IsVaultOpen] }
        .map { it.toBoolean() }
        .flowOn(dispatcher)

    override val isBioAuthEnabled: Flow<Boolean> = storage.data
        .map { preferences -> preferences[SettingsKeys.IsBioAuthEnabled] }
        .map { it.toBoolean() }
        .flowOn(dispatcher)

    override val isDoNotDisturb: Flow<Boolean> = storage.data
        .map { preferences -> preferences[SettingsKeys.IsDoNotDisturb] ?: false }
        .flowOn(dispatcher)

    override val lastVersion: Flow<String> = storage.data
        .map { preferences -> preferences[SettingsKeys.LastVersion] }
        .map { it ?: Release.Version.Last }
        .flowOn(dispatcher)

    override val sortingType: Flow<FolderListSortingType> = storage.data
        .map { preferences -> preferences[SettingsKeys.FolderListSortingType] }
        .map { if (it != null) FolderListSortingType.valueOf(it) else FolderListSortingType.CreationDate }
        .flowOn(dispatcher)

    override val sortingOrder: Flow<SortingOrder> = storage.data
        .map { preferences -> preferences[SettingsKeys.FolderListSortingOrder] }
        .map { if (it != null) SortingOrder.valueOf(it) else SortingOrder.Descending }
        .flowOn(dispatcher)

    override val isShowNotesCount: Flow<Boolean> = storage.data
        .map { preferences -> preferences[SettingsKeys.ShowNotesCount] }
        .map { it.toBoolean() }
        .flowOn(dispatcher)

    override val mainFolderId: Flow<Long> = storage.data
        .map { preferences -> preferences[SettingsKeys.MainFolderId] }
        .map { it ?: Folder.GeneralFolderId }
        .flowOn(dispatcher)

    override fun getWidgetFolderId(widgetId: Int): Flow<Long> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.FolderId(widgetId)] }
            .map { it ?: 0L }
            .flowOn(dispatcher)
    }

    override fun getIsWidgetCreated(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.Id(widgetId)] }
            .map { it.toBoolean() }
            .flowOn(dispatcher)
    }

    override fun getIsWidgetHeaderEnabled(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.Header(widgetId)] }
            .map { it?.toBoolean() ?: true }
            .flowOn(dispatcher)
    }

    override fun getIsWidgetEditButtonEnabled(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.EditButton(widgetId)] }
            .map { it?.toBoolean() ?: true }
            .flowOn(dispatcher)
    }

    override fun getIsWidgetAppIconEnabled(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.AppIcon(widgetId)] }
            .map { it?.toBoolean() ?: true }
            .flowOn(dispatcher)
    }

    override fun getIsWidgetNewItemButtonEnabled(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.NewItemButton(widgetId)] }
            .map { it?.toBoolean() ?: true }
            .flowOn(dispatcher)
    }

    override fun getWidgetNotesCount(widgetId: Int): Flow<Boolean> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.NotesCount(widgetId)] }
            .map { it?.toBoolean() ?: true }
            .flowOn(dispatcher)
    }

    override fun getWidgetRadius(widgetId: Int): Flow<Int> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.Radius(widgetId)] }
            .map { it?.toIntOrNull() ?: 16 }
            .flowOn(dispatcher)
    }

    override fun getWidgetSelectedLabelIds(widgetId: Int, folderId: Long): Flow<List<Long>> {
        return storage.data
            .map { preferences -> preferences[SettingsKeys.Widget.SelectedLabelIds(widgetId, folderId)] }
            .map { it?.toLongList() ?: emptyList() }
            .flowOn(dispatcher)
    }

    override suspend fun updateConfig(config: SettingsConfig) {
        withContext(dispatcher) {
            with(config) {
                updateTheme(theme)
                updateFont(font)
                updateLanguage(language)
                if (vaultPasscode != null) updateVaultPasscode(vaultPasscode)
                updateVaultTimeout(vaultTimeout)
                updateScheduledVaultTimeout(scheduledVaultTimeout)
                updateIsVaultOpen(isVaultOpen)
                updateIsBioAuthEnabled(isBioAuthEnabled)
                updateLastVersion(lastVersion)
                updateSortingType(sortingType)
                updateSortingOrder(sortingOrder)
                updateIsShowNotesCount(isShowNotesCount)
                updateMainFolderId(mainFolderId)
            }
        }
    }

    override suspend fun updateTheme(theme: Theme) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Theme] = theme.toString() }
        }
    }

    override suspend fun updateFont(font: Font) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Font] = font.toString() }
        }
    }

    override suspend fun updateLanguage(language: Language) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Language] = language.toString() }
        }
    }

    override suspend fun updateVaultPasscode(passcode: String) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.VaultPasscode] = passcode }
        }
    }

    override suspend fun updateVaultTimeout(timeout: VaultTimeout) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.VaultTimeout] = timeout.toString() }
        }
    }

    override suspend fun updateScheduledVaultTimeout(timeout: VaultTimeout?) {
        withContext(dispatcher) {
            storage.edit { preferences ->
                if (timeout != null)
                    preferences[SettingsKeys.ScheduledVaultTimeout] = timeout.toString()
                else
                    preferences.remove(SettingsKeys.ScheduledVaultTimeout)
            }
        }
    }

    override suspend fun updateIsVaultOpen(isOpen: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.IsVaultOpen] = isOpen.toString() }
        }
    }

    override suspend fun updateIsBioAuthEnabled(isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.IsBioAuthEnabled] = isEnabled.toString() }
        }
    }

    override suspend fun updateLastVersion(version: String) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.LastVersion] = version }
        }
    }

    override suspend fun updateSortingType(sortingType: FolderListSortingType) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.FolderListSortingType] = sortingType.toString() }
        }
    }

    override suspend fun updateSortingOrder(sortingOrder: SortingOrder) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.FolderListSortingOrder] = sortingOrder.toString() }
        }
    }

    override suspend fun updateIsShowNotesCount(isShow: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.ShowNotesCount] = isShow.toString() }
        }
    }

    override suspend fun updateIsDoNotDisturb(isDoNotDisturb: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.IsDoNotDisturb] = isDoNotDisturb }
        }
    }

    override suspend fun updateMainFolderId(folderId: Long) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.MainFolderId] = folderId }
        }
    }

    override suspend fun updateWidgetFolderId(widgetId: Int, folderId: Long) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.FolderId(widgetId)] = folderId }
        }
    }

    override suspend fun updateIsWidgetCreated(widgetId: Int, isCreated: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.Id(widgetId)] = isCreated.toString() }
        }
    }

    override suspend fun updateIsWidgetHeaderEnabled(widgetId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.Header(widgetId)] = isEnabled.toString() }
        }
    }

    override suspend fun updateIsWidgetEditButtonEnabled(widgetId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.EditButton(widgetId)] = isEnabled.toString() }
        }
    }

    override suspend fun updateIsWidgetAppIconEnabled(widgetId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.AppIcon(widgetId)] = isEnabled.toString() }
        }
    }

    override suspend fun updateIsWidgetNewItemButtonEnabled(widgetId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.NewItemButton(widgetId)] = isEnabled.toString() }
        }
    }

    override suspend fun updateWidgetNotesCount(widgetId: Int, isEnabled: Boolean) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.NotesCount(widgetId)] = isEnabled.toString() }
        }
    }

    override suspend fun updateWidgetRadius(widgetId: Int, radius: Int) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.Radius(widgetId)] = radius.toString() }
        }
    }

    override suspend fun updateWidgetSelectedLabelIds(widgetId: Int, folderId: Long, labelIds: List<Long>) {
        withContext(dispatcher) {
            storage.edit { preferences -> preferences[SettingsKeys.Widget.SelectedLabelIds(widgetId, folderId)] = labelIds.joinToString() }
        }
    }

    private fun String.toLongList() = split(", ").mapNotNull { it.toLongOrNull() }
}