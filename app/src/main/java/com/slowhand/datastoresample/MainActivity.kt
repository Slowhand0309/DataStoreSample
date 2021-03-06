package com.slowhand.datastoresample

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.slowhand.datastoresample.model.settingsDataStore
import com.slowhand.datastoresample.ui.theme.DataStoreSampleTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val TEXT_KEY = stringPreferencesKey("example_text")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DataStoreSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val coroutineScope = rememberCoroutineScope()
                    coroutineScope.launch {
                        saveText(this@MainActivity, "sample")

                        val textFlow: Flow<String> = dataStore.data.map { p -> p[TEXT_KEY] ?: "" }
                        textFlow.collect { Log.d("DataStore", "text = $it") }
                    }
                    Greeting("Android")
                }
            }
        }

        GlobalScope.launch {
            incrementCounter(this@MainActivity)
            val exampleCounterFlow: Flow<Int> = settingsDataStore.data
                .map { settings ->
                    settings.exampleCounter
                }
            exampleCounterFlow.collect { Log.d("DataStore", "counter = $it")}
        }
    }
}

suspend fun saveText(context: Context, text: String) {
    context.dataStore.edit { settings ->
        settings[TEXT_KEY] = text
    }
}

suspend fun incrementCounter(context: Context) {
    context.settingsDataStore.updateData { currentSettings ->
        currentSettings.toBuilder()
            .setExampleCounter(currentSettings.exampleCounter + 1)
            .build()
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DataStoreSampleTheme {
        Greeting("Android")
    }
}