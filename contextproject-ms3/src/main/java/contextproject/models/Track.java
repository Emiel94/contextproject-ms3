package contextproject.models;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import contextproject.helpers.StackTrace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Hashtable;

public class Track {
  private static Logger log = LogManager.getLogger(Track.class.getName());

  private Mp3File song;
  private String title;
  private String artist;
  private String album;
  private String absolutePath;
  private long length;
  private float bpm;
  private Key key;
  private BeatGrid beatGrid;

  /**
   * constructor of a track.
   * 
   * @param abPath
   *          Path of the mp3 file
   */
  public Track(String abPath, Hashtable<String, String> info) {

    // String title, String artist, String album, long length, double bpm, String key, long
    // firstBeat, int beatIntroStart, int beatsIntroLength, int beatOutroStart, int beatsOutroLength
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
    this.absolutePath = abPath;
    this.title = info.get("title");
    this.artist = info.get("artist");
    this.album = info.get("album");
    this.length = Long.parseLong(info.get("length"));
    this.bpm = Float.parseFloat(info.get("bpm"));
    if (info.get("key") != null) {
      this.key = new Key(info.get("key"));
    }
    getMetadata();
    this.beatGrid = new BeatGrid(this.length, this.bpm, Long.parseLong(info.get("firstBeat")),
        Integer.parseInt(info.get("startBeatIntro")),
        Integer.parseInt(info.get("introBeatLength")),
        Integer.parseInt(info.get("startBeatOutro")), 
        Integer.parseInt(info.get("outroBeatLength")));
  }

  /**
   * get information from Id3Tag.
   */
  private void getMetadata() {
    if (song.hasId3v2Tag()) {
      if (title == null) {
        title = song.getId3v2Tag().getTitle();
      }
      if (artist == null) {
        artist = song.getId3v2Tag().getArtist();
      }
      if (album == null) {
        album = song.getId3v2Tag().getAlbum();
      }
      if (bpm == 0) {
        bpm = song.getId3v2Tag().getBPM();
      }
      try {
        key = new Key(song.getId3v2Tag().getKey());
      } catch (IllegalArgumentException e) {
        log.warn("Could not find key information in: " + song.getFilename());
      }
    } else {
      log.warn("Could not find Id3v2 information in: " + song.getFilename());
    }
    if (length == 0) {
      length = song.getLengthInMilliseconds();
    }
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
   * BeatGrid object.
   * 
   * @return BeatGrida
   */
  public BeatGrid getBeatGrid() {
    return beatGrid;
  }
}
