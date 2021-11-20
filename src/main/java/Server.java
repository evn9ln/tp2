import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;

import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket server;
    private static OutputStreamWriter writer;
    private static BufferedReader reader;
    private static BufferedReader consoleReader;

    public static void main(String[] args) {
        try {
            try {
                server = new ServerSocket(8000);
                System.out.println("Player 1 connected.");
                clientSocket = server.accept();

                if (clientSocket.isConnected()) {
                    System.out.println("Player 2 connected.");
                }
                try {
                    writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                    consoleReader = new BufferedReader(new InputStreamReader(System.in));

                    String message = "";
                    message = reader.readLine();
                    if (message.contains("start")) {
                        System.out.println("Message from player 2: start");
                        System.out.println("\n_______________________________________________________________________" +
                                "\nThe GAME has started!");
                        System.out.print("Your warriors : [");
                        int[] warriors = GameService.getWarriors();
                        for (int i = 0; i < warriors.length; i++) {
                            if (i == warriors.length - 1) {
                                System.out.print(warriors[i] + "]\n");
                                continue;
                            }
                            System.out.print(warriors[i] + ",");
                        }

                        int[] clientsWarriors = GameService.getWarriors();
                        JSONArray jsonArray = new JSONArray(clientsWarriors);
                        writer.write(jsonArray.toString() + "\n");
                        writer.flush();

                        String serverTurn;
                        int[] serverArr;
                        while (true) {
                            System.out.println("Enter your turn (using spaces AND list of warriors, " +
                                    "for example:1 2 3 4 5) : ");
                            serverTurn = consoleReader.readLine();
                            try {
                                serverArr = GameService.parseString(serverTurn);
                                if (GameService.isTurnCorrect(new JSONArray(warriors), serverArr)) {
                                    break;
                                } else {
                                    System.out.println("Incorrect turn! Try again...");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Incorrect input (use only numbers and 'space' on your keyboard!");
                            }
                        }
                        System.out.println("Waiting for Player 2 turn...");
                        String clientTurn = reader.readLine();
                        JSONObject jsonObjectClient = new JSONObject(clientTurn);
                        clientTurn = (String) jsonObjectClient.get("clientTurn");
                        int[] clientArr = GameService.parseString(clientTurn);
                        System.out.println(GameService.getWinner(clientArr, serverArr));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("winner", GameService.getWinner(clientArr, serverArr));
                        writer.write(jsonObject.toString() + "\n");
                        writer.flush();
                    } else if (message.contains("exit")) {
                        System.out.println("Message from player 2: exit");
                        System.out.println("Player 1 win!");
                    }
                } finally {
                    clientSocket.close();
                    System.out.println("Client disconnected");
                }
            } catch (SocketException socketException) {
                System.out.println("Something went wrong with client connection. Restart game");
            } finally {
                System.out.println("Server closed");
                server.close();
                writer.close();
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Something went wrong. Restart game");
        }
    }
}
