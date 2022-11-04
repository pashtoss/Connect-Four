package connectfour

const val MIN_SIZE = 5
const val MAX_SIZE = 9

fun isStringDigit(str: String): = if (str.toIntOrNull() != null) true else false

fun correctNumberOfGames(): Int {
    while (true) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter\nInput a number of games:")
        val temp = readln()
        when {
            temp.isEmpty() -> return 1
            isStringDigit(temp) && temp.toInt() > 0 -> return temp.toInt()
            else -> {
                println("Invalid input")
                continue
            }
        }
    }
}

fun correctDimension(): MutableList<Int> {
    while (true) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        var dimension = readln().replace(" ", "").uppercase()
        dimension = dimension.replace("\t", "")
        if (dimension.isEmpty()) return mutableListOf(6, 7)
        if ("X" !in dimension || dimension.length < 3) {
            println("Invalid input")
            continue
        }
        val (sRow: String, sColumn: String) = dimension.split("X")
        var flag = true
        for (ch in sRow + sColumn) {
            if (!ch.isDigit()) {
                print("Invalid input")
                flag = false
                break
            }
        }
        if (!flag) continue
        val rowCount = sRow.toInt()
        val columnCount = sColumn.toInt()

        if (rowCount !in MIN_SIZE..MAX_SIZE) {
            println("Board rows should be from 5 to 9")
            continue
        }
        if (columnCount !in MIN_SIZE..MAX_SIZE) {
            println("Board columns should be from 5 to 9")
            continue
        }
        return mutableListOf(rowCount, columnCount)
    }
}

fun printBoard(field: MutableList<MutableList<Char>>) {
    val rows = field.size
    val columns = field[0].size
    for (column in 1..columns) {
        print(" $column")
    }
    for (row in 0 until rows) {
        println()
        for (column in 0 until columns) {
            print("║${field[row][column]}")
        }
        print("║")
    }
    println("\n╚${"═╩".repeat(columns - 1)}═╝")
}

fun fillBoard(field: MutableList<MutableList<Char>>, whoTurns: Boolean, column: Int): Boolean {
    val sign = if (whoTurns) 'o' else '*'
    val rows = field.size
    val columns = field[0].size
    for (row in rows - 1 downTo 0) {
        if (field[row][column - 1] == ' ') {
            field[row][column - 1] = sign
            return true
        }
    }
    println("Column $column is full")
    return false
}

fun checkField(field: MutableList<MutableList<Char>>, whoTurns: Boolean): Int {
    val rows = field.size
    val columns = field[0].size
    val pattern = when (whoTurns) {
        true -> "oooo"
        false -> "****"
    }

    fun isFieldFull(topOfField: MutableList<Char>): Boolean {
        for (column in topOfField) if (column == ' ') return false
        return true
    }

    fun horizontalWin(): Boolean {
        for (row in field.reversed()) {
            if (pattern in row.joinToString("")) return true
        }
        return false
    }

    fun verticalWin(): Boolean {
        for (col in 0 until columns) {
            var columnRow = ""
            for (row in field) {
                columnRow += row[col]
            }
            if (pattern in columnRow) return true
        }
        return false
    }

    fun diagonalWin(): Boolean {
        //normal
        for (row in 0..rows - 4) {
            for (column in 0..columns - 4) {
                var diagonalString = ""
                var innerRow = row
                var innerColumn = column
                while (innerRow < rows && innerColumn < columns) {
                    diagonalString += field[innerRow++][innerColumn++]
                }
                if (pattern in diagonalString) return true
            }
        }
        //reversed
        for (row in 0..rows - 4) {
            for (column in columns - 1 downTo 3) {
                var diagonalString = ""
                var innerRow = row
                var innerColumn = column
                while (innerRow < rows && innerColumn >= 0) {
                    diagonalString += field[innerRow++][innerColumn--]
                }
                if (pattern in diagonalString) return true
            }
        }
        return false
    }

    return when {
        isFieldFull(field.first()) -> -1
        horizontalWin() || verticalWin() || diagonalWin() -> 1
        else -> 0
    }
}

fun singleGame(
    firstPlayer: String,
    secondPlayer: String,
    whoIsFirst: Boolean,
    rows: Int,
    columns: Int
): Int {
    val field = MutableList(rows) { MutableList(columns) { ' ' } }
    var whoTurns = whoIsFirst
    printBoard(field)
    while (true) {
        if (whoTurns) println("$firstPlayer's turn:") else println("$secondPlayer's turn:")
        val inputChoice = readln()
        if (inputChoice == "end") {
            return -1
        }
        var choice = 0
        if (!isStringDigit(inputChoice)) {
            println("Incorrect column number")
            continue
        }
        choice = inputChoice.toInt()
        if (choice !in 1..columns) {
            println("The column number is out of range (1 - $columns)")
            continue
        }
        if (fillBoard(field, whoTurns, choice))
            printBoard(field)
        when (checkField(field, whoTurns)) {
            0 -> {
                whoTurns = whoTurns.not()
                continue
            }

            -1 -> {
                println("It is a draw")
                return 0
            }

            1 -> {
                println("Player ${if (whoTurns) firstPlayer else secondPlayer} won")
                return if (whoTurns) 1 else 2
            }
        }
    }
}

fun multipleGames(
    firstPlayer: String,
    secondPlayer: String,
    numberOfGames: Int,
    rows: Int,
    columns: Int
) {
    var score1 = 0
    var score2 = 0
    var counter = 1
    var whoTurns = true
    while (counter <= numberOfGames) {
        println("Game #${counter++}")
        when (singleGame(firstPlayer, secondPlayer, whoTurns, rows, columns)) {
            -1 -> return
            0 -> {
                score1 += 1
                score2 += 1
            }
            1 -> score1 += 2
            2 -> score2 += 2
        }
        whoTurns = whoTurns.not()
        println("Score\n$firstPlayer: $score1 $secondPlayer: $score2")
    }
}

fun main() {
    println("Connect Four")
    println("First player's name:")
    val firstPlayer = readln()
    println("Second player's name:")
    val secondPlayer = readln()
    val (rows, columns) = correctDimension()
    val numberOfGames = correctNumberOfGames()
    println("$firstPlayer VS $secondPlayer\n$rows X $columns board")
    if (numberOfGames == 1) {
        println("Single game")
        singleGame(firstPlayer, secondPlayer, true, rows, columns)
    } else {
        println("Total $numberOfGames games")
        multipleGames(firstPlayer, secondPlayer, numberOfGames, rows, columns)
    }
    print("Game over!")
}
