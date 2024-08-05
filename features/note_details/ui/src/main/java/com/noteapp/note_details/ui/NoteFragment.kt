package com.noteapp.note_details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noteapp.core.domain.toStringWithFormat
import com.noteapp.core.ui.extension.collectFlow
import com.noteapp.core.ui.extension.disable
import com.noteapp.core.ui.extension.enable
import com.noteapp.core.ui.extension.hide
import com.noteapp.core.ui.extension.loadImage
import com.noteapp.core.ui.extension.showSoftKeyboard
import com.noteapp.core.ui.extension.showToast
import com.noteapp.core.ui.extension.themeColor
import com.noteapp.core.ui.extension.show
import com.noteapp.core.ui.viewBinding
import com.noteapp.home.ui.R
import com.noteapp.home.ui.databinding.DialogAddPhotoBinding
import com.noteapp.home.ui.databinding.DialogNoteInfoLayoutBinding
import com.noteapp.home.ui.databinding.FragmentNoteBinding
import com.noteapp.note_details.shared.NoteDetailsCommunicator
import com.noteapp.note_details.shared.model.NoteDetailsType
import com.noteapp.note_details.ui.model.NoteDetailsParcelableArguments
import dagger.hilt.android.AndroidEntryPoint
import com.noteapp.core.ui.R as CoreR


@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_note) {

    private val binding by viewBinding(FragmentNoteBinding::bind)
    private val viewModel by viewModels<NoteViewModel>()

    private val args by lazy {
        arguments?.getParcelable<NoteDetailsParcelableArguments>(NoteDetailsCommunicator.noteDetailsNavKey)
            ?: throw IllegalStateException()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()

        if (args.noteDetailsType == NoteDetailsType.ADD) {
            binding.etTitle.showSoftKeyboard()
        }
        collectFlow(viewModel.state, stateCollector)
        collectFlow(viewModel.event, eventCollector)
    }

    private val stateCollector: suspend (NoteViewModel.UiState) -> Unit = { uiState ->
        when (uiState.currentState) {
            NoteViewModel.State.INITIAL -> viewModel.setUiState(
                args.noteDetailsType,
                args.noteUiModel
            )
            NoteViewModel.State.VIEW_NOTE_DETAILS -> onViewNoteDetailsState(uiState)
            NoteViewModel.State.ADD_NEW_NOTE -> onAddNewNoteState(uiState.photoUrl)
            NoteViewModel.State.EDIT_NOTE -> onEditNoteState(uiState)
        }
    }

    private val eventCollector: suspend (NoteViewModel.Event) -> Unit = { event ->
        when (event) {
            NoteViewModel.Event.NoteAddedSuccessfully -> findNavController().navigateUp()
        }
    }

    private fun onViewNoteDetailsState(uiState: NoteViewModel.UiState) {
        with(binding) {
            etTitle.disable()
            etContent.disable()
            etTitle.setText(uiState.noteUiModel?.title)
            etContent.setText(uiState.noteUiModel?.content)
            ivPhoto.loadImage(uiState.photoUrl)
            ivInfo.show()
            btnSave.hide()
            ivAddPhoto.setImageResource(R.drawable.ic_edit)

        }
    }

    private fun onAddNewNoteState(photoUrl: String?) {
        if (photoUrl.isNullOrEmpty()) {
            binding.ivPhoto.hide()
            binding.ivPhoto.setImageDrawable(null)
            binding.ivAddPhoto.setImageResource(R.drawable.ic_add_photo)
        } else {
            binding.ivPhoto.loadImage(photoUrl)
            binding.ivPhoto.show()
            // Couldn't find any proper drawable so we'll manually change it's color
            binding.ivAddPhoto.setImageResource(R.drawable.ic_remove_photo)
            val primaryColor = requireContext().themeColor(androidx.appcompat.R.attr.colorAccent)
            binding.ivAddPhoto.setColorFilter(
                primaryColor,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    private fun onEditNoteState(uiState: NoteViewModel.UiState) {
        with(binding) {
            etTitle.enable()
            etContent.enable()
            ivInfo.hide()
            btnSave.show()

            if (uiState.photoUrl.isNullOrEmpty()) {
                ivPhoto.hide()
                ivAddPhoto.setImageResource(R.drawable.ic_add_photo)
            } else {
                ivPhoto.show()
                ivPhoto.loadImage(uiState.photoUrl)
                ivAddPhoto.setImageResource(R.drawable.ic_remove_photo)
                val primaryColor =
                    requireContext().themeColor(androidx.appcompat.R.attr.colorAccent)
                ivAddPhoto.setColorFilter(
                    primaryColor,
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }


    private fun showDialogForPhotoInput() {
        val dialogBinding = DialogAddPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext(), CoreR.style.DarkDialog)
            .setTitle(getString(R.string.add_photo))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                viewModel.setPhoto(dialogBinding.etPhoto.text.toString())
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        dialog.show()
    }

    private fun showRemovePhotoDialog() {
        val dialog = AlertDialog.Builder(
            requireContext(),
            CoreR.style.DarkDialog
        )
            .setTitle(getString(R.string.remove_photo))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                viewModel.setPhoto(null)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        dialog.show()
    }

    private fun initListeners() {
        with(binding) {
            ivAddPhoto.setOnClickListener {
                when (viewModel.state.value.currentState) {
                    NoteViewModel.State.VIEW_NOTE_DETAILS -> viewModel.changeUiStateToEditNote()
                    NoteViewModel.State.ADD_NEW_NOTE, NoteViewModel.State.EDIT_NOTE -> {
                        if (viewModel.state.value.photoUrl.isNullOrEmpty()) {
                            showDialogForPhotoInput()
                        } else {
                            showRemovePhotoDialog()
                        }
                    }
                    else -> {}
                }
            }
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnSave.setOnClickListener {
                when {
                    etTitle.text.isNullOrEmpty() -> {
                        showToast(getString(R.string.note_title_not_entered_error))
                    }
                    etContent.text.isNullOrEmpty() -> {
                        showToast(getString(R.string.note_content_not_entered_error))
                    }
                    else -> {
                        viewModel.saveNote(etTitle.text.toString(), etContent.text.toString())
                    }
                }
            }

            ivInfo.setOnClickListener {
                val note = viewModel.state.value.noteUiModel
                val dialogBinding =
                    DialogNoteInfoLayoutBinding.inflate(LayoutInflater.from(requireContext()))
                dialogBinding.tvCreatedDateInfo.text =
                    note?.createDate?.toStringWithFormat()
                val modifyDate = note?.modifyDate
                if (modifyDate != null) {
                    dialogBinding.tvModifiedDateInfo.show()
                    dialogBinding.tvModifiedDateInfo.text =
                        modifyDate.toStringWithFormat()
                } else {
                    dialogBinding.tvModifiedDateTitle.hide()
                    dialogBinding.tvModifiedDateInfo.hide()
                }
                val dialog = AlertDialog.Builder(requireContext(), CoreR.style.DarkDialog)
                    .setView(dialogBinding.root).create()
                dialog.show()
            }
        }
    }
}