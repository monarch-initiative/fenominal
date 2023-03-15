package org.monarchinitiative.fenominal.core.impl.lexical;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LexicalResources {

    private static final String CLUSTER_SIGNATURE = "$";

    private static final Logger LOGGER = LoggerFactory.getLogger(LexicalResources.class);

    private static final String LEXICAL_RESOURCE_PATH = "org/monarchinitiative/fenominal/core/impl/lexical/";

    private final Map<String, String> invertedIndex;

    private final Map<Integer, Double> tblatOptimalThresholds;

    private final Map<String, Map<String, Double>> trigramStates;

    public LexicalResources() {
        invertedIndex = new LinkedHashMap<>();
        tblatOptimalThresholds = new LinkedHashMap<>();
        trigramStates = new LinkedHashMap<>();

        loadLexicalClusters();
        loadOptimalThresholds();
        loadTrigramStates();
    }

    private void loadTrigramStates() {
        try (InputStream inputStream = getResourceAsStream(LEXICAL_RESOURCE_PATH + "tblat_trigram_states")) {
            String text = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
            String[] lines = text.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.equalsIgnoreCase("")) {
                    String[] splits = line.split("==");
                    String head = splits[0];
                    Map<String, Double> states = new LinkedHashMap<>();
                    String[] values = splits[1].split("\\|");
                    for (String value : values) {
                        String[] actualValues = value.split("::");
                        states.put(actualValues[0], Double.parseDouble(actualValues[1]));
                    }
                    trigramStates.put(head, states);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void loadOptimalThresholds() {
        try (InputStream inputStream = getResourceAsStream(LEXICAL_RESOURCE_PATH + "tblat_optimal_thresholds")) {
            String text = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
            String[] lines = text.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.equalsIgnoreCase("")) {
                    String[] splits = line.split(",");
                    tblatOptimalThresholds.put(Integer.parseInt(splits[0]), Double.parseDouble(splits[1]));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void loadLexicalClusters() {
        try (InputStream inputStream = getResourceAsStream(LEXICAL_RESOURCE_PATH + "clusters")) {
            String text = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
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

    public Map<String, String> getNegationClues() {
        final Map<String, String> negationClues = new LinkedHashMap<>();
        try (InputStream inputStream = getResourceAsStream(LEXICAL_RESOURCE_PATH + "negation.clues")) {
            String text = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
            String[] lines = text.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!"".equals(line)) {
                    negationClues.put(line, "");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return negationClues;
    }

    public Set<String> getClusters(Set<String> words) {
        Set<String> clusters = new HashSet<>();
        for (String word : words) {
            clusters.add(invertedIndex.getOrDefault(word.toLowerCase(), word));
        }
        return clusters;
    }

    public List<String> getClusters(List<String> words) {
        List<String> clusters = new ArrayList<>();
        for (String word : words) {
            clusters.add(invertedIndex.getOrDefault(word.toLowerCase(), word));
        }
        return clusters;
    }

    public String getCluster(String word) {
        return invertedIndex.getOrDefault(word, word);
    }

    public Map<String, Map<String, Double>> getTrigramStates() {
        return trigramStates;
    }

    public double getThresholdForLength(int length) {
        if (this.tblatOptimalThresholds.containsKey(length)) {
            return this.tblatOptimalThresholds.get(length);
        }

        return 0.0;
    }

    private static InputStream getResourceAsStream(String resourcePath) {
        InputStream is = LexicalResources.class.getResourceAsStream(resourcePath);
        if (is != null)
            return is;

        is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        if (is != null)
            return is;

        // load from the module path
        Optional<Module> fenominalCoreOptional = ModuleLayer.boot().findModule("org.monarchinitiative.fenominal.core");
        if (fenominalCoreOptional.isPresent()) {
            Module fenominal = fenominalCoreOptional.get();
            try {
                return fenominal.getResourceAsStream(resourcePath);
            } catch (IOException e) {
                // swallow
            }
        }

        throw new IllegalStateException("Unable to load resource " + resourcePath);
    }
}
