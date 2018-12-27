package com.voting.nodesresolver.impl;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class NodesResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodesResolver.class);

    private static final int TIMEOUT = 5 * 60000;

    private Set<String> nodesList = new HashSet<>();

    public void addNode(String nodeAddress) {
        nodesList.add(nodeAddress);
    }

    public Set<String> getNodesList() {
        return nodesList;
    }

    public void shareNode(String nodeUrl) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT)
                .setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT)
                .build();

        HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();

        Set<String> iteratorNodesList = new HashSet<>(nodesList);

        iteratorNodesList.parallelStream().forEach(node -> {
            try {
                if (!node.equals(nodeUrl)) {
                    HttpPost httpPost = new HttpPost(node + "/register");
                    httpPost.setHeader("Content-type", "application/json");


                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("node_url", nodeUrl);

                    httpPost.setEntity(new StringEntity(jsonObject.toString()));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (HttpStatus.OK.value() != httpResponse.getStatusLine().getStatusCode()) {
                        nodesList.remove(node);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);

                nodesList.remove(node);
            }
        });
    }
}
