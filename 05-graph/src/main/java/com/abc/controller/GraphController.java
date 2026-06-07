package com.abc.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/graph")
public class GraphController {
    private static final Logger log = LoggerFactory.getLogger(GraphController.class);

    private final CompiledGraph compiledGraph;
    private final CompiledGraph simpleGraph;
    private final CompiledGraph conditionalGraph;
    private final CompiledGraph loopGraph;

    public GraphController(@Qualifier("quickStartGraph")  CompiledGraph compiledGraph,
                           @Qualifier("simpleGraph")CompiledGraph simpleGraph,
                           @Qualifier("conditionalGraph")CompiledGraph conditionalGraph,
                           @Qualifier("loopGraph") CompiledGraph loopGraph) {
        this.compiledGraph = compiledGraph;
        this.simpleGraph = simpleGraph;
        this.conditionalGraph = conditionalGraph;
        this.loopGraph = loopGraph;
    }

    @GetMapping("/quickStartGraph")
    public String quickStartGraph(){
        Optional<OverAllState> optionalOverAllState = compiledGraph.call(Map.of());
        log.info("overAllState:{}",optionalOverAllState);
        return "ok";
    }

    @GetMapping("/simpleGraph")
    public Map<String, Object> simpleGraph(@RequestParam("word")  String word){
        Optional<OverAllState> optionalOverAllState = simpleGraph.call(Map.of("word",word));
        log.info("overAllState:{}",optionalOverAllState);
        return optionalOverAllState.map(OverAllState::data).orElse(Map.of());
    }

    @GetMapping("/conditionalGraph")
    public Map<String, Object> conditionalGraph(@RequestParam("topic") String topic){
        Optional<OverAllState> overAllStateOptional = conditionalGraph.call(Map.of("topic", topic));
        Map<String, Object> data = overAllStateOptional.map(OverAllState::data).orElse(Map.of());
        log.info("overAllState:{}",data);
        return data;
    }

    @GetMapping("/loopGraph")
    public Map<String, Object> loopGraph(@RequestParam("topic") String topic){
        Optional<OverAllState> overAllStateOptional = loopGraph.call(Map.of("topic", topic));
        Map<String, Object> data = overAllStateOptional.map(OverAllState::data).orElse(Map.of());
        log.info("overAllState:{}",data);
        return data;
    }

}
