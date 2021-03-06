package contextproject.formats;

import static org.junit.Assert.assertTrue;

import contextproject.models.Library;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class XmlExportTest {

  private Library library;
  private String folder;

  /**
   * Before every test this method is done first to initialize the M3UExport.
   */
  @Before
  public void initialize() {
    folder = System.getProperty("user.dir").toString() + File.separator + "src" + File.separator
        + "test" + File.separator + "resources" + File.separator + "loadTest";
    library = new Library();
  }

  @Test
  public void fileExistsTest() {
    XmlExport export = new XmlExport(folder + File.separator + "test.xml", library);
    export.export();
    File file = new File(folder + File.separator + "test.xml");
    assertTrue(file.exists());
    file.delete();
  }
}
