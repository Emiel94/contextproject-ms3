package contextproject.models;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import contextproject.helpers.StackTrace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Track {
  private static Logger log = LogManager.getLogger(Track.class.getName());

  private Mp3File song;
  private String title;
  private String artist;
  private String album;
  private String absolutePath;
  private long length;
  private double bpm;
  private Key key;

  /**
   * constructor of a track.
   * 
   * @param abPath
   *          Path of the mp3 file
   */
  public Track(String abPath) {
    try {
      song = new Mp3File(abPath);
    } catch (UnsupportedTagException e) {
      log.error("There was a Unsupported tag exception with file:" + abPath);
      log.trace(StackTrace.stackTrace(e));
    } catch (InvalidDataException e) {
      log.error("There was a Invalid data exception with file:" + abPath);
      log.trace(StackTrace.stackTrace(e));
    } catch (IOException e) {
      log.error("There was a IO exception with file:" + abPath);
      log.trace(StackTrace.stackTrace(e));
    }
    absolutePath = abPath;
    getMetadata();
  }

  /**
   * get information from Id3Tag.
   */
  private void getMetadata() {
    if (song.hasId3v2Tag()) {
      title = song.getId3v2Tag().getTitle();
      artist = song.getId3v2Tag().getArtist();
      album = song.getId3v2Tag().getAlbum();
      bpm = song.getId3v2Tag().getBPM();
      try {
        key = new Key(song.getId3v2Tag().getKey());
      } catch (IllegalArgumentException e) {
        log.warn("Could not find key information in: " + song.getFilename());
      }
    } else {
      log.warn("Could not find Id3v2 information in: " + song.getFilename());
    }
    length = song.getLengthInMilliseconds();
  }

  /**
   * String with title.
   * 
   * @return String
   */
  public String getTitle() {
    return title;
  }

  /**
   * String with artist.
   * 
   * @return String
   */
  public String getArtist() {
    return artist;
  }

  /**
   * String with album.
   * 
   * @return String
   */
  public String getAlbum() {
    return album;
  }

  /**
   * String with absolute path.
   * 
   * @return String
   */
  public String getPath() {
    return absolutePath;
  }

  /**
   * Long with track length.
   * 
   * @return String
   */
  public Long getLength() {
    return length;
  }

  /**
   * Beats per minute of the track.
   * 
   * @return int
   */
  public double getBpm() {
    return bpm;
  }

  /**
   * Track key object.
   * 
   * @return Key
   */
  public Key getKey() {
    return key;

  }

  /**
   * Equals method to check if an object is the same as the Track object.
   * 
   * @param other
   *          object
   * @return true if equals, else false.
   */
  public boolean equals(Object other) {
    if (other instanceof Track) {
      return (this.getPath().equals(((Track) other).getPath()));
    }
    return false;
  }
}
