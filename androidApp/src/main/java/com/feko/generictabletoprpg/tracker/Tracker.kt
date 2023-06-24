package com.feko.generictabletoprpg.com.feko.generictabletoprpg.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.feko.generictabletoprpg.Navigation
import com.feko.generictabletoprpg.R
import com.feko.generictabletoprpg.common.OverviewScreen
import com.feko.generictabletoprpg.theme.Typography
import com.feko.generictabletoprpg.tracker.TrackedThing
import org.koin.androidx.compose.koinViewModel

object Tracker : OverviewScreen<TrackerViewModel, TrackedThing>() {
    override val screenTitle: String
        get() = "Tracker"
    override val route: String
        get() = "tracker"
    override val isRootDestination: Boolean
        get() = true
    override val detailsNavRouteProvider: Navigation.DetailsNavRouteProvider
        get() = object : Navigation.DetailsNavRouteProvider {
            override fun getNavRoute(id: Long): String {
                throw UnsupportedOperationException(
                    "Details nav route provider should not be used from the search all screen."
                )
            }
        }
    override val isFabEnabled: Boolean
        get() = true
    override val isFabDropdownMenuEnabled: Boolean
        get() = true

    @Composable
    override fun getViewModel(): TrackerViewModel = koinViewModel()

    @Composable
    public override fun OverviewListItem(item: TrackedThing, navController: NavHostController) {
        Card(Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp)
            ) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.name,
                        style = Typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Divider(
                        Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .padding(vertical = 4.dp)
                    )
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(85.dp)
                    ) {
                        Text(item.getPrintableValue())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) row@{
                            if (item.type == TrackedThing.Type.Percentage) {
                                return@row
                            }
                            Text(item.type.name, style = Typography.bodySmall)
                            if (item is TrackedThing.SpellSlot) {
                                Text("Lv ${item.level}", style = Typography.bodySmall)
                            }
                        }
                    }
                }
                when (item.type) {
                    TrackedThing.Type.Percentage -> PercentageActions(item)
                    TrackedThing.Type.Health -> HealthActions(item)
                    TrackedThing.Type.Ability -> AbilityActions(item)
                    TrackedThing.Type.SpellSlot -> SpellSlotActions(item)
                    TrackedThing.Type.None -> {}
                }
            }
        }
    }

    @Composable
    private fun PercentageActions(item: TrackedThing) {
        ItemActionsBase { viewModel ->
            IconButton(
                onClick = { viewModel.addToPercentageRequested(item) },
                enabled = item.canAdd()
            ) {
                Icon(Icons.Default.Add, "")
            }
            IconButton(
                onClick = { viewModel.subtractFromPercentageRequested(item) },
                enabled = item.canSubtract()
            ) {
                Icon(painterResource(R.drawable.subtract), "")
            }
        }
    }

    @Composable
    private fun HealthActions(item: TrackedThing) {
        ItemActionsBase { viewModel ->
            IconButton(
                onClick = { viewModel.healRequested(item) },
                enabled = item.canAdd()
            ) {
                Icon(painterResource(R.drawable.heart_plus), "")
            }
            IconButton(
                onClick = { viewModel.takeDamageRequested(item) },
                enabled = item.canSubtract()
            ) {
                Icon(painterResource(R.drawable.heart_minus), "")
            }
            IconButton(
                onClick = { viewModel.resetValueToDefault(item) },
                enabled = item.canAdd()
            ) {
                Icon(Icons.Default.Refresh, "")
            }
        }
    }

    @Composable
    private fun AbilityActions(item: TrackedThing) {
        ItemActionsBase { viewModel ->
            IconButton(
                onClick = { viewModel.useAbility(item) },
                enabled = item.canSubtract()
            ) {
                Icon(painterResource(R.drawable.subtract), "")
            }
            IconButton(
                onClick = { viewModel.resetValueToDefault(item) },
                enabled = item.canAdd()
            ) {
                Icon(Icons.Default.Refresh, "")
            }
        }
    }

    @Composable
    private fun SpellSlotActions(item: TrackedThing) {
        ItemActionsBase { viewModel ->
            IconButton(
                onClick = { viewModel.useSpell(item) },
                enabled = item.canSubtract()
            ) {
                Icon(painterResource(R.drawable.subtract), "")
            }
            IconButton(
                onClick = { viewModel.resetValueToDefault(item) },
                enabled = item.canAdd()
            ) {
                Icon(Icons.Default.Refresh, "")
            }
        }
    }

    @Composable
    private fun ItemActionsBase(
        actions: @Composable (TrackerViewModel) -> Unit
    ) {
        val viewModel = koinViewModel<TrackerViewModel>()
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            actions(viewModel)
        }
    }

    @Composable
    override fun DropdownMenuContent(viewModel: TrackerViewModel) {
        TrackedThing.Type
            .values()
            .drop(1) // None is dropped
            .forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        viewModel.showCreateDialog(type)
                    })
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun AlertDialogComposable(viewModel: TrackerViewModel) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDialog() },
            properties = DialogProperties()
        ) {
            Card {
                when (viewModel.dialogType) {
                    TrackerViewModel.DialogType.Create -> EditDialog(viewModel)
                    TrackerViewModel.DialogType.AddPercentage,
                    TrackerViewModel.DialogType.ReducePercentage ->
                        ChangePercentageDialog(viewModel)

                    TrackerViewModel.DialogType.HealHealth,
                    TrackerViewModel.DialogType.DamageHealth ->
                        ChangeHealthDialog(viewModel)
                }
            }
        }
    }

    @Composable
    private fun EditDialog(viewModel: TrackerViewModel) {
        DialogBase(viewModel) {
            val type by viewModel.editedTrackedThingType.collectAsState()
            NameTextField(viewModel, autoFocus = true)
            SpellSlotLevelTextField(type, viewModel)
            ValueTextField(viewModel, type) { viewModel.setValue(it) }
        }
    }

    @Composable
    private fun ChangePercentageDialog(viewModel: TrackerViewModel) {
        DialogBase(viewModel) {
            ValueTextField(
                viewModel,
                TrackedThing.Type.Percentage,
                autoFocus = true
            ) { viewModel.updateValueInputField(it) }
        }
    }

    @Composable
    private fun ChangeHealthDialog(viewModel: TrackerViewModel) {
        DialogBase(viewModel) {
            ValueTextField(
                viewModel,
                TrackedThing.Type.Health,
                autoFocus = true
            ) { viewModel.updateValueInputField(it) }
        }
    }

    @Composable
    private fun DialogBase(
        viewModel: TrackerViewModel,
        inputFields: @Composable () -> Unit
    ) {
        Column(
            Modifier.padding(16.dp),
            Arrangement.spacedBy(16.dp)
        ) {
            DialogTitle(viewModel)
            inputFields()
            val buttonEnabled by viewModel.confirmButtonEnabled.collectAsState()
            TextButton(
                onClick = { viewModel.confirmDialogAction() },
                enabled = buttonEnabled,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(16.dp)
                    .align(Alignment.End)
            ) {
                Text("Confirm")
            }
        }
    }

    @Composable
    private fun NameTextField(
        viewModel: TrackerViewModel,
        autoFocus: Boolean = false
    ) {
        val focusRequester = remember { FocusRequester() }
        val nameInputData by viewModel.editedTrackedThingName.collectAsState()
        val focusManager = LocalFocusManager.current
        TextField(
            value = nameInputData.value,
            onValueChange = { viewModel.setName(it) },
            isError = !nameInputData.isValid,
            label = {
                Text(
                    "Name",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { viewModel.setName("") }
                ) {
                    Icon(Icons.Default.Clear, "")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        if (autoFocus) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }

    @Composable
    private fun SpellSlotLevelTextField(
        type: TrackedThing.Type,
        viewModel: TrackerViewModel
    ) {
        val focusManager = LocalFocusManager.current
        if (type == TrackedThing.Type.SpellSlot) {
            val spellSlotLevelInputData
                    by viewModel.editedTrackedThingSpellSlotLevel.collectAsState()
            TextField(
                value = spellSlotLevelInputData.value,
                onValueChange = { viewModel.setLevel(it) },
                isError = !spellSlotLevelInputData.isValid,
                label = {
                    Text(
                        "Level",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { viewModel.setLevel("") }
                    ) {
                        Icon(Icons.Default.Clear, "")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )
        }
    }

    @Composable
    private fun ValueTextField(
        viewModel: TrackerViewModel,
        type: TrackedThing.Type,
        autoFocus: Boolean = false,
        updateValue: (String) -> Unit
    ) {
        val focusRequester = remember { FocusRequester() }
        val valueInputData by viewModel.editedTrackedThingValue.collectAsState()
        TextField(
            value = valueInputData.value,
            onValueChange = { updateValue(it) },
            isError = !valueInputData.isValid,
            suffix = {
                if (type == TrackedThing.Type.Percentage) {
                    Text("%")
                }
            },
            label = {
                Text(
                    "Amount",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { updateValue("") }
                ) {
                    Icon(Icons.Default.Clear, "")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.confirmDialogAction() }
            )
        )
        if (autoFocus) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}