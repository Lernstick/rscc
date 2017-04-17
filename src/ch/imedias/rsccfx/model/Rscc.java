package ch.imedias.rsccfx.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Stores the key and keyserver connection details.
 * Handles communication with the keyserver.
 */
public class Rscc {
  /**
   * Points to the "docker-build_p2p" folder inside resources, relative to the build path.
   * Important: Make sure to NOT include a / in the beginning or the end.
   */
  private static final String DOCKER_FOLDER_NAME = "docker-build_p2p";
  /**
   * sh files can not be executed in the JAR file and therefore must be extracted.
   * ".rscc" is a hidden folder in the user's home directory (e.g. /home/user)
   */
  private static final String RSCC_FOLDER_NAME = ".rscc";
  private final SystemCommander systemCommander;
  private String pathToResourceDocker;
  private StringProperty key = new SimpleStringProperty();
  private String keyServerIp = "86.119.39.89";
  private String keyServerHttpPort = "800";
  //TODO: Replace when the StunFileGeneration is ready
  private String pathToStunDumpFile = this.getClass()
      .getClassLoader().getResource("ice4jDemoDump.ice")
      .toExternalForm().replace("file:","");

  /**
   * Initializes the Rscc model class.
   */
  public Rscc(SystemCommander systemCommander) {
    this.systemCommander = systemCommander;
    defineResourcePath();
  }

  /**
   * Sets resource path, according to the application running either as a JAR or in the IDE.
   */
  private void defineResourcePath() {
    String userHome = System.getProperty("user.home");
    URL theLocationOftheRunningClass = this.getClass().getProtectionDomain()
        .getCodeSource().getLocation();
    File actualClass = new File(theLocationOftheRunningClass.getFile());
    if (actualClass.isDirectory()) {
      pathToResourceDocker =
          getClass().getClassLoader().getResource(DOCKER_FOLDER_NAME)
              .getFile().toString().replaceFirst("file:", "");

    } else {
      pathToResourceDocker = userHome + "/" + RSCC_FOLDER_NAME + "/" + DOCKER_FOLDER_NAME;
      extractJarContents(theLocationOftheRunningClass,
          userHome + "/" + RSCC_FOLDER_NAME, DOCKER_FOLDER_NAME);
    }
  }

  /**
   * Extracts files from running JAR to folder.
   * @param filter filters the files that will be extracted by this string.
   */
  private void extractJarContents(URL sourceLocation, String destinationDirectory, String filter) {
    JarFile jarFile = null;
    try {
      jarFile = new JarFile(new File(sourceLocation.getFile()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    Enumeration<JarEntry> contentList = jarFile.entries();
    while (contentList.hasMoreElements()) {
      JarEntry item = contentList.nextElement();
      if (item.getName().contains(filter)) {
        System.out.println(item.getName());
        File targetFile = new File(destinationDirectory, item.getName());
        if (!targetFile.exists()) {
          targetFile.getParentFile().mkdirs();
          targetFile = new File(destinationDirectory, item.getName());
        }
        if (item.isDirectory()) {
          continue;
        }
        try (
            InputStream fromStream = jarFile.getInputStream(item);
            FileOutputStream toStream = new FileOutputStream(targetFile);
        ) {
          while (fromStream.available() > 0) {
            toStream.write(fromStream.read());
          }

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        targetFile.setExecutable(true);
      }
    }
  }

  /**
   * Sets the IP and HTTP port of the keyserver in the model.
   */
  public void keyServerSetup(String keyServerIp, String keyServerHttpPort) {
    this.keyServerIp = keyServerIp;
    this.keyServerHttpPort = keyServerHttpPort;
  }

  /**
   * Sets up the server with use.sh.
   */
  private void keyServerSetup() {
    String command = commandStringGenerator(
        pathToResourceDocker, "use.sh", keyServerIp, keyServerHttpPort);
    systemCommander.executeTerminalCommand(command);
  }

  /**
   * Kills the connection to the keyserver.
   */
  public void killConnection() {
    // Execute port_stop.sh with the generated key to kill the connection
    String command = commandStringGenerator(pathToResourceDocker, "port_stop.sh", getKey());
    systemCommander.executeTerminalCommand(command);
    setKey("");
  }

  /**
   * Requests a token from the key server.
   */
  public void requestTokenFromServer() {
    keyServerSetup();

    String command = commandStringGenerator(
        pathToResourceDocker, "start_x11vnc.sh", pathToStunDumpFile);
    String key = systemCommander.executeTerminalCommand(command);
    setKey(key); // update key in model
  }

  /**
   * Starts connection to the user.
   */
  public void connectToUser() {
    keyServerSetup();

    String command = commandStringGenerator(pathToResourceDocker, "start_vncviewer.sh", getKey());
    systemCommander.executeTerminalCommand(command);
  }

  /**
   * Refreshes the key by killing the connection, requesting a new key and starting the server
   * again.
   */
  public void refreshKey() {
    killConnection();
    requestTokenFromServer();
  }

  /**
   * Generates String to run command.
   */
  private String commandStringGenerator(
      String pathToScript, String scriptName, String... attributes) {
    StringBuilder commandString = new StringBuilder();

    commandString.append(pathToScript).append("/").append(scriptName);
    Arrays.stream(attributes)
        .forEach((s) -> commandString.append(" ").append(s));

    return commandString.toString();
  }

  public StringProperty keyProperty() {
    return key;
  }

  public String getKey() {
    return key.get();
  }

  public void setKey(String key) {
    this.key.set(key);
  }

  public String getKeyServerIp() {
    return keyServerIp;
  }

  public String getKeyServerHttpPort() {
    return keyServerHttpPort;
  }
}
