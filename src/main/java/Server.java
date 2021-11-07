import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket server;
    private static OutputStreamWriter writer;
    private static BufferedReader reader;

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
                    System.out.print("Your warriors : [");
                    int[] warriors = GameService.getWarriors();
                    for (int i = 0; i < warriors.length; i++) {
                        if (i == warriors.length - 1) {
                            System.out.print(warriors[i] + "]\n");
                            continue;
                        }
                        System.out.print(warriors[i] + ",");
                    }

                    writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
                    reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

                    String message = "";
                    message = reader.readLine();
                        if (message.contains("start")) {
                            int[] clientsWarriors = GameService.getWarriors();
                            JSONArray jsonArray = new JSONArray(clientsWarriors);
                            writer.write(jsonArray.toString() + "\n");
                            writer.flush();
                        }

                        System.out.println("Message from player 2: " + message);

                        if (message.contains("exit")) {
                            System.out.println("Player 1 win!");
                        }

                } finally {
                    clientSocket.close();
                    System.out.println("Client disconnected");
                }
            } finally {
                System.out.println("Server closed");
                server.close();
                writer.close();
                reader.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
