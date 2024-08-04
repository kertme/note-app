package com.rcunal.supernotes

import com.rcunal.supernotes.features.add_note_view_note.common.db.NoteDatabaseTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * @author: R. Cemre Ünal,
 * created on 9/23/2022
 */

@RunWith(Suite::class)
@Suite.SuiteClasses(
    HomeScreenTest::class,
    NoteScreenTest::class,
    NoteDatabaseTest::class,
)
class AllTests