package com.sandboni.core.engine.sta.operation;

import com.sandboni.core.engine.result.ChangeStats;
import com.sandboni.core.engine.result.FormattedChangeStats;
import com.sandboni.core.engine.sta.graph.Edge;
import com.sandboni.core.engine.sta.graph.vertex.Vertex;
import org.jgrapht.Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class FormattedChangeStatsOperation extends AbstractGraphOperation<SetResult<FormattedChangeStats>> {

    private final Map<Vertex, ChangeStats> changeStats;

    public FormattedChangeStatsOperation(Map<Vertex, ChangeStats> changeStats) {
        Objects.requireNonNull(changeStats, INVALID_ARGUMENT);
        this.changeStats = new HashMap<>(changeStats);
    }

    @Override
    public SetResult<FormattedChangeStats> execute(Graph<Vertex, Edge> graph) {
        Map<String, FormattedChangeStats> formatted = new HashMap<>();
        changeStats.forEach((key, value) -> formatted.computeIfAbsent(key.getActor(), FormattedChangeStats::new)
                .addMethod(key.getAction(), value));
        return new SetResult<>(new HashSet<>(formatted.values()));
    }

}
