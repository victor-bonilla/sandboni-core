package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.CucumberVertex;
import com.sandboni.core.engine.sta.graph.vertex.TestVertex;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sandboni.core.engine.common.StreamHelper.emptyIfFalse;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.END_VERTEX;
import static com.sandboni.core.engine.sta.graph.vertex.VertexInitTypes.START_VERTEX;

public class RelatedTestsOperation extends AbstractGraphOperation<SetResult<TestVertex>> {

    private static final Logger logger = LoggerFactory.getLogger(RelatedTestsOperation.class);

    private final Set<TestVertex> allTests;

    RelatedTestsOperation(Set<TestVertex> allTests) {
        Objects.requireNonNull(allTests, INVALID_ARGUMENT);
        this.allTests = new HashSet<>(allTests);
    }

    @Override
    public SetResult<TestVertex> execute(Graph<Vertex, Edge> graph) {
        ShortestPathAlgorithm<Vertex, Edge> algorithm = new BellmanFordShortestPath<>(graph);
        return new SetResult<>(emptyIfFalse(graph.containsVertex(START_VERTEX) && graph.containsVertex(END_VERTEX),
                () -> allTests.stream()
                        .filter(v -> {
                            GraphPath<Vertex, Edge> path = algorithm.getPath(END_VERTEX, v);
                            if (path != null && logger.isDebugEnabled()) {
                                logger.debug("Found path for {} = {}", v, getPathString(path.getEdgeList()));
                            }
                            return isAffectedCucumberVertex(v) || path != null;
                        })));
    }

    private String getPathString(List<Edge> edgeList) {
        return edgeList.stream()
                .map(edge -> String.format("Type: %s - %s -> %s", edge.getLinkType(), edge.getSource(), edge.getTarget()))
                .collect(Collectors.joining(", "));
    }

    private boolean isAffectedCucumberVertex(Vertex v) {
        return v instanceof CucumberVertex && ((CucumberVertex) v).isAffected();
    }
}
