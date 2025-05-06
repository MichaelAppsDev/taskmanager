# Aplikacja Android Task Manager
## Przegląd projektu

### Spis treści
1. [Opis projektu](#opis-projektu)
2. [Architektura](#architektura)
3. [Główne funkcje](#główne-funkcje)
4. [Implementacja techniczna](#implementacja-techniczna)
5. [Ekrany](#ekrany)
6. [Zarządzanie danymi](#zarządzanie-danymi)
7. [Uwierzytelnianie](#uwierzytelnianie)
8. [Planowane rozszerzenia](#planowane-rozszerzenia)

---

## Opis projektu
Task Manager to nowoczesna aplikacja Android zbudowana w Jetpack Compose, która pomaga użytkownikom organizować zadania, kolekcje i przypomnienia. Aplikacja zapewnia czysty, intuicyjny interfejs do zarządzania codziennymi aktywnościami i długoterminowymi projektami.

**Używane technologie:**
- Kotlin
- Jetpack Compose
- Room Database
- Firebase Authentication
- Material Design 3
- Navigation Component

---

## Architektura
Aplikacja wykorzystuje wzorzec architektoniczny MVVM (Model-View-ViewModel):

```kotlin
// Przykład struktury ViewModel
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TaskRepository(
        collectionDao = database.collectionDao(),
        taskDao = database.taskDao(),
        reminderDao = database.reminderDao()
    )
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Metody ViewModel...
}
```

**Główne komponenty:**
- **Modele**: Klasy danych reprezentujące zadania, kolekcje i przypomnienia
- **Widoki**: Komponenty UI Compose
- **ViewModels**: Zarządzanie stanem i logika biznesowa
- **Repozytorium**: Warstwa dostępu do danych
- **Baza danych**: Baza danych Room do lokalnego przechowywania
- **Uwierzytelnianie**: Firebase Auth do zarządzania użytkownikami

---

## Główne funkcje

### 1. Zarządzanie zadaniami
- Tworzenie, edycja i usuwanie zadań
- Organizowanie zadań w kolekcje
- Ustawianie terminów i priorytetów
- Oznaczanie zadań jako zakończone

### 2. Kolekcje
- Grupowanie powiązanych zadań
- Niestandardowe kolory kolekcji
- Ulubione kolekcje
- Funkcjonalność wyszukiwania

### 3. Integracja z kalendarzem
- Przeglądanie zadań według daty
- Widok miesięczny
- Zarządzanie terminami
- Planowanie zadań

### 4. Śledzenie aktywności
- Historia ukończenia zadań
- Powiadomienia o przypomnieniach
- Oś czasu aktywności
- Śledzenie postępów

### 5. Profil użytkownika
- Zarządzanie kontem
- Ustawienia
- Funkcjonalność wylogowania
- Preferencje użytkownika

---

## Implementacja techniczna

### Nawigacja
Aplikacja wykorzystuje pasek nawigacji dolnej z pięcioma głównymi ekranami:

```kotlin
sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Home : Screen("home", Icons.Default.Home, "Strona główna")
    object Collections : Screen("collections", Icons.Default.List, "Kolekcje")
    object Calendar : Screen("calendar", Icons.Default.DateRange, "Kalendarz")
    object Activity : Screen("activity", Icons.Default.Notifications, "Aktywność")
    object Profile : Screen("profile", Icons.Default.Person, "Profil")
}
```

### Uwierzytelnianie
Do zarządzania użytkownikami wykorzystywane jest Firebase Authentication:

```kotlin
class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Nieznany błąd")
            }
        }
    }
}
```

### Modele danych
Aplikacja wykorzystuje kilka modeli danych dla różnych encji:

```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val collectionId: String,
    val userId: String,
    val dueDate: Date? = null,
    val isCompleted: Boolean = false
)

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val userId: String,
    val isFavorite: Boolean = false
)
```

---

## Ekrany

### Ekran główny
Główny ekran pokazujący wszystkie zadania z ich statusem i terminami:

```kotlin
@Composable
fun HomeScreen(viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val tasks = uiState.tasks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Moje zadania",
            style = MaterialTheme.typography.headlineMedium
        )
        // Implementacja listy zadań...
    }
}
```

### Ekran kolekcji
Organizowanie zadań w kolekcje:

```kotlin
@Composable
fun CollectionsScreen(viewModel: TaskViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val collections = uiState.collections

    Column {
        // Implementacja listy kolekcji...
    }
}
```

### Ekran kalendarza
Przeglądanie i zarządzanie zadaniami według daty:

```kotlin
@Composable
fun CalendarScreen(viewModel: TaskViewModel) {
    // Implementacja kalendarza...
}
```

### Ekran aktywności
Śledzenie ukończenia zadań i przypomnień:

```kotlin
@Composable
fun ActivityScreen(viewModel: TaskViewModel) {
    // Implementacja śledzenia aktywności...
}
```

### Ekran profilu
Zarządzanie kontem użytkownika:

```kotlin
@Composable
fun ProfileScreen(
    viewModel: TaskViewModel,
    authViewModel: AuthViewModel
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { authViewModel.signOut() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wyloguj się")
            }
        }
    }
}
```

---

## Zarządzanie danymi

### Baza danych Room
Aplikacja wykorzystuje Room do lokalnego przechowywania danych:

```kotlin
@Database(
    entities = [Task::class, Collection::class, Reminder::class],
    version = 1
)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun collectionDao(): CollectionDao
    abstract fun reminderDao(): ReminderDao
}
```

### DAO
Obiekty dostępu do danych dla operacji na bazie danych:

```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getTasksByUserId(userId: String): Flow<List<Task>>
    
    @Insert
    suspend fun insertTask(task: Task)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
}
```

---

## Uwierzytelnianie

### Integracja z Firebase
Aplikacja wykorzystuje Firebase Authentication do zarządzania użytkownikami:

```kotlin
class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .await()
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Nieznany błąd")
            }
        }
    }
}
```

### Stany uwierzytelniania
Aplikacja obsługuje różne stany uwierzytelniania:

```kotlin
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
```

---

## Planowane rozszerzenia

1. **Synchronizacja w chmurze**
   - Implementacja Firebase Firestore do synchronizacji w chmurze
   - Obsługa trybu offline
   - Obsługa wielu urządzeń

2. **Zaawansowane funkcje**
   - Udostępnianie zadań
   - Współpraca
   - Załączniki plików
   - Notatki głosowe

3. **Ulepszenia interfejsu**
   - Tryb ciemny
   - Niestandardowe motywy
   - Obsługa widgetów
   - Animacje

4. **Analityka**
   - Statystyki ukończenia zadań
   - Wgląd w produktywność
   - Wzorce użytkowania

5. **Integracja**
   - Integracja z kalendarzem
   - Integracja z pocztą e-mail
   - Obsługa aplikacji trzecich

---

## Podsumowanie
Aplikacja Task Manager zapewnia kompleksowe rozwiązanie do zarządzania zadaniami z nowoczesnym, przyjaznym dla użytkownika interfejsem. Zbudowana zgodnie z najlepszymi praktykami i nowoczesnymi narzędziami do tworzenia aplikacji Android, stanowi solidną podstawę dla przyszłych rozszerzeń i skalowalności.

**Kluczowe wnioski:**
- Czysta architektura z wzorcem MVVM
- Nowoczesny interfejs z Jetpack Compose
- Bezpieczne uwierzytelnianie z Firebase
- Efektywne zarządzanie danymi z Room
- Skalowalna i łatwa w utrzymaniu baza kodu 
