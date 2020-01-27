//Ming-Xia Delvas 2019/04/11 Exercice 7 TicTacToe

import java.util.Scanner;

/**
 *
 */
public class TicTacToe {

    public enum GameStatus {
        keepPlaying, xWins, oWins, tie
    };

    private int nb = 0;
    private boolean xTurn = true;
    private char[][] grid = new char[3][3];

    public boolean play(int x, int y) {
        if (grid[y][x] != 0) {
            return false;
        }

        grid[y][x] = (char) (xTurn ? 1 : 2);
        xTurn = !xTurn;

        nb++;

        return true;
    }

    public GameStatus getGameStatus() {

        char val;

        // Vertical
        for (int col = 0; col < 3; col++) {
            if (grid[col][0] > 0 && grid[col][0] == grid[col][1] && grid[col][0] == grid[col][2]) {
                return grid[col][0] == 1 ? GameStatus.xWins : GameStatus.oWins;
            }
        }

        // Horizontal
        for (int row = 0; row < 3; row++) {
            if (grid[0][row] > 0 && grid[0][row] == grid[1][row] && grid[0][row] == grid[2][row]) {
                return grid[0][row] == 1 ? GameStatus.xWins : GameStatus.oWins;
            }
        }

        // Diagonal
        if (grid[0][0] > 0 && grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
            return grid[0][0] == 1 ? GameStatus.xWins : GameStatus.oWins;
        }

        if (grid[0][2] > 0 && grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0]) {
            return grid[0][2] == 1 ? GameStatus.xWins : GameStatus.oWins;
        }

        return nb == 9 ? GameStatus.tie : GameStatus.keepPlaying;
    }

    public char getTurn() {
        return xTurn ? 'x' : 'o';
    }

    @Override
    public String toString() {
        String s = "-------\n";

        for (char[] line : grid) {
            s += "|";
            for (char c : line) {
                switch (c) {
                    case 0:
                        s += ' ';
                        break;
                    case 1:
                        s += 'x';
                        break;
                    default:
                        s += 'o';
                }
                s += '|';
            }
            s += "\n-------\n";
        }

        return s;
    }

    public static void main(String[] args) {
        TicTacToe ttt = new TicTacToe();

        Scanner s = new Scanner(System.in);

        while (ttt.getGameStatus() == GameStatus.keepPlaying) {
            System.out.print(ttt);
            System.out.print(ttt.getTurn() + "> ");
            String move = s.nextLine();
            char x = (char) (move.charAt(0) - '1');
            char y = (char) (move.charAt(1) - '1');

            ttt.play(x, y);
        }

        System.out.println(ttt);
        System.out.println(ttt.getGameStatus().name());
    }
}
