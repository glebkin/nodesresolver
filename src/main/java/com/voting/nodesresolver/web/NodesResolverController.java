package com.voting.nodesresolver.web;

import com.voting.nodesresolver.impl.NodesResolver;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class NodesResolverController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodesResolverController.class);

    private final NodesResolver nodesResolver;

    @Autowired
    public NodesResolverController(NodesResolver nodesResolver) {
        this.nodesResolver = nodesResolver;
    }

    @RequestMapping(value = "/resolve", method = RequestMethod.POST)
    public Set<String> resolveNodes(@RequestBody String body) {
        String nodeAddress = "";
        if (StringUtils.isNotBlank(body)) {
            JSONObject jsonObject = new JSONObject(body);
            nodeAddress = jsonObject.get("node_url").toString();
        }

        if (StringUtils.isNotBlank(nodeAddress)) {
            nodesResolver.shareNode(nodeAddress);
        }

        Set<String> nodesList = new HashSet<>(nodesResolver.getNodesList());

        nodesResolver.addNode(nodeAddress);

        LOGGER.info("Nodes: {}", nodesResolver.getNodesList());
        return nodesList;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Set<String> getNodes() {
        return nodesResolver.getNodesList();
    }
}
