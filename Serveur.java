//Ming-Xia Delvas 2019/04/11 Exercice 7 TicTacToe

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

    private int nbClients = 0;
    private boolean gameStarted = false;
    private final Object nbClientsLock = new Object();
    private final Socket[] clients = new Socket[2];
    private final BufferedWriter[] writers = new BufferedWriter[2];
    private TicTacToe ttt = new TicTacToe();

    public static void main(String[] args) {
        Serveur s = new Serveur();
        s.start();
    }

    public void closeAll() {
        for (Socket client : clients) {
            if (!client.isClosed()) {
                try {
                    client.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void broadcast(String msg) {
        if (!gameStarted) {
            return;
        }

        for (int i = 0; i < clients.length; i++) {
            if (!clients[i].isClosed()) {
                try {
                    writers[i].write(msg + "\n");
                    writers[i].flush();
                } catch (IOException ex) {
                    closeAll();
                }
            }
        }
    }

    public synchronized void process(int idx, String msg) {
        if (!gameStarted) {
            return;
        }

        try {
            String[] move = msg.split(":");

            char x = (char) (move[0].charAt(0) - '1');
            char y = (char) (move[1].charAt(0) - '1');

            if (ttt.getTurn() == 'x' && idx == 0 || ttt.getTurn() == 'o' && idx == 1) {
                broadcast("move " + ttt.getTurn() + " " + (x + 1) + ":" + (y + 1));

                ttt.play(x, y);

                if (ttt.getGameStatus() != TicTacToe.GameStatus.keepPlaying) {
                    broadcast(ttt.getGameStatus().name());
                    gameStarted = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(idx + ": " + msg);
        System.out.print(ttt);
    }

    public void send(int idx, String msg) {
        if (!gameStarted) {
            return;
        }

        try {
            writers[idx].write(msg);
            writers[idx].flush();
        } catch (IOException ex) {
            closeAll();
        }
    }

    public void start() {
        try {
            ServerSocket server = new ServerSocket(1337, 1);

            Runnable run = () -> {
                try {
                    Socket client;
                    int clientNumber;

                    synchronized (nbClientsLock) {
                        client = server.accept();
                        clientNumber = nbClients++;

                        clients[clientNumber] = client;
                    }

                    System.out.println("Connexion !");
                    System.out.println(client);

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(client.getInputStream()));
                    writers[clientNumber] = new BufferedWriter(
                            new OutputStreamWriter(client.getOutputStream()));

                    if (nbClients == 2) {
                        synchronized (Serveur.this) {
                            Serveur.this.notify();
                        }
                    }

                    String line;

                    while ((line = reader.readLine()) != null) {
                        process(clientNumber, line);
                    }

                    broadcast("end");
                    closeAll();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            };

            Thread t1 = new Thread(run);
            Thread t2 = new Thread(run);

            t1.start();
            t2.start();

            synchronized (this) {
                this.wait();
            }

            System.out.println("All clients joined!");
            gameStarted = true;

            send(0, "start x\n");
            send(1, "start o\n");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            System.out.println("Interruption");
        }
    }
}
