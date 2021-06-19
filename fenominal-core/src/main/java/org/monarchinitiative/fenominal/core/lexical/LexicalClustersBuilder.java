package org.monarchinitiative.fenominal.core.lexical;

import org.apache.commons.io.IOUtils;
import org.monarchinitiative.fenominal.json.CurieUtilBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexicalClustersBuilder {

    private static final String CLUSTER_SIGNATURE = "$";

    private static final Logger LOGGER = LoggerFactory.getLogger(LexicalClustersBuilder.class);

    private Map<String, String> invertedIndex;

    public LexicalClustersBuilder() {
        invertedIndex = new LinkedHashMap<>();
        loadClusters();
    }

    private void loadClusters() {
        try (InputStream inputStream = CurieUtilBuilder.class.getClassLoader().getResourceAsStream("clusters")) {
            String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
            String[] lines = text.split("\n");
            int id = 1;
            for (String line : lines) {
                line = line.trim();
                if (!"".equals(line)) {
                    String[] clusters = line.split(",");
                    for (String element : clusters) {
                        invertedIndex.put(element, CLUSTER_SIGNATURE + id + CLUSTER_SIGNATURE);
                    }
                    id++;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public Set<String> getClusters(Set<String> words) {
        Set<String> clusters = new HashSet<>();
        for (String word : words) {
            clusters.add(invertedIndex.containsKey(word.toLowerCase()) ? invertedIndex.get(word.toLowerCase()) : word);
        }
        return clusters;
    }

    public List<String> getClusters(List<String> words) {
        List<String> clusters = new ArrayList<>();
        for (String word : words) {
            clusters.add(invertedIndex.containsKey(word.toLowerCase()) ? invertedIndex.get(word.toLowerCase()) : word);
        }
        return clusters;
    }

    public String getCluster(String word) {
        return invertedIndex.containsKey(word) ? invertedIndex.get(word) : word;
    }
}
