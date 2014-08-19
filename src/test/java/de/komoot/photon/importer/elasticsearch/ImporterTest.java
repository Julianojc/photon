package de.komoot.photon.importer.elasticsearch;

import de.komoot.photon.ESBaseTester;
import de.komoot.photon.importer.model.PhotonDoc;
import org.json.JSONObject;
import org.junit.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Peter Karich
 */
public class ImporterTest extends ESBaseTester {

	@Before
	public void setUp() {
		setUpES();
		deleteAll();
	}

	@Test
	public void testAdd() {
		Map<String, String> nameMap = new HashMap<String, String>();
		nameMap.put("name", "testing");
		PhotonDoc doc = this.createDoc(1, "way", 1, nameMap, 0., 0.);
		Importer instance = new Importer(getClient());
		instance.add(doc);
		instance.finish();

		refresh();
		assertEquals(1L, instance.count());

		final Searcher searcher = new Searcher(getClient());
		final List<JSONObject> results = searcher.search("testing", "en", null, null, 10, true);
		assertEquals(1, results.size());
	}
}