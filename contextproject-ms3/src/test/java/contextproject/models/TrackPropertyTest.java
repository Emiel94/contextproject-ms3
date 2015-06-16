package contextproject.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TrackPropertyTest {

  private MusicalKey testKey = new MusicalKey("A");
  URL resourceUrl = getClass().getResource("/beep.mp3");
  Path resourcePath;

  private Track testTrack;
  private TrackProperty testProperty;

  /**
   * This is were the Trackproperty is initialized for this UnitTest.
   */
  @Before
  public void setUp() {
    try {
      resourcePath = Paths.get(resourceUrl.toURI());
      testTrack = new Track(resourcePath.toString());

    } catch (URISyntaxException e) {
      fail("file wans't read correctly");
      e.printStackTrace();
    }
    testProperty = new TrackProperty("testName", "testArtist", "120", testKey, testTrack, "");
  }

  @Test
  public void keyTest() {
    testProperty.setKey(new MusicalKey("B"));
    assertEquals(testProperty.keyProperty().get(), testProperty.getKey());
  }

  @Test
  public void artistTest() {
    testProperty.setArtist("Felix!");
    assertEquals(testProperty.artistProperty().get(), testProperty.getArtist());
  }

  @Test
  public void titleTest() {
    testProperty.setTitle("Cool track!");
    assertEquals(testProperty.titleProperty().get(), testProperty.getTitle());
  }

  @Test
  public void bpmTest() {
    testProperty.setBpm("220");
    assertEquals(testProperty.bpmProperty().get(), testProperty.getBpm());
  }

  @Test
  public void trackTest() {
    testProperty.setTrack(testTrack);
    assertEquals(testProperty.trackProperty().get(), testProperty.getTrack());
  }

  @Test
  public void playingTest() {
    testProperty.setPlaying("Playing");
    assertEquals(testProperty.getPlaying(), testProperty.playingProperty().get());
  }
}
