import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    private static Socket clientSocket;
    private static BufferedReader consoleReader;
    private static OutputStreamWriter writer;
    private static BufferedReader reader;

    public static void main(String[] args) {
        try {
            try {
                clientSocket = new Socket("localhost", 8000);
                if (clientSocket.isConnected()) {
                    System.out.println("Welcome to the game Warriors!\n_____________________________________________________________\n");
                }

                consoleReader = new BufferedReader(new InputStreamReader(System.in));
                writer = new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));

                String word = "";
                while (!word.contains("start") && !word.contains("exit")) {
                    System.out.println("Enter 'start' to start game (or exit to end the game) :");
                    word = consoleReader.readLine();
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", word);
                writer.write(jsonObject.toString() + "\n");
                writer.flush();

                if (word.equals("exit")) {
                    System.out.println("Game over!");
                } else {
                    String line = reader.readLine();
                    JSONArray jsonArray = new JSONArray(line);
                    System.out.println("Your warriors : " + jsonArray.toString());

                    System.out.println("Enter your turn (using spaces AND list of warriors) : ");
                    String clientTurn = consoleReader.readLine();
                    int[] clientArr = GameService.parseString(clientTurn);

                    while(!GameService.isTurnCorrect(jsonArray, clientArr)) {
                        clientTurn=consoleReader.readLine();
                        clientArr = GameService.parseString(clientTurn);
                    }
                    jsonObject.clear();
                    jsonObject.put("clientTurn",clientTurn);
                    writer.write(jsonObject.toString() + "\n");
                    writer.flush();
                    jsonObject = new JSONObject(reader.readLine());
                    System.out.println(jsonObject.get("winner"));

                    File stateFileClient = new File("state.xml");
                    File stateFileServer = new File("state2.xml");
                    JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfWarriors.class);
                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Schema schema = schemaFactory.newSchema(new File("schema1.xsd"));
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    Unmarshaller unmarshaller2 = jaxbContext.createUnmarshaller();
                    unmarshaller.setSchema(schema);
                    unmarshaller2.setSchema(schema);

                    ArrayOfWarriors clientStartArray = (ArrayOfWarriors) unmarshaller.unmarshal(stateFileClient);
                    System.out.println(clientStartArray.toString());
                    ArrayOfWarriors serverStartArray = (ArrayOfWarriors) unmarshaller.unmarshal(stateFileServer);
                    System.out.println(serverStartArray.toString());
                }

            } finally {
                System.out.println("Client closed.");
                if (clientSocket != null) {
                    clientSocket.close();
                    consoleReader.close();
                    reader.close();
                    writer.close();
                }
            }
        } catch (IOException | JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }
}


