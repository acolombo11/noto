package com.noto.app.util

import android.content.Context
import com.noto.app.R
import com.noto.app.domain.model.*
import kotlinx.serialization.encodeToString

fun Release.toJson(): String = NotoDefaultJson.encodeToString(this)
fun String.toRelease(): Release = NotoDefaultJson.decodeFromString(this)

fun Release.Changelog.format(context: Context): String = changesIds.joinToString("\n\n") { id -> context.stringResource(id) }

fun Release.Changelog.format(context: Context, count: Int) = changesIds.take(count).joinToString("\n\n") { id -> context.stringResource(id) }
    .let { if (changesIds.count() > count) it.plus("\n\n...") else it }

val Release.Companion.Current: Release
    get() = Release_2_3_0(
        Release.Changelog(
            listOf(
                R.string.release_2_3_0_new_languages,
                R.string.release_2_3_0_reminder,
                R.string.release_2_3_0_selection,
                R.string.release_2_3_0_find_in_note,
                R.string.release_2_3_0_archive,
                R.string.release_2_3_0_sorting,
                R.string.release_2_3_0_reading_mode,
                R.string.release_2_3_0_whats_new,
                R.string.release_2_3_0_report_issues,
                R.string.release_2_3_0_quick_note,
                R.string.release_2_3_0_bug_fixes,
            )
        )
    )

val Release.Companion.Previous: List<Release>
    get() = listOf(
        Release_2_2_3(
            Release.Changelog(
                listOf(
                    R.string.release_2_2_3,
                )
            )
        ),
        Release_2_2_2(
            Release.Changelog(
                listOf(
                    R.string.release_2_2_2,
                )
            )
        ),
        Release_2_2_1(
            Release.Changelog(
                listOf(
                    R.string.release_2_2_1_languages,
                    R.string.release_2_2_1_design,
                    R.string.release_2_2_1_undo_redo,
                    R.string.release_2_2_1_filtered_view,
                    R.string.release_2_2_1_quick_exit,
                    R.string.release_2_2_1_quick_note_tile,
                    R.string.release_2_2_1_system_app_language,
                )
            )
        ),
        Release_2_2_0(
            Release.Changelog(
                listOf(
                    R.string.release_2_2_0_languages,
                    R.string.release_2_2_0_multi_selection,
                    R.string.release_2_2_0_reading_mode,
                    R.string.release_2_2_0_settings,
                    R.string.release_2_2_0_paging,
                    R.string.release_2_2_0_app_speed,
                    R.string.release_2_2_0_import_json,
                    R.string.release_2_2_0_black_theme,
                    R.string.release_2_2_0_bug_fixes,
                )
            )
        ),
        Release_2_1_6(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_6
                )
            )
        ),
        Release_2_1_5(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_5
                )
            )
        ),
        Release_2_1_4(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_4_shortcuts,
                    R.string.release_2_1_4_labels,
                    R.string.release_2_1_4_bug_fixes,
                )
            )
        ),
        Release_2_1_3(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_3
                )
            )
        ),
        Release_2_1_2(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_2
                )
            )
        ),
        Release_2_1_1(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_1
                )
            )
        ),
        Release_2_1_0(
            Release.Changelog(
                listOf(
                    R.string.release_2_1_0_undo_redo,
                    R.string.release_2_1_0_labels,
                    R.string.release_2_1_0_app_bars,
                    R.string.release_2_1_0_menu,
                    R.string.release_2_1_0_new_folder,
                    R.string.release_2_1_0_grouping_order,
                    R.string.release_2_1_0_drag_elevation,
                    R.string.release_2_1_0_preview,
                    R.string.release_2_1_0_dates,
                    R.string.release_2_1_0_highlight,
                    R.string.release_2_1_0_navigation,
                    R.string.release_2_1_0_material_you,
                    R.string.release_2_1_0_splash_screen,
                    R.string.release_2_1_0_reading_mode,
                    R.string.release_2_1_0_scroll_position,
                    R.string.release_2_1_0_disable_animations,
                    R.string.release_2_1_0_import_data,
                    R.string.release_2_1_0_vault_crash,
                    R.string.release_2_1_0_label_filtering,
                    R.string.release_2_1_0_app_icon,
                    R.string.release_2_1_0_change_app_icon,
                    R.string.release_2_1_0_undo_redo_swipe,
                    R.string.release_2_1_0_language,
                    R.string.release_2_1_0_bug_fixes,
                )
            )
        ),
        Release_2_0_1(
            Release.Changelog(
                listOf(
                    R.string.release_2_0_1
                )
            )
        ),
        Release_2_0_0(
            Release.Changelog(
                listOf(
                    R.string.release_2_0_0_new_look,
                    R.string.release_2_0_0_folders,
                    R.string.release_2_0_0_animations,
                    R.string.release_2_0_0_nested_folders,
                    R.string.release_2_0_0_swipe_folder,
                    R.string.release_2_0_0_general,
                    R.string.release_2_0_0_all_recent,
                    R.string.release_2_0_0_highlight_search,
                    R.string.release_2_0_0_collapsing_toolbar,
                    R.string.release_2_0_0_language,
                    R.string.release_2_0_0_swipe_bottom_app_bar,
                    R.string.release_2_0_0_navigation,
                    R.string.release_2_0_0_settings_main_folder,
                    R.string.release_2_0_0_settings_toolbar,
                    R.string.release_2_0_0_shortcuts,
                    R.string.release_2_0_0_bug_fixes,
                )
            )
        ),
        Release_1_8_0(
            Release.Changelog(
                listOf(
                    R.string.release_1_8_0_vault,
                    R.string.release_1_8_0_vault_timeout,
                    R.string.release_1_8_0_vault_bio,
                    R.string.release_1_8_0_language,
                    R.string.release_1_8_0_reddit,
                    R.string.release_1_8_0_bug_fixes,
                )
            )
        ),
    )
