package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.PointDTO
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Cell
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.WinningDirection
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.ThreeForTenGameApplicationMobileTheme
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.Yellow


typealias Board = List<List<Cell?>>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCell(cell: Cell?, onClick: () -> Unit = {}, onValueChange: (Int) -> Unit = {},
             isGameBoardInteractive: Boolean = true,
             isLastMove: Boolean = false
     ) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf(cell?.value?.toString() ?: "1") } // Valeur par défaut à 3
    var errorMessage by remember { mutableStateOf("") } // Message d'erreur
    var isValid by remember { mutableStateOf(true) } // Valider si la saisie est correcte
// Définir une couleur différente si c'est le dernier mouvement
    val cellBackgroundColor = if (isLastMove) Yellow else Color.White
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(cellBackgroundColor) // Appliquer la couleur de fond selon le dernier mouvement
            .border(1.dp, Color.Black)
            .clickable(
                enabled = isGameBoardInteractive, // Rendre le clic non interactif si nécessaire
                onClick = {
                    // Ne rien faire si la cellule est déjà remplie
                    if (cell?.value == null) {
                        onClick()
                        isDialogVisible = true // Afficher le popup
                    }
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Afficher la valeur de la cellule si ce n'est pas en mode édition
        Text(
            text = cell?.value?.toString() ?: "",
            fontSize = 25.sp
        )

        // Afficher les directions gagnées
        cell?.wonCasesDirections?.forEach { direction ->
            when (direction) {
                WinningDirection.UP_DIAGONAL -> {
                    DirectionIcon(
                        alignment = Alignment.TopEnd,
                        rotation = -45f
                    )
                }
                WinningDirection.DOWN_DIAGONAL -> {
                    DirectionIcon(
                        alignment = Alignment.BottomEnd,
                        rotation = 45f
                    )
                }
                WinningDirection.HORIZONTAL -> {
                    DirectionIcon(
                        alignment = Alignment.CenterEnd,
                        rotation = 0f,
                        icon =  Icons.AutoMirrored.Filled.ArrowForward
                    )
                }
                WinningDirection.VERTICAL -> {
                    DirectionIcon(
                        alignment = Alignment.BottomCenter,
                        rotation = 90f,
                        icon =  Icons.AutoMirrored.Filled.ArrowForward
                    )
                }
            }
        }
    }

    // Afficher le popup lorsqu'on clique sur une cellule vide
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                isDialogVisible = false // Fermer le dialog quand on clique à l'extérieur
            },
            title = { Text("Enter a value") },
            text = {
                Column {
                    // Saisie de la valeur avec validation
                    TextField(
                        value = inputValue,
                        onValueChange = { value ->
                            inputValue = value
                            // Validation de la saisie
                            if (value.toIntOrNull() in 1..8) {
                                isValid = true
                                errorMessage = ""
                            } else if (value.isNotEmpty()) {
                                isValid = false
                                errorMessage = "Value must be between 1 and 8"
                            }
                        },
                        label = { Text("Enter a value between 1 and 8") },
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Number
//                        ),
                        isError = !isValid, // Afficher l'erreur si la saisie est invalide
                    )

                    // Affichage du message d'erreur
                    if (!isValid) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Si la saisie est valide, mettre à jour la valeur
                        if (isValid && inputValue.isNotEmpty()) {
                            onValueChange(inputValue.toInt())
                            isDialogVisible = false // Fermer le popup
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isDialogVisible = false // Fermer le popup sans rien changer
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DirectionIcon(
    alignment: Alignment,
    rotation: Float = 0f,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward, // Icône par défaut : flèche
    tint: Color = Color.Red
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(12.dp)
                .rotate(rotation),
            tint = tint
        )
    }
}


@Composable
fun GameBoard(
    board: Board,
    onCellClick: (row: Int, col: Int) -> Unit,
    onValueChange: (row: Int, col: Int, value: Int) -> Unit,
    lastMovePointDTO: PointDTO? = null,
    isGameBoardInteractive: Boolean = true
) {
    Column {
        for (row in board.indices) {
            Row {
                for (col in board[row].indices) {
                    val cell = board[row][col]

                    // Vérification si cette cellule est celle du dernier mouvement
                    val isLastMove = lastMovePointDTO != null && lastMovePointDTO.x == row && lastMovePointDTO.y == col

                    GameCell(
                        cell = cell,
                        onClick = {
                            if (isGameBoardInteractive) {
                                // Appel à la fonction de clic uniquement si le plateau est interactif
                                onCellClick(row, col)
                            }
                        },
                        onValueChange = { value ->
                            // Mise à jour de la valeur de la cellule
                            if (isGameBoardInteractive) {
                                onValueChange(row, col, value)
                            }
                        },
                        isGameBoardInteractive = isGameBoardInteractive, // Passer l'état interactif
                        isLastMove = isLastMove // Passer si c'est la dernière cellule jouée
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewCell_NoDirections() {
    GameCell(Cell(value = 3))
}

@Preview(showBackground = true)
@Composable
fun PreviewCell_AllDirections() {
    GameCell(
        Cell(
            value = 4,
            wonCasesDirections = setOf(
                WinningDirection.HORIZONTAL,
                WinningDirection.VERTICAL,
                WinningDirection.DOWN_DIAGONAL,
                WinningDirection.UP_DIAGONAL
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCell_TwoDirections() {
    GameCell(
        Cell(
            value = 2,
            wonCasesDirections = setOf(WinningDirection.HORIZONTAL, WinningDirection.DOWN_DIAGONAL)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGameBoard() {
    val sampleBoard: Board = listOf(
        listOf(
            Cell(1), Cell(2), Cell(3), null
        ),
        listOf(
            null, Cell(4, setOf(WinningDirection.HORIZONTAL)), null, Cell(5)
        ),
        listOf(
            Cell(2, setOf(WinningDirection.DOWN_DIAGONAL, WinningDirection.VERTICAL)), null, Cell(3), null
        ),
        listOf(
            null, null, null, Cell(1, setOf(WinningDirection.UP_DIAGONAL))
        )
    )

    ThreeForTenGameApplicationMobileTheme {
        GameBoard(board = sampleBoard,
            onCellClick = { row, col ->
                // Simuler un clic
                println("Clicked cell at [$row, $col]")
            },
            onValueChange = { row, col, value ->
                // Mise à jour de la valeur de la cellule
                println("Setting value $value for cell [$row, $col]")
                // Tu peux aussi appeler ton ViewModel pour mettre à jour l'état du jeu ici
            },
            lastMovePointDTO = PointDTO(1, 1),
            isGameBoardInteractive = false
        )
    }
}