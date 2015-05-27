package contextproject.sorters;

import static org.junit.Assert.assertEquals;

import contextproject.models.Key;
import contextproject.models.Playlist;
import contextproject.models.Track;
import contextproject.sorters.GreedyPlaylistSorter;

import org.junit.Before;
import org.junit.Test;

public class GreedyPlaylistSorterTest {

  private Playlist playlist = new Playlist();
  private Track track = new Track();
  private Track track2 = new Track();
  private Track track3 = new Track();
  /**
   * Setting up playlist that can be sorted.
   */
  @Before
  public void setUp() {
    playlist.setName("testList");
    track.setKey(new Key("7A"));
    track.setBpm(180);
    track.setTitle("test1");
    track2.setKey(new Key("3B"));
    track2.setBpm(120);
    track2.setTitle("");
    track3.setKey(new Key("2A"));
    track3.setBpm(180);
    playlist.add(track);
    playlist.add(track2);
    playlist.add(track3);
  }

  @Test
  public void sortNoMatchTrackTest() {
    GreedyPlaylistSorter greedySort = new GreedyPlaylistSorter();
    assertEquals(greedySort.sort(playlist).size(), 2);
  }
}
