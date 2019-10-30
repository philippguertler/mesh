package com.gentics.mesh.core.data.root;

import static com.gentics.mesh.test.TestSize.PROJECT_AND_NODE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gentics.madl.tx.Tx;
import com.gentics.mesh.core.data.node.impl.NodeImpl;
import com.gentics.mesh.test.context.AbstractMeshTest;
import com.gentics.mesh.test.context.MeshTestSetting;
import com.syncleus.ferma.FramedGraph;

@MeshTestSetting(testSize = PROJECT_AND_NODE, startServer = false)
public class NodeRootTest extends AbstractMeshTest {

	@Test
	public void testAddNode() {
		try (Tx tx = tx()) {
			FramedGraph graph = tx.getGraph();
			NodeRoot root = project().getNodeRoot();
			NodeImpl node = graph.addFramedVertex(NodeImpl.class);
			long start = root.computeCount();
			root.addItem(node);
			root.addItem(node);
			root.addItem(node);
			root.addItem(node);
			assertEquals(start + 1, root.computeCount());
		}
	}

}
