package edu.seg2105.edu.server.backend;

import edu.seg2105.client.common.ChatIF;
import java.util.Scanner;
import java.io.*;

public class ServerConsole implements ChatIF {

    private EchoServer server;
    private Scanner fromConsole;

    /**
     * constructor from server
     */

    public ServerConsole(EchoServer server){
        this.server = server;
        fromConsole = new Scanner(System.in);
    }

    public void accept() {
        while(true) {
            String message = fromConsole.nextLine();

            if (message.startsWith("#")) {
                try {
                    handleCommand(message);
                } catch (IOException e) {
                    display("Error handling command!");
                }
            } else {
                display("SERVER MSG>" + message);
                server.sendToAllClients("SERVER MSG>" + message);
            }
        }
    }

    private void handleCommand(String command) throws IOException {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];

        switch(cmd.toLowerCase()){
            case "#quit":
                server.close();
                System.out.println("Server quitting!");
                System.exit(0); //ends process
                break;

            case "#stop":
                server.stopListening();
                display("Server stopped: no longer listening for new clients");
                break;

            case "#close":
                server.close();
                display("Server closing: no longer listening, all clients disconnected");
                break;

            case "#setport":
                if (server.isListening()) {
                    display("Cannot setport while server is running!");
                } else if (parts.length > 1) {
                    try {
                        int port = Integer.parseInt(parts[1].trim());
                        server.setPort(port);
                        display("set port to: " + port);
                    } catch (NumberFormatException e) {
                        display("Invalid port number!");
                    }
                } else {
                    display("Usage: #setport <port>");
                }
                break;

            case "#start":
                if (server.isListening()) {
                    display("Server already running");
                } else {
                    try {
                        server.listen();
                        display("Server started listening on port: " + server.getPort());
                    } catch (IOException e) {
                        display("Error starting server: " + e.getMessage());
                    }
                }
                break;

            case "#getport":
                display("Current port: " + server.getPort());
                break;

            default:
                display("Unknown command: " + cmd);

        }


    }

    /**
     * Interface for printing out message
     */
    @Override
    public void display(String message) {
        System.out.println(message);
    }

    /**
    * Main method for running server
    */

    public static void main(String[] args) {
        int port = EchoServer.DEFAULT_PORT;
        if(args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, using default!");
            }
        }

        EchoServer server = new EchoServer(port);
        try {
            server.listen();
            System.out.println("server listening on port: " + port);
        } catch (IOException e) {
            System.out.println("Could not start server!" + e.getMessage());
        }

        ServerConsole console = new ServerConsole(server);
        console.accept();
    }

}

