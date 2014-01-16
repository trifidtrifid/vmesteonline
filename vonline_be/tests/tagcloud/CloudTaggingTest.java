package tagcloud;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;

public class CloudTaggingTest {
	
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testCreateMessage() {
		try {
			PersistenceManagerFactory pmf = PMF.get();
			PersistenceManager pm = pmf.getPersistenceManager();
			//createRubrics
			RubricTag rootRubric = new RubricTag(1L, null, "Root rubric");
			RubricTag subRubric = new RubricTag(2L, null, "Sub rubric");
			RubricTag subSubRubric = new RubricTag(3L, null, "SubSub rubric");
			pm.makePersistent(rootRubric);
			subRubric.setParent(rootRubric.getKey());
			pm.makePersistent(subRubric);
			subSubRubric.setParent(subRubric.getKey());
			pm.makePersistent(subSubRubric);
			//create rubricsTag for message
			RubricTag msgRubric1 = new RubricTag(2L, null, "Msg rubric1");
			RubricTag msgRubric2 = new RubricTag(3L, null, "Msg rubric2");
			Set<CloudTag> msgRubrics = new HashSet<CloudTag>();
			msgRubrics.add(msgRubric1);
			msgRubrics.add(msgRubric2);
			//post message in rubrics 2 and 3
			Message msg = new Message(0L, "MSG1 content", msgRubrics);
			pm.makePersistent(msg);
			//looking for the message in rubric 2 
			Query q = pm.newQuery(RubricTag.class);
			q.setFilter("tag == tagId");
			q.declareParameters("long tagId");
			for (long rId = 1; rId < 4; rId++) {
				try {
					//checkout rubric rId
					List<RubricTag> results = (List<RubricTag>) q.execute();
					if (!results.isEmpty()) {
						for (RubricTag rubric : results) {
							Key rc = rubric.getKey();
							if (null == rc.getParent()) {
								System.out.print("RUbric "
										+ rubric.getContent()
										+ " has no messages");

							} else {
								Message msgIr = pm.getObjectById(Message.class,
										rc);
								System.out.print("RUbric "
										+ rubric.getContent()
										+ " Contains message: " + msgIr);
							}
						}
					} else {
						// Handle "no results" case
					}
				} finally {
					q.closeAll();
				}
			}
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
}
