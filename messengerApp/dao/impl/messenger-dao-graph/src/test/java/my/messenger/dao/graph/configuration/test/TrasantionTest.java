package my.messenger.dao.graph.configuration.test;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:META-INF/spring/tx-graph-context-test.xml"
})
@TransactionConfiguration(transactionManager="neo4jTransactionManager")
public class TrasantionTest {

	@Autowired
	private Neo4jTemplate neo4jTemplate;
	
	@Autowired
	private JtaTransactionManager neo4jTransactionManager;
	
	@Test
	@Transactional
	public void neo4jTemplateConfigurationTest(){
		Node node = neo4jTemplate.createNode();
		
		Assert.assertNotNull(node);
	}
	
	@Test
	public void transactionTest() throws SystemException{
		TransactionManager tm = neo4jTransactionManager.getTransactionManager();
		Transaction t = tm.getTransaction();
	}
}
