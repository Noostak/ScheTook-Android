package com.sopt.presentation.groupCreate

import androidx.lifecycle.viewModelScope
import com.sopt.core.state.UiState
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.GroupProfileEntity
import com.sopt.domain.entity.GroupSuccessEntity
import com.sopt.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupCreateViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : BaseViewModel<GroupCreateSideEffect>() {
    private val _groupProfileState = MutableStateFlow(GroupProfileEntity())
    val groupProfileState: StateFlow<GroupProfileEntity> = _groupProfileState

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> get() = _showErrorDialog

    private val _postGroupState: MutableStateFlow<UiState<GroupSuccessEntity>> =
        MutableStateFlow(UiState.Empty)

    fun postGroup(groupName: String, groupProfileImage: String?) {
        viewModelScope.launch {
            _postGroupState.emit(UiState.Loading)
            groupRepository.postGroup(groupName, groupProfileImage).fold(
                onSuccess = {
                    _postGroupState.emit(UiState.Success(it))
                    navigateToGroupCreateSuccess(it)
                },
                onFailure = {
                    _postGroupState.emit(UiState.Failure(it.message.toString()))
                    triggerErrorDialog()
                }
            )
        }
    }

    fun showErrorDialog(show: Boolean) {
        _showErrorDialog.update { show }
    }

    private fun triggerErrorDialog() {
        emitSideEffect(GroupCreateSideEffect.ShowErrorDialog)
    }

    private fun navigateToGroupCreateSuccess(groupSuccessData: GroupSuccessEntity) {
        emitSideEffect(
            GroupCreateSideEffect.NavigateToGroupCreateSuccess(
                groupSuccessData.groupId,
                groupSuccessData.groupInvitationCode
            )
        )
    }

    fun requestGalleryPicker() {
        if (_groupProfileState.value.isPermissionGranted) {
            emitSideEffect(GroupCreateSideEffect.RequestImagePicker)
        } else {
            emitSideEffect(GroupCreateSideEffect.ShowSnackBar)
        }
    }

    fun updateGalleryPermissionState(isGranted: Boolean) {
        viewModelScope.launch {
            _groupProfileState.update { it.copy(isPermissionGranted = isGranted) }
        }
    }

    fun onImageSelected(imageUri: String?) {
        viewModelScope.launch {
            _groupProfileState.update { it.copy(selectedImageUri = imageUri) }
        }
    }

    fun onGroupNameChanged(groupName: String) {
        _groupProfileState.update { it.copy(groupName = groupName) }
        validateGroupName(groupName)
    }

    private fun validateGroupName(groupName: String) {
        viewModelScope.launch {
            _groupProfileState.update { it.copy(isGroupNameCheck = groupName.isNotBlank() && groupName.length in 1..30) }
        }
    }
}
