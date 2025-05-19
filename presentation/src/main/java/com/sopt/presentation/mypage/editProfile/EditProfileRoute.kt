package com.sopt.presentation.mypage.editProfile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.sopt.core.designsystem.component.button.NoostakBottomButton
import com.sopt.core.designsystem.component.dialog.NoostakDialog
import com.sopt.core.designsystem.component.image.ProfileImagePicker
import com.sopt.core.designsystem.component.snackbar.NoostakSnackBar
import com.sopt.core.designsystem.component.snackbar.SNACK_BAR_DURATION
import com.sopt.core.designsystem.component.textfield.NoostakTextField
import com.sopt.core.designsystem.component.topappbar.NoostakTopAppBar
import com.sopt.core.designsystem.screen.NoostakLoadingScreen
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.launchImagePicker
import com.sopt.core.type.DialogType
import com.sopt.core.type.ImagePickerType
import com.sopt.core.type.TextFieldType
import com.sopt.core.util.permission.ImagePickerLaunchers
import com.sopt.domain.entity.ProfileEntity
import com.sopt.presentation.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EditProfileRoute(
    nickname: String,
    profileImage: String?,
    navigateUp: () -> Unit,
    navigateToMyPage: () -> Unit,
    editProfileViewModel: EditProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val userProfileState by editProfileViewModel.userProfileState.collectAsStateWithLifecycle()

    val showErrorDialog by editProfileViewModel.showErrorDialog.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val snackBarVisible = remember { mutableStateOf(false) }

    val onShowPermissionGallerySnackBar: (message: String) -> Unit = {
        coroutineScope.launch {
            snackBarVisible.value = true
            val job = launch { snackBarHostState.showSnackbar(message = it) }
            delay(SNACK_BAR_DURATION)
            job.cancel()
            snackBarVisible.value = false
        }
    }

    var isVisibleSnackBar by remember { mutableStateOf(false) }
    val permissions = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val galleryLauncher = ImagePickerLaunchers().rememberGalleryLauncher { uri ->
        editProfileViewModel.onImageSelected(uri.toString())
    }
    val photoPickerLauncher = ImagePickerLaunchers().rememberPhotoPickerLauncher { uri ->
        editProfileViewModel.onImageSelected(uri.toString())
    }

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        editProfileViewModel.setInitialProfile(nickname, profileImage)
        isInitialized = true
    }

    LaunchedEffect(lifecycleOwner) {
        editProfileViewModel.sideEffects.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is EditProfileSideEffect.NavigateUp -> navigateUp()
                    is EditProfileSideEffect.NavigateToMyPage -> navigateToMyPage()
                    is EditProfileSideEffect.ShowGallerySnackBar -> isVisibleSnackBar = true
                    is EditProfileSideEffect.ShowErrorDialog -> editProfileViewModel.showErrorDialog(
                        true
                    )

                    is EditProfileSideEffect.RequestImagePicker -> context.launchImagePicker(
                        galleryLauncher,
                        photoPickerLauncher
                    )
                }
            }
    }

    if (isVisibleSnackBar) {
        onShowPermissionGallerySnackBar(context.getString(R.string.sb_permission_gallery))
        isVisibleSnackBar = false
    }

    if (showErrorDialog) {
        NoostakDialog(
            dialogType = DialogType.NETWORK_FAILURE,
            onClick = {
                editProfileViewModel.patchProfile(
                    userProfileState.memberName,
                    editProfileViewModel.isChangedImage().toString(),
                    if (editProfileViewModel.isChangedImage()) userProfileState.memberProfileImage else null
                )
            },
            onDismissRequest = { editProfileViewModel.showErrorDialog(false) }
        )
    }

    if (isInitialized) {
        EditProfileScreen(
            snackBarHostState = snackBarHostState,
            snackBarVisible = snackBarVisible,
            onBackButtonClick = editProfileViewModel::navigateUp,
            userProfileState = userProfileState,
            onProfileCameraBtnClick = {
                if (permissions.any {
                    ContextCompat.checkSelfPermission(
                            context,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                }
                ) {
                    editProfileViewModel.updateGalleryPermissionState(true)
                } else {
                    isVisibleSnackBar = true
                }

                editProfileViewModel.requestGalleryPicker()
            },
            onNameChange = { newName ->
                editProfileViewModel.onMemberNameChanged(newName)
            },
            onNextBtnClick = { memberName, memberProfileImage ->
                editProfileViewModel.patchProfile(
                    memberName,
                    editProfileViewModel.isChangedImage().toString(),
                    if (editProfileViewModel.isChangedImage()) memberProfileImage else null
                )
            },
            isNextBtnActive = (userProfileState.isMemberNameCheck && editProfileViewModel.validateProfile())
        )
    } else {
        NoostakLoadingScreen()
    }
}

@Composable
fun EditProfileScreen(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    snackBarVisible: MutableState<Boolean> = remember { mutableStateOf(false) },
    onBackButtonClick: () -> Unit,
    userProfileState: ProfileEntity,
    onProfileCameraBtnClick: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onNextBtnClick: (String, String?) -> Unit,
    isNextBtnActive: Boolean = false
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            NoostakTopAppBar(
                title = stringResource(R.string.btn_my_page_profile_edit),
                modifier = Modifier,
                isIconVisible = true,
                onBackButtonClick = onBackButtonClick
            )
        },
        snackbarHost = {
            AnimatedVisibility(
                visible = snackBarVisible.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SnackbarHost(
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.bottom_padding_snack_bar_gallery_permission)),
                    hostState = snackBarHostState,
                    snackbar = { snackBarData ->
                        NoostakSnackBar(
                            message = snackBarData.visuals.message,
                            textStyle = NoostakTheme.typography.c3SemiBold,
                            textColor = NoostakTheme.colors.red01,
                            backgroundColor = NoostakTheme.colors.pink
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dimensionResource(id = R.dimen.horizontal_padding))
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                Spacer(modifier = Modifier.height(37.dp))
                ProfileImagePicker(
                    imagePickerType = ImagePickerType.USER,
                    selectedImageUri = userProfileState.memberProfileImage,
                    onCameraBtnClick = onProfileCameraBtnClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(32.dp))
                NoostakTextField(
                    textFieldType = TextFieldType.EDITPROFILE,
                    value = userProfileState.memberName,
                    onValueChange = { onNameChange(it) },
                    maxLength = 10,
                    lengthTextStyle = NoostakTheme.typography.c3Regular
                )
            }
            NoostakBottomButton(
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.vertical_padding)),
                text = stringResource(R.string.btn_group_create_next),
                activateColor = NoostakTheme.colors.blue600,
                deactivateColor = NoostakTheme.colors.gray500,
                isEnabled = isNextBtnActive,
                onButtonClick = {
                    onNextBtnClick(
                        userProfileState.memberName,
                        userProfileState.memberProfileImage
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupCreateScreenPreview() {
    NoostakAndroidTheme {
        EditProfileScreen(
            onBackButtonClick = {},
            userProfileState = ProfileEntity(
                memberName = "호크스",
                memberProfileImage = null
            ),
            onProfileCameraBtnClick = {},
            onNameChange = {},
            onNextBtnClick = { _, _ -> }
        )
    }
}
