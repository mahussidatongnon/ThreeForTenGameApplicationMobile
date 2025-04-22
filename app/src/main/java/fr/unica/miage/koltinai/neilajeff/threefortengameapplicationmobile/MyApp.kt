package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.WelcomeScreen
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.MyAppScaffoldViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.MyAppViewModel

@kotlinx.serialization.Serializable
object GamesRoute

@kotlinx.serialization.Serializable
data class GameRoute(val idGame : String)

@kotlinx.serialization.Serializable
data class PlayGameRoute(val idGame : String)

@kotlinx.serialization.Serializable
object WelcomeRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(myAppViewModel: MyAppViewModel = MyAppViewModel()) {
    val navController = rememberNavController()
    val username = myAppViewModel.username
    val startDestination: Any = if (username.value == null)
                            WelcomeRoute
                        else
                            GamesRoute

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<WelcomeRoute> {
            WelcomeScreen { username ->
                myAppViewModel.loadPlayer(username)
                navController.navigate(route = GamesRoute)
            }
        }
        composable<GamesRoute> {
            MyAppScaffold(navController=navController)  { paddingValues ->
                GamesRoute
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppScaffold(myAppScaffoldViewModel: MyAppScaffoldViewModel = MyAppScaffoldViewModel(), navController: NavController, content: @Composable (PaddingValues) -> Unit) {
    val currentRoute = navController.currentBackStackEntry?.destination
//    val username = myAppScaffoldViewModel.username
    println("currentRoute: $currentRoute")
    Scaffold(
        topBar = { TopAppBar(title = { Text("Games App") }) },
        bottomBar = {
            BottomAppBar {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    NavigationBarItem(
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Accueil") },
                        label = { Text("Accueil") },
                        selected = currentRoute == GamesRoute,
                        onClick = { navController.navigate(GamesRoute) }
                    )
                }
            }
        },
        content = content
    )
}