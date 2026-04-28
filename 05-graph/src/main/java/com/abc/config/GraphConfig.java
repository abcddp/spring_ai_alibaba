package com.abc.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphConfig {
    private static final Logger log = LoggerFactory.getLogger(GraphConfig.class);

    @Bean
    public CompiledGraph quickStartGraph() throws GraphStateException {
        //定义状态图
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("input1", new ReplaceStrategy(),
                "input2", new ReplaceStrategy());

        StateGraph stateGraph = new StateGraph("quickStartGraph", keyStrategyFactory);

        //定义节点
        stateGraph.addNode("node1", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node1 state: {}", state);
                return Map.of("input1",1,"input2",1);
            }
        }));

        stateGraph.addNode("node2", AsyncNodeAction.node_async(new NodeAction() {
            @Override
            public Map<String, Object> apply(OverAllState state) throws Exception {
                log.info("node2 state: {}", state);
                return Map.of("input1",2,"input2",2);
            }
        }));

        //定义边
        stateGraph.addEdge(StateGraph.START, "node1");
        stateGraph.addEdge("node1", "node2");
        stateGraph.addEdge("node2", StateGraph.END);

        return stateGraph.compile();
    }
}
