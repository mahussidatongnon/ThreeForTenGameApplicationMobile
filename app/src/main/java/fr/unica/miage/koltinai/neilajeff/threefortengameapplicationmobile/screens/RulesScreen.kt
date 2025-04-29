package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.Purple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Règles du jeu",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Titre
                Text(
                    text = "3 pour 10",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Introduction
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(
                        text = "Ce jeu est difficile. Il est en fait très facile d'y jouer et de gagner des points, mais il est complexe de compter les points et de se souvenir de toutes les combinaisons qui ont déjà marqué un point, ce qui est nécessaire.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }

                // Règles du jeu
                Text(
                    text = "Règles du jeu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )

                RuleItem("Le plateau de jeu est constitué d'un damier carré de K cases (dans notre application K = 5, 6 ou 7) de côté.")

                RuleItem("Il y a deux joueurs. Le premier choisit un chiffre c (1 ≤ c ≤ 8) et le place dans une des cases vides. Le second fait de même et ainsi de suite.")

                RuleItem("Le but est pour chaque joueur, à la fin du jeu, quand le plateau est plein, d'avoir fait le maximum de lignes de trois cases consécutives dont le total fait 10.")

                RuleItem("À chaque fois qu'un joueur joue, il faut déterminer le nombre de groupes de trois cases consécutives, comprenant celle qui vient d'être jouée, dont le total fait 10 et compter un point pour ce joueur par groupe ainsi détecté.")

                RuleItem("Il est possible de placer un chiffre qui permet de gagner plus d'un point (s'il permet, en même temps, de faire plusieurs groupes de trois cases consécutives totalisant 10).")

                RuleItem("Le jeu se joue entre deux joueurs à tour de rôle.")

                Text(
                    text = buildAnnotatedString {
                        append("Attention : ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Red)) {
                            append("au cours d'une partie, chaque case peut être utilisée quatre fois au plus pour faire un point : horizontalement, verticalement, en diagonale montante et en diagonale descendante. Elle ne peut pas être utilisée deux fois dans la même direction !")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                // Types d'IA
                Text(
                    text = "Types d'intelligence artificielle",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )

                Text(
                    text = "Dans notre application, vous pouvez jouer contre trois types d'IA :",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 8.dp),
                    fontSize = 16.sp
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "IA Aléatoire",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Purple
                        )
                        Text(
                            text = "Cette IA joue des coups aléatoires sans stratégie particulière.",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "IA Passive",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Purple
                        )
                        Text(
                            text = "Cette IA joue de manière défensive, en essayant d'éviter de donner des points à l'adversaire.",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "IA Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Purple
                        )
                        Text(
                            text = "Cette IA joue de manière agressive, en essayant de maximiser ses propres points même si elle risque d'en donner à l'adversaire.",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Astuce
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Astuce",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Pour gagner, essayez de placer des chiffres qui créent plusieurs lignes de somme 10 en même temps. Surveillez également les cases qui pourraient donner des points à votre adversaire.",
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RuleItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = "• ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Purple
        )
        Text(
            text = text,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}