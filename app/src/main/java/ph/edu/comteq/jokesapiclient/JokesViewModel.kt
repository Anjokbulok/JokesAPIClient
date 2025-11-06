package ph.edu.comteq.jokesapiclient


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


sealed class JokesUiState{
    object Idle: JokesUiState() // initial state
    object Loading: JokesUiState() // fetching data
    data class Success(val jokes: List<Joke>): JokesUiState() //date fetched successful
    data class Error(val message: String): JokesUiState() // failed
}


class JokesViewModel : ViewModel() {
    private val api = RetrofitInstance.jokesAPI

    private val _uiState = MutableStateFlow<JokesUiState>(JokesUiState.Idle)

    //public can be observed
    val uiState: MutableStateFlow<JokesUiState> = _uiState

    //fetch data
    fun getJokes() {
        viewModelScope.launch {
            _uiState.value = JokesUiState.Loading
            try {
                val jokes = api.getJokes()
                _uiState.value = JokesUiState.Success(jokes)
            } catch (e: Exception) {
                _uiState.value = JokesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addJoke(setup: String, punchline: String){
        viewModelScope.launch{
            try{
                val newJoke = Joke(id = null, setup = setup, punchline = punchline)
                api.addJoke(newJoke)
                getJokes()
            } catch (e: Exception){
                _uiState.value = JokesUiState.Error(
                    e.message ?: "Unknown Error"
                )
            }
        }
    }
}