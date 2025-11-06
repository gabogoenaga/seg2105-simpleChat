// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import edu.seg2105.client.ui.ClientConsole;
import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();

    sendToServer("#login " + loginID);   //always sends login info to the server
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  /**
   * Method for handling commands locally
   */
  public void handleCommand(String command) {
      String[] parts = command.split(" ", 2);
      String cmd = parts[0];

      switch(cmd.toLowerCase()) {
          case "#quit":
              quit();
              break;

          case "#logoff":
              try {
                  closeConnection();
                  clientUI.display("Connection closed");
              } catch (IOException e) {
                  clientUI.display("Error logging off! " + e.getMessage());
              }
              break;

          case "#sethost":
              if (isConnected()) {
                  clientUI.display("Cannot set host while connected!");
              } else if (parts.length > 1) {
                  setHost(parts[1].trim());
                  clientUI.display("Host set to: " + parts[1].trim());
              } else {
                  clientUI.display("Usage: #sethost <hostname>");
              }
              break;

          case "#setport":
              if (isConnected()) {
                  clientUI.display("Cannot set port while connected!");
              } else if (parts.length > 1) {
                  try {
                      int port = Integer.parseInt(parts[1].trim());
                      setPort(port);
                      clientUI.display("Port set to: " + port);
                  } catch (NumberFormatException e) {
                      clientUI.display("Invalid port number!");
                  }
              } else {
                  clientUI.display("Usage: #setport <port>");
              }
              break;

          case "#login":
              if (isConnected()) {
                  clientUI.display("Already connected!");
              } else {
                  try {
                      openConnection();
                      clientUI.display("Connected to server!");
                  } catch (IOException e) {
                      clientUI.display("Cannot connect to server!");
                  }
              }
              break;

          case "#gethost":
              clientUI.display("Current host is: " + getHost());
              break;

          case "#getport":
              clientUI.display("Current port is: " + getPort());
              break;

          default:
              clientUI.display("Unkown command!" + cmd);
              break;
      }
  }


  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

    /**
     * This method is called when the server has closed the connection
     */
  public void connectionClosed(){
      clientUI.display("Server has shut down. Client will quit.");
  }

  public void connectionException(Exception exception) {
      clientUI.display("Server shutdown unexpectedly. Client will quit");
      System.exit(2);
  }

}
//End of ChatClient class
