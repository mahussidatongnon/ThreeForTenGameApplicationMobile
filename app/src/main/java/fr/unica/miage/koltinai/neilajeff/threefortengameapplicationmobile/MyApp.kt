package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.GameScreen
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.PlayGameScreen
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.RulesScreen
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.WelcomeScreen
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.MyAppScaffoldViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.MyAppViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.PlayGameViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.Purple

// Les routes définies comme strings simples
const val WELCOME_ROUTE = "welcome"
const val GAMES_ROUTE = "games"
const val RULES_ROUTE = "rules" // Nouvelle route pour les règles du jeu

@kotlinx.serialization.Serializable
data class PlayGameRoute(val gameId: String, val autoStart : Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(myAppViewModel: MyAppViewModel = MyAppViewModel()) {
    val navController = rememberNavController()
    val username = myAppViewModel.username
    val startDestination = if (username.value == null) WELCOME_ROUTE else GAMES_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(WELCOME_ROUTE) {
            WelcomeScreen { username ->
                // Au lieu d'utiliser LaunchedEffect ici
                myAppViewModel.loadPlayer(username)
                // Naviguer immédiatement
                navController.navigate(route = GAMES_ROUTE) {
                    popUpTo(WELCOME_ROUTE) { inclusive = true }
                }
            }
        }
        composable(GAMES_ROUTE) {
            MyAppScaffold(
                navController = navController,
                title = "3 pour 10 - Accueil"
            ) { paddingValues ->
                GameScreen(
                    navController = navController,
                    paddingValues = paddingValues,
                    onLogout = {
                        // Rediriger vers l'écran de connexion
                        navController.navigate(WELCOME_ROUTE) {
                            // Effacer toute la pile de navigation
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
        composable<PlayGameRoute> { backStackEntry ->
            val playGameRoute = backStackEntry.toRoute<PlayGameRoute>()

            MyAppScaffold(
                navController = navController,
                title = "3 pour 10 - Game (${playGameRoute.gameId})"
            ) { paddingValues ->
                PlayGameScreen(
                    playGameViewModel = PlayGameViewModel(playGameRoute.gameId, autoStart = playGameRoute.autoStart),
                    navController = navController
                )
            }
        }
        // Nouvel écran pour les règles du jeu
        composable(RULES_ROUTE) {
            RulesScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppScaffold(
    myAppScaffoldViewModel: MyAppScaffoldViewModel = MyAppScaffoldViewModel(),
    navController: NavController,
    title: String = "3 pour 10",
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple
                ),
                actions = {
                    // Bouton pour accéder aux règles du jeu
                    IconButton(
                        onClick = { navController.navigate(RULES_ROUTE) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info, // Utilisation de l'icône Info à la place de Help
                            contentDescription = "Règles du jeu",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            // N'afficher la barre de navigation que sur l'écran principal
            if (currentRoute != WELCOME_ROUTE && currentRoute != RULES_ROUTE) {
                NavigationBar(
                    containerColor = Purple.copy(alpha = 0.1f)
                ) {
                    NavigationBarItem(
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Accueil") },
                        label = { Text("Accueil") },
                        selected = currentRoute == GAMES_ROUTE,
                        onClick = {
                            if (currentRoute != GAMES_ROUTE) {
                                navController.navigate(GAMES_ROUTE) {
                                    // Popper au GAMES_ROUTE si on y est déjà
                                    popUpTo(GAMES_ROUTE) { inclusive = true }
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Purple,
                            selectedTextColor = Purple,
                            indicatorColor = Purple.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        },
        content = content
    )
}