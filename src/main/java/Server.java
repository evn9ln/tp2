import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket server;
    private static OutputStreamWriter writer;
    private static BufferedReader reader;
    private static BufferedReader consoleReader;
    private static File stateFileClient;
    private static File stateFileServer;

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

                        System.out.println("Enter your turn (using spaces AND list of warriors) :");
                        String serverTurn = consoleReader.readLine();
                        int[] serverArr = GameService.parseString(serverTurn);

                        stateFileClient = new File("state.xml");
                        JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfWarriors.class);
                        SchemaOutputResolver sor = new MySchemaOutputResolver();
                        jaxbContext.generateSchema(sor);
                        sor.createOutput("src/main/resources", "schema1.xsd");
                        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        Schema schema = schemaFactory.newSchema(new File("schema1.xsd"));
                        Marshaller marshaller = jaxbContext.createMarshaller();
                        Marshaller marshaller2 = jaxbContext.createMarshaller();
                        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                        marshaller2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                        Unmarshaller unmarshaller2 = jaxbContext.createUnmarshaller();
                        marshaller.setSchema(schema);
                        marshaller2.setSchema(schema);
                        unmarshaller.setSchema(schema);
                        unmarshaller2.setSchema(schema);


                        marshaller.marshal(new ArrayOfWarriors(Arrays.stream(clientsWarriors)
                                .boxed()
                                .collect(Collectors.toList()), "Client Start Warriors"), stateFileClient);
                        stateFileServer = new File("state2.xml");
                        marshaller2.marshal(new ArrayOfWarriors( Arrays.stream(warriors)
                               .boxed()
                               .collect(Collectors.toList()), "Server Start Warriors"), stateFileServer);

                        while (!GameService.isTurnCorrect(new JSONArray(warriors), serverArr)) {
                            serverTurn = consoleReader.readLine();
                            serverArr = GameService.parseString(serverTurn);
                        }
                        String clientTurn = reader.readLine();
                        JSONObject jsonObjectClient = new JSONObject(clientTurn);
                        clientTurn = (String) jsonObjectClient.get("clientTurn");
                        int[] clientArr = GameService.parseString(clientTurn);
                        System.out.println(GameService.getWinner(clientArr, serverArr));
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("winner", GameService.getWinner(clientArr, serverArr));

                        ArrayOfWarriors serverStartArray = (ArrayOfWarriors) unmarshaller.unmarshal(stateFileServer);
                        System.out.println(serverStartArray.toString());

                        ArrayOfWarriors clientStartArray = (ArrayOfWarriors) unmarshaller.unmarshal(stateFileClient);
                        System.out.println(clientStartArray.toString());

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
            } finally {
                System.out.println("Server closed");
                server.close();
                writer.close();
                reader.close();
            }
        } catch (IOException | SAXException | JAXBException e) {
            System.err.println(e);
        }
    }

    public static class MySchemaOutputResolver extends SchemaOutputResolver {

        public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
            File file = new File(suggestedFileName);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toURL().toString());
            return result;
        }

    }
}
