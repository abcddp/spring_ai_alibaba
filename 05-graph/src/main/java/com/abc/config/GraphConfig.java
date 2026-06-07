package com.abc.config;

import com.abc.node.*;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphConfig {
    private static final Logger log = LoggerFactory.getLogger(GraphConfig.class);

    @Bean("quickStartGraph")
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

    @Bean("simpleGraph")
    public CompiledGraph simpleGraph(ChatClient.Builder clientBuilder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("word", new ReplaceStrategy(),
                "sentence", new ReplaceStrategy(),
                "translation", new ReplaceStrategy());

        StateGraph stateGraph = new StateGraph("simpleGraph", keyStrategyFactory);

        //定义节点
        stateGraph.addNode("sentenceConstructionNode",
                AsyncNodeAction.node_async(new SentenceConstructionNode(clientBuilder)));

        stateGraph.addNode("translationNode",
                AsyncNodeAction.node_async(new TranslationNode(clientBuilder)));

        //定义边
        stateGraph.addEdge(StateGraph.START, "sentenceConstructionNode");
        stateGraph.addEdge("sentenceConstructionNode", "translationNode");
        stateGraph.addEdge("translationNode", StateGraph.END);

        return stateGraph.compile();
    }

    @Bean("conditionalGraph")
    public CompiledGraph conditionalGraph(ChatClient.Builder clientBuilder) throws GraphStateException{
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("topic",new ReplaceStrategy());
        // 定义状态图 StateGraph
        StateGraph stateGraph = new StateGraph("conditionalGraph", keyStrategyFactory);

        stateGraph.addNode("生成笑话",AsyncNodeAction.node_async(new GenerateJokeNode(clientBuilder)));
        stateGraph.addNode("评估笑话",AsyncNodeAction.node_async(new EvaluateJokesNode(clientBuilder)));
        stateGraph.addNode("优化笑话",AsyncNodeAction.node_async(new EnhancejokeQualityNode(clientBuilder)));

        stateGraph.addEdge(StateGraph.START,"生成笑话");
        stateGraph.addEdge("生成笑话", "评估笑话");
        //三个参数，第一个，来源节点，第二个，来源节点的哪个参数，第三个，参数的值匹配后续节点的集合
        stateGraph.addConditionalEdges("评估笑话", AsyncEdgeAction.edge_async(new EdgeAction() {
            @Override
            public String apply(OverAllState state) throws Exception {
                return state.value("result", "优秀");
            }
        }), Map.of("优秀", StateGraph.END, "不够优秀","优化笑话"));

        stateGraph.addEdge("优化笑话", StateGraph.END);

        return stateGraph.compile();
    }

    @Bean("loopGraph")
    public CompiledGraph loopGraph(ChatClient.Builder clientBuilder) throws GraphStateException{
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("topic",new ReplaceStrategy());
        // 定义状态图 StateGraph
        StateGraph stateGraph = new StateGraph("loopGraph", keyStrategyFactory);
        stateGraph.addNode("生成笑话",AsyncNodeAction.node_async(new GenerateJokeNode(clientBuilder)));
        stateGraph.addNode("评估笑话",AsyncNodeAction.node_async(new LoopEvaluateJokesNode(clientBuilder,7,3)));

        // 定义边
        stateGraph.addEdge(StateGraph.START,"生成笑话");
        stateGraph.addEdge("生成笑话","评估笑话");

        stateGraph.addConditionalEdges("评估笑话",AsyncEdgeAction.edge_async(new EdgeAction() {
            @Override
            public String apply(OverAllState state) throws Exception {
                return state.value("result","loop");
            }
        }),Map.of("loop","生成笑话","break",StateGraph.END));
        return stateGraph.compile();
    }

}
