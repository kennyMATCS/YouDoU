package net.youdou.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.alorma.compose.settings.ui.SettingsSwitch

// TODO: put this in it's own class
// TODO: better settings organization. probably only need one page. Haptics divider,
//  Accessibility divider
@Preview
@Composable
fun YouDoUSettings() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            var switchState by remember { mutableStateOf(false) }
            var checkBoxState by remember { mutableStateOf(false) }
            var radioState by remember { mutableIntStateOf(0) }

            Column {
                SettingsGroup {
                    SettingsSwitch(
                        state = switchState, title = { Text("Test Switch") }) {
                        switchState = !switchState
                    }

                    SettingsCheckbox(
                        state = checkBoxState, title = { Text("Test Checkbox") }) {
                        checkBoxState = !checkBoxState
                    }
                }

                SettingsGroup {
                    SettingsRadioButton(
                        state = radioState == 0, title = { Text("Option 1") }) {
                        radioState = 0
                    }

                    SettingsRadioButton(
                        state = radioState == 1, title = { Text("Option 2") }) {
                        radioState = 1
                    }

                    SettingsRadioButton(
                        state = radioState == 2, title = { Text("Option 3") }) {
                        radioState = 2
                    }
                }
            }
        }
    }
}