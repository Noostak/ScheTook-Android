package com.sopt.presentation.mypage.editProfile

import androidx.lifecycle.viewModelScope
import com.sopt.core.state.UiState
import com.sopt.core.util.BaseViewModel
import com.sopt.domain.entity.ProfileEntity
import com.sopt.domain.repository.ProfileRepository
import com.sopt.domain.repository.UserInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val profileRepository: ProfileRepository
) : BaseViewModel<EditProfileSideEffect>() {
    private val _userProfileState = MutableStateFlow(ProfileEntity())
    val userProfileState: StateFlow<ProfileEntity> = _userProfileState

    private val _patchProfileState: MutableStateFlow<UiState<Unit>> =
        MutableStateFlow(UiState.Empty)

    private val _nickname = MutableStateFlow("")
    private val nickname: StateFlow<String> = _nickname

    private val _profileImage = MutableStateFlow("")
    private val profileImage: StateFlow<String?> = _profileImage

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> get() = _showErrorDialog

    fun setInitialProfile(memberName: String, memberProfileImage: String?) {
        _nickname.value = memberName
        _profileImage.value = memberProfileImage ?: ""
        _userProfileState.update {
            it.copy(
                memberName = memberName,
                memberProfileImage = memberProfileImage
            )
        }
        validateMemberName(memberName)
    }

    private suspend fun saveProfile(memberName: String, memberProfileImage: String?) {
        userInfoRepository.saveNickname(memberName)
        userInfoRepository.saveProfileImage(memberProfileImage ?: "")
    }

    fun patchProfile(memberName: String, profileImageUpdated: String, memberProfileImage: String?) {
        viewModelScope.launch {
            _patchProfileState.emit(UiState.Loading)
            profileRepository.patchProfile(memberName, profileImageUpdated, memberProfileImage)
                .fold(onSuccess = {
                    saveProfile(
                        userProfileState.value.memberName,
                        userProfileState.value.memberProfileImage
                    )
                    _patchProfileState.emit(UiState.Success(it))
                    emitSideEffect(EditProfileSideEffect.NavigateToMyPage)
                }, onFailure = {
                        _patchProfileState.emit(UiState.Failure(it.message.toString()))
                        triggerErrorDialog()
                    })
        }
    }

    fun showErrorDialog(show: Boolean) {
        _showErrorDialog.update { show }
    }

    private fun triggerErrorDialog() {
        emitSideEffect(EditProfileSideEffect.ShowErrorDialog)
    }

    fun navigateUp() {
        emitSideEffect(EditProfileSideEffect.NavigateUp)
    }

    fun requestGalleryPicker() {
        if (_userProfileState.value.isPermissionGranted) {
            emitSideEffect(EditProfileSideEffect.RequestImagePicker)
        } else {
            emitSideEffect(EditProfileSideEffect.ShowGallerySnackBar)
        }
    }

    fun updateGalleryPermissionState(isGranted: Boolean) {
        _userProfileState.update { it.copy(isPermissionGranted = isGranted) }
    }

    fun onImageSelected(imageUri: String?) {
        viewModelScope.launch {
            _userProfileState.update { it.copy(memberProfileImage = imageUri) }
        }
    }

    fun isChangedImage(): Boolean {
        return profileImage.value != userProfileState.value.memberProfileImage
    }

    fun onMemberNameChanged(memberName: String) {
        _userProfileState.update { it.copy(memberName = memberName) }
        validateMemberName(memberName)
    }

    private fun validateMemberName(memberName: String) {
        viewModelScope.launch {
            _userProfileState.update {
                it.copy(
                    isMemberNameCheck = (memberName.isNotBlank() && memberName.length in 1..10 && memberName.all { name -> name.isLetterOrDigit() })
                )
            }
        }
    }

    fun validateProfile(): Boolean {
        return (nickname.value != userProfileState.value.memberName || profileImage.value != userProfileState.value.memberProfileImage)
    }
}
