package ch.imedias.rsccfx.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemCommander {

  /**
   * Executes a command in the Linux terminal.
   * more detailed instructions concerning this command can be found here:
   * https://www.cs.technik.fhnw.ch/confluence16/pages/viewpage.action?pageId=6929334
   *
   * @param command terminal command to be executed.
   * @return String trimmed output of the terminal without whitespaces at beginning / end.
   */
  public String executeTerminalCommand(String command) {
    Process process;

    try {
      StringBuffer output = new StringBuffer();
      // Execute Command
      process = Runtime.getRuntime().exec(command);
      process.waitFor();
      // read the output from the command
      BufferedReader outputReader = new BufferedReader(new
          InputStreamReader(process.getInputStream()));
      String line = null;
      while ((line = outputReader.readLine()) != null) {
        output.append(line + "\n");
      }
      outputReader.close();
      return output.toString().trim();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return "";
  }

}
