package my.messenger.test.dao.graph.db;

import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder.DatabaseCreator;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseFactoryState;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.Settings;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.EmbeddedReadOnlyGraphDatabase;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.InternalAbstractGraphDatabase.Dependencies;
import org.neo4j.shell.ShellSettings;
import org.springframework.data.neo4j.support.index.IndexType;

public class Neo4JDBFactory extends GraphDatabaseFactory {

	private final String FULL_TEXT_INDEXES = "people,movie,actor";
	private final String NODE_KEYS_INDEXABLE = "name,email";

	public Neo4JDBFactory() {
		super();
	}

	@Override
	public GraphDatabaseService newEmbeddedDatabase(String path) {
		GraphDatabaseBuilder dbBuilder = newEmbeddedDatabaseBuilder(path);
		GraphDatabaseService service = dbBuilder.newGraphDatabase();
		cratesAndAddFulltextIndexes((GraphDatabaseAPI) service, FULL_TEXT_INDEXES);

		return service;
	}

	public GraphDatabaseService newEmbeddedDatabase(String path, Map<String, String> config) {
		GraphDatabaseBuilder dbBuilder = newEmbeddedDatabaseBuilder(path);
		dbBuilder.setConfig(config);
		return dbBuilder.newGraphDatabase();
	}

	@Override
	public GraphDatabaseBuilder newEmbeddedDatabaseBuilder(final String path) {
		return createAndPrepareDatabaseBuilder(path);
	}

	private GraphDatabaseBuilder createAndPrepareDatabaseBuilder( final String path) {
		DatabaseCreator creator = createDatabaseCreator(path);
		GraphDatabaseBuilder dbBuilder = new GraphDatabaseBuilder(creator);

		dbBuilder.setConfig(ShellSettings.remote_shell_enabled, Settings.TRUE);
		// dbBuilder.setConfig(ShellSettings.remote_shell_read_only,
		// Settings.TRUE);
		dbBuilder.setConfig(GraphDatabaseSettings.node_keys_indexable, NODE_KEYS_INDEXABLE);
		dbBuilder.setConfig(GraphDatabaseSettings.node_auto_indexing, Settings.TRUE);
		dbBuilder.setConfig(GraphDatabaseSettings.relationship_auto_indexing, Settings.TRUE);

		return dbBuilder;
	}

	/**
	 * Create an index for every index name inside indexes
	 * 
	 * @param graphdb
	 * @param indexes
	 *            Index names separated by comma
	 */
	private void cratesAndAddFulltextIndexes(GraphDatabaseAPI graphdb,
			String indexes) {
		String[] arrayOfIndex = indexes.split(",");
		IndexManager indexMgr = graphdb.index();

		for (String index : arrayOfIndex) {
			indexMgr.forNodes(index, IndexType.FULLTEXT.getConfig());
		}

	}

	/**
	 * Creates an anonymous class of type DatabaseCreator
	 * 
	 * @param path
	 * @return
	 */
	private DatabaseCreator createDatabaseCreator(final String path) {
		
		final GraphDatabaseFactoryState state = getStateCopy();

		return new GraphDatabaseBuilder.DatabaseCreator() {

			@SuppressWarnings("deprecation")
			public GraphDatabaseService newDatabase(Map<String, String> config) {
				config.put("ephemeral", "false");
				Dependencies dependencies = state.databaseDependencies();
				if (Settings.TRUE.equalsIgnoreCase(config.get( GraphDatabaseSettings.read_only.name() ) )) {
					return new EmbeddedReadOnlyGraphDatabase(path, config, dependencies);
				} else {
					return new EmbeddedGraphDatabase(path, config, dependencies);
				}
			}
		};
	}
}
