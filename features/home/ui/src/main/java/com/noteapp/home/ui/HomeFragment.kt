package com.noteapp.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.noteapp.core.ui.extension.isScrollingUp
import com.noteapp.core.ui.extension.toPx
import com.noteapp.home.ui.model.NoteUiModel
import com.noteapp.note_details.shared.NoteDetailsCommunicator
import com.noteapp.note_details.shared.model.NoteDetailsType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var noteDetailsCommunicator: NoteDetailsCommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    val lazyListState = rememberLazyListState()
                    val isScrollingUp = lazyListState.isScrollingUp()

                    Scaffold(
                        containerColor = Color.Transparent,
                        floatingActionButton = {
                            AnimatedVisibility(
                                visible = isScrollingUp,
                                enter = scaleIn(),
                                exit = scaleOut()
                            ) {
                                FloatingActionButton(
                                    modifier = Modifier.padding(bottom = 40.dp, end = 10.dp),
                                    containerColor = colorResource(id = R.color.teal_200),
                                    onClick = ::onFloatingActionButtonClicked
                                ) {
                                    Icon(Icons.Filled.Add, "add icon")
                                }
                            }
                        }
                    ) { paddingValues ->
                        val layoutDirection = LocalLayoutDirection.current

                        val calculatedPaddingValues = remember {
                            PaddingValues(
                                start = paddingValues.calculateStartPadding(layoutDirection) + 20.dp,
                                end = paddingValues.calculateEndPadding(layoutDirection) + 20.dp,
                                top = paddingValues.calculateTopPadding(),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                        }
                        val notes = viewModel.notes.collectAsLazyPagingItems()

                        LazyColumn(
                            modifier = Modifier
                                .padding(calculatedPaddingValues),
                            state = lazyListState,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            item {
                                Text(
                                    modifier = Modifier.padding(top = 40.dp),
                                    text = stringResource(id = R.string.notes),
                                    color = Color.White,
                                    fontSize = 46.sp
                                )
                            }
                            items(
                                count = notes.itemCount,
                                key = { notes[it]?.id ?: 0 }
                            ) { index ->
                                val note = notes[index] ?: return@items

                                DismissibleNoteItem(
                                    note = note,
                                    onClick = ::onNoteClicked,
                                    onDismiss = viewModel::deleteNote,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onNoteClicked(noteUiModel: NoteUiModel) {
        val args = with(noteUiModel) {
            NoteDetailsCommunicator.NoteDetailsArguments(
                noteDetailsType = NoteDetailsType.VIEW,
                id = id,
                createDate = createDate,
                formattedCreateDate = formattedCreateDate,
                modifyDate = modifyDate,
                title = title,
                content = content,
                imageUrl = imageUrl,
            )
        }
        noteDetailsCommunicator.startNoteDetails(
            noteDetailsArguments = args
        )
    }

    private fun onFloatingActionButtonClicked() {
        noteDetailsCommunicator.startNoteDetails(
            noteDetailsArguments = NoteDetailsCommunicator.NoteDetailsArguments(
                noteDetailsType = NoteDetailsType.ADD
            )
        )
    }
}

// todo: add swipe to reveal animation to this
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissibleNoteItem(
    note: NoteUiModel,
    onClick: (note: NoteUiModel) -> Unit,
    onDismiss: (note: NoteUiModel) -> Unit,
) {
    var show by remember { mutableStateOf(true) }
    val currentItem by rememberUpdatedState(note)

    val threshold = 150.dp.toPx()
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                show = false
                true
            } else false
        }, positionalThreshold = { threshold }
    )

    AnimatedVisibility(
        visible = show,
        exit = fadeOut(spring())
    ) {
        SwipeToDismiss(
            state = dismissState,
            directions = setOf(DismissDirection.EndToStart),
            background = {
                DismissBackground(dismissState)
            },
            dismissContent = {
                NoteItem(note = note, onClick = onClick)
            }
        )
    }

    LaunchedEffect(show) {
        if (!show) {
            delay(DISMISS_ANIMATION_DELAY)
            onDismiss(currentItem)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: DismissState) {
    val color = when (dismissState.dismissDirection) {
        DismissDirection.EndToStart -> Color(0xFFFF1744)
        else -> return
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(Icons.Default.Delete, contentDescription = "Delete")
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun NoteItem(note: NoteUiModel, onClick: (note: NoteUiModel) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick(note) },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.nevada))
    ) {
        Row {
            note.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = note.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                note.content?.let {
                    Text(
                        text = it,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            PagingData

            Column(
                modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                CardLabel(text = note.formattedCreateDate)
                Spacer(modifier = Modifier.height(10.dp))
                note.modifyDate?.let {
                    CardLabel(text = stringResource(id = R.string.edited))
                }
            }
        }
    }
}

@Composable
fun CardLabel(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.teal_700)),
    ) {
        Text(
            modifier = Modifier.padding(6.dp),
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun CardLabelPreview(text: String = "Test") {
    CardLabel(text = text)
}

private const val DISMISS_ANIMATION_DELAY = 800L