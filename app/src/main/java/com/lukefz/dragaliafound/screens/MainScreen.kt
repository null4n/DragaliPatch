package com.lukefz.dragaliafound.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lukefz.dragaliafound.R
import com.lukefz.dragaliafound.composable.AppContainer
import com.lukefz.dragaliafound.composable.SpacedLine
import com.lukefz.dragaliafound.navigation.NavScreens
import com.lukefz.dragaliafound.utils.PatcherConfig
import com.lukefz.dragaliafound.utils.Constants

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(navController: NavController, model: MainScreenViewModel = viewModel()) {
    val appName by remember { model.customAppName }
    val serverUrl by remember { model.customServerUrl }
    val cdnUrl by remember { model.customCdnUrl }

    val context = LocalContext.current

    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { selectedUri ->
        if (selectedUri != null)
            model.handleOpenDocumentTree(DocumentFile.fromTreeUri(context, selectedUri)!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                /*navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(NavScreens.Patcher.route) }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "About button")
                    }
                }*/
            )
        },
        floatingActionButton = {
            if (model.isPatchable &&
                model.estimateUrlLength(serverUrl) <= Constants.URL_MAX_LENGTH &&
                serverUrl.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        PatcherConfig.appName = model.customAppName.value
                        PatcherConfig.apiUrl = model.customServerUrl.value
                        PatcherConfig.cdnUrl = model.customCdnUrl.value
                        navController.navigate(NavScreens.Patcher.route)
                    },
                    icon = { Icon(Icons.Filled.PlayArrow, "Start button") },
                    text = { Text(stringResource(R.string.activity_patcher_start_patching)) },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                AppContainer(model.originalAppInfo, model.originalAppIcon)

                SpacedLine(2.dp)

                AppContainer(model.patchedAppInfo, model.patchedAppIcon)

                SpacedLine(2.dp)

                OutlinedTextField(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                        .fillMaxWidth(),
                    singleLine = true,
                    value = appName,
                    onValueChange = {
                        model.customAppName.value = it
                    },
                    label = { Text(stringResource(R.string.activity_main_custom_name)) }
                )

                Spacer(Modifier.size(2.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                        .fillMaxWidth(),
                    singleLine = true,
                    value = serverUrl,
                    onValueChange = {
                                    if (it.length <= Constants.URL_MAX_LENGTH)
                                        model.customServerUrl.value = it
                    },
                    isError = model.estimateUrlLength(serverUrl) > Constants.URL_MAX_LENGTH,
                    label = { Text(stringResource(R.string.activity_main_custom_server)) }
                )

                Spacer(Modifier.size(2.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                        .fillMaxWidth(),
                    singleLine = true,
                    value = cdnUrl,
                    onValueChange = {
                        if (it.length <= Constants.CDN_URL_MAX_LENGTH)
                            model.customCdnUrl.value = it
                    },
                    isError = model.estimateUrlLength(cdnUrl, false) > Constants.CDN_URL_MAX_LENGTH,
                    label = { Text(stringResource(R.string.activity_main_cdn_server)) }
                )

                SpacedLine(4.dp)

                Button(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        model.startOriginalGameBackup()
                        intentLauncher.launch(null)
                    },
                    enabled = model.isPatchable,

                ) {
                    Text(stringResource(R.string.app_backup_button))
                }

                Button(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                        .fillMaxWidth(),
                    onClick = {
                        model.startModifiedGameBackup()
                        intentLauncher.launch(null)
                    },
                    enabled = model.isPatchable,

                    ) {
                    Text(stringResource(R.string.modified_app_backup_button))
                }

                SpacedLine(4.dp)

                Column(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            start = 2.dp,
                            end = 2.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.activity_about_credits),
                        style = MaterialTheme.typography.headlineSmall,
                    )

                    Spacer(Modifier.size(4.dp))

                    ClickableText(
                        modifier = Modifier.padding(start = 4.dp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) {
                                append(stringResource(R.string.activity_about_credits_based_on))
                            }
                        },
                        onClick = {
                            model.openWebsite(Constants.PROJECT_EARTH_GITHUB_URL)
                        }
                    )

                    Spacer(Modifier.size(4.dp))

                    ClickableText(
                        modifier = Modifier.padding(start = 4.dp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) {
                                append(stringResource(R.string.activity_about_credits_app_creator))
                            }
                        },
                        onClick = {
                            model.openWebsite(Constants.GITHUB_URL)
                        }
                    )

                    SpacedLine(8.dp)

                    Text(
                        text = stringResource(R.string.activity_about_special_thanks),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.size(4.dp))

                    Text(
                        text = stringResource(R.string.activity_about_custom_server_creators),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = stringResource(R.string.activity_about_custom_server_creators_reason),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}