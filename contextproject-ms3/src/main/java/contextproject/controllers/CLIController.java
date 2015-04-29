package contextproject.controllers;

import contextproject.loaders.FolderLoader;
import contextproject.models.Playlist;

import java.io.IOException;
import java.util.Scanner;

/**
 * Controls CLI for time being.
 * 
 * @author Felix
 */
public class CLIController {

  private String fileName;
  private Playlist result;

  /**
   * Input is initialised to empty string and result to null.
   */
  public CLIController() {
    fileName = "";
    result = null;

  }
  /**
   * This will start a simple Command Line Interface for our first working version. Optionally to be
   * included in later versions.
   */
  public void run() {
    int input = 0;
    Scanner sc = new Scanner(System.in);
    System.out.println("Welcome to our application!");
    while (input != 4) {
      System.out.println("Option 1: Construct playlist! Enter foldername");
      System.out.println("Option 2: About");
      System.out.println("Option 3: Play music!");
      System.out.println("Option 4: Close");
      input = sc.nextInt();

      switch (input) {
        case 1 :
          try {
            this.setFileName(sc.nextLine());
            FolderLoader folderLoader = new FolderLoader(this.getFileName());
            this.result = new Playlist(folderLoader.load());
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
          break;
        case 2 :
          System.out.println(this.aboutDevs());
          break;
        case 3 :
          System.out.println("Let's play some music!");
          break;
        default :
          break;
      }
    }
    sc.close();
  }
  /**
   * This method prints info about the team behind this project.
   * 
   * @return a String with info
   */
  public String aboutDevs() {
    return "Hey, we are Davey, Felix, Emiel, Roy and Millen!\n"
        + "We are trying to create a cool product that could serve as the future of music\n"
        + "Not to sound overly ambitious..\n" + "Remember stay hungry, stay foolish!";
  }

  /**
   * This takes up the name of the music file to be read into the system.
   * 
   * @param input :Name of the file.
   * @throws IOException: To be thrown when name is invalid.
   */
  public void setFileName(String input) throws IOException {

    if (input == null || input.length() == 0) {
      throw new IOException("The name of the file is invalid!");
    } else {
      fileName = input;
    }
  }

  public String getFileName() {
    return this.fileName;
  }

  public Playlist getTrackOrdering() {
    return this.result;
  }
}