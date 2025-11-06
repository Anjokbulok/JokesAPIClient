package ph.edu.comteq.jokesapiclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import ph.edu.comteq.jokesapiclient.ui.theme.JokesAPIClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JokesAPIClientTheme {
                val jokesViewModel: JokesViewModel = viewModel()
                var showDialog by remember {mutableStateOf(false)}
                Scaffold(modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton (onClick = { showDialog = true}) {
                                Icon(Icons.Default.Add, "Add New Jokes")
                        }
                    }) { innerPadding ->
                    JokesScreen(
                        viewModel = jokesViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                if (showDialog) {
                    AddJokeDialog(
                        onDismiss =  {showDialog = false},
                        onConfirm =  {setup, punchline ->
                            jokesViewModel.addJoke(setup, punchline)
                            showDialog
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun JokesScreen(viewModel: JokesViewModel, modifier: Modifier = Modifier){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getJokes()

    }

    Column(modifier = modifier.fillMaxWidth()) {
        when (val state = uiState) {
            is JokesUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No jokes to show")
                }
            }
            is JokesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is JokesUiState.Success -> {
                LazyColumn {
                    items(state.jokes) { joke ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ){
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ){
                                Text(text = joke.setup)
                                Text(text = joke.punchline)
                            }
                        }

                    }
                }

            }

            is JokesUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

        }
    }
}

@Composable
fun AddJokeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
){
    var setup by remember {mutableStateOf("")}
    var punchline by remember {mutableStateOf("")}

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {Text("Add Joke")},
        text = {
            Column {
                OutlinedTextField(
                    value = setup, onValueChange = { setup = it },
                    label = { Text("setup") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = punchline, onValueChange = { punchline = it },
                    label = { Text("punchline") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (setup.isNotBlank() && punchline.isNotBlank()){
                    onConfirm(setup,punchline)
                }
            },
                enabled = setup.isNotBlank() && punchline.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}