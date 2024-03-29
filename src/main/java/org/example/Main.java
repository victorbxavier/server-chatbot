package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;
public class Main {
    private static final boolean TRACE_MODE = false;
    static String botName = "super";
    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(8080);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            Socket socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            // Sends output to client
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());

            String resourcesPath = getResourcesPath();
            System.out.println(resourcesPath);
            MagicBooleans.trace_mode = TRACE_MODE;
            Bot bot = new Bot(botName, resourcesPath);
            Chat chatSession = new Chat(bot);
            bot.brain.nodeStats();
            String textLine = "";
            while (true) {
                System.out.print("Human : ");
                textLine = in.readUTF();
                System.out.println(textLine);
                if ((textLine == null) || (textLine.length() < 1))
                    textLine = MagicStrings.null_input;
                if (textLine.equals("q")) {
                    System.exit(0);
                } else if (textLine.equals("wq")) {
                    bot.writeQuit();
                    System.exit(0);
                } else {
                    String request = textLine;
                    if (MagicBooleans.trace_mode)
                        System.out.println(
                                "STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0)
                                        + ":TOPIC=" + chatSession.predicates.get("topic"));
                    String response = chatSession.multisentenceRespond(request);
                    while (response.contains("&lt;"))
                        response = response.replace("&lt;", "<");
                    while (response.contains("&gt;"))
                        response = response.replace("&gt;", ">");
                    System.out.println("Robot : " + response);

                    out.writeUTF("Robot: "+response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        System.out.println(path);
        String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        return resourcesPath;
    }
}