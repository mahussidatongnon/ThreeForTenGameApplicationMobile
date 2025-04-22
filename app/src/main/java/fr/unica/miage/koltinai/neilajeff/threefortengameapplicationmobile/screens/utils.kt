package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.ThreeForTenGameApplicationMobileTheme


enum class Direction { HORIZONTAL, VERTICAL, DIAGONAL_DOWN, DIAGONAL_UP }

data class Cell(
    val value: Int?, // chiffre placé ou null
    val usedDirections: Set<Direction> = emptySet()
)

typealias Board = List<List<Cell>>

@Composable
fun GameCell(cell: Cell, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color.White)
            .border(1.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Affiche le chiffre au centre
        Text(
            text = cell.value?.toString() ?: "",
            fontSize = 20.sp
        )

        // Indicateurs de direction déjà utilisés
        cell.usedDirections.forEach {
            when (it) {
                Direction.HORIZONTAL -> ArrowIcon(Alignment.TopCenter, 0f)         // ➡️
                Direction.VERTICAL -> ArrowIcon(Alignment.CenterStart, 90f)       // ⬇️
                Direction.DIAGONAL_DOWN -> ArrowIcon(Alignment.TopStart, 45f)     // ↘️
                Direction.DIAGONAL_UP -> ArrowIcon(Alignment.BottomStart, -45f)   // ↗️
            }
        }
    }
}

@Composable
fun ArrowIcon(alignment: Alignment, rotation: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(12.dp).rotate(rotation),
            tint = Color.Red
        )
    }
}

@Composable
fun GameBoard(board: Board, onCellClick: (row: Int, col: Int) -> Unit) {
    Column {
        for (row in board.indices) {
            Row {
                for (col in board[row].indices) {
                    GameCell(cell = board[row][col]) {
                        onCellClick(row, col)
                    }
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
            usedDirections = setOf(
                Direction.HORIZONTAL,
                Direction.VERTICAL,
                Direction.DIAGONAL_DOWN,
                Direction.DIAGONAL_UP
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
            usedDirections = setOf(Direction.HORIZONTAL, Direction.DIAGONAL_DOWN)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewGameBoard() {
    val sampleBoard: Board = listOf(
        listOf(
            Cell(1), Cell(2), Cell(3), Cell(null)
        ),
        listOf(
            Cell(null), Cell(4, setOf(Direction.HORIZONTAL)), Cell(null), Cell(5)
        ),
        listOf(
            Cell(2, setOf(Direction.DIAGONAL_DOWN, Direction.VERTICAL)), Cell(null), Cell(3), Cell(null)
        ),
        listOf(
            Cell(null), Cell(null), Cell(null), Cell(1, setOf(Direction.DIAGONAL_UP))
        )
    )

    ThreeForTenGameApplicationMobileTheme {
        GameBoard(board = sampleBoard) { row, col ->
            // Simuler un clic
            println("Clicked cell at [$row, $col]")
        }
    }
}