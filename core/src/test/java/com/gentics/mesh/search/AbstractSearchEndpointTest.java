package com.gentics.mesh.search;

import static com.gentics.mesh.core.data.ContainerType.DRAFT;
import static com.gentics.mesh.core.data.ContainerType.PUBLISHED;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.gentics.mesh.core.data.Project;
import com.gentics.mesh.core.data.Release;
import com.gentics.mesh.core.data.schema.SchemaContainerVersion;
import com.gentics.mesh.dagger.MeshInternal;
import com.gentics.mesh.error.InvalidArgumentException;
import com.gentics.mesh.search.index.IndexHandler;
import com.gentics.mesh.search.index.node.NodeIndexHandler;
import com.gentics.mesh.test.AbstractRestEndpointTest;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public abstract class AbstractSearchEndpointTest extends AbstractRestEndpointTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractSearchEndpointTest.class);

	@BeforeClass
	public static void initMesh() throws Exception {
		init(true);
		initDagger();
	}

	@Before
	public void setupHandlers() throws Exception {
		for (IndexHandler handler : meshDagger.indexHandlerRegistry().getHandlers()) {
			handler.init().await();
		}
	}

	@After
	public void resetElasticSearch() {
		// searchProvider.reset();
		searchProvider.clear();
	}

	@BeforeClass
	@AfterClass
	public static void clean() throws IOException {
		FileUtils.deleteDirectory(new File("data"));
	}

	protected String getSimpleQuery(String text) throws JSONException {
		QueryBuilder qb = QueryBuilders.queryStringQuery(text);
		JSONObject request = new JSONObject();
		request.put("query", new JSONObject(qb.toString()));
		String query = request.toString();
		if (log.isDebugEnabled()) {
			log.debug(query);
		}
		return query;
	}

	protected String getSimpleTermQuery(String key, String value) throws JSONException {
		QueryBuilder qb = QueryBuilders.termQuery(key, value);
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		bqb.must(qb);

		JSONObject request = new JSONObject();
		request.put("query", new JSONObject(bqb.toString()));
		String query = request.toString();
		if (log.isDebugEnabled()) {
			log.debug(query);
		}
		return query;
	}

	protected String getSimpleWildCardQuery(String key, String value) throws JSONException {
		QueryBuilder qb = QueryBuilders.wildcardQuery(key, value);
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		bqb.must(qb);

		JSONObject request = new JSONObject();
		request.put("query", new JSONObject(bqb.toString()));
		String query = request.toString();
		if (log.isDebugEnabled()) {
			log.debug(query);
		}
		return query;
	}

	protected String getRangeQuery(String fieldName, double from, double to) throws JSONException {
		RangeQueryBuilder range = QueryBuilders.rangeQuery(fieldName).from(from).to(to);
		return "{ \"query\": " + range.toString() + "}";
	}

	protected void fullIndex() throws InterruptedException, InvalidArgumentException {
		Project project = project();
		for (Release release : project.getReleaseRoot().findAll()) {
			for (SchemaContainerVersion version : release.findAllSchemaVersions()) {
				String draftIndex = NodeIndexHandler.getIndexName(project.getUuid(), release.getUuid(), version.getUuid(), DRAFT);
				if (log.isDebugEnabled()) {
					log.debug("Creating schema mapping for index {" + draftIndex + "}");
				}
				meshDagger.nodeIndexHandler().updateNodeIndexMapping(draftIndex, version.getSchema()).await();

				String publishIndex = NodeIndexHandler.getIndexName(project.getUuid(), release.getUuid(), version.getUuid(), PUBLISHED);
				if (log.isDebugEnabled()) {
					log.debug("Creating schema mapping for index {" + publishIndex + "}");
				}
				meshDagger.nodeIndexHandler().updateNodeIndexMapping(publishIndex, version.getSchema()).await();
			}
		}

		MeshInternal.get().searchQueue().addFullIndex().processSync();
	}

}