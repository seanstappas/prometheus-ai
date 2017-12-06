package knn.internal;

import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import tags.Tag;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

class KnowledgeNodeNetworkImpl implements KnowledgeNodeNetwork {
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;
    private TreeSet<KnowledgeNode> ageSortedKNs;

    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;
    private BackwardSearcher backwardSearcher;
    private LambdaSearcher lambdaSearcher;

    @Inject
    public KnowledgeNodeNetworkImpl(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("ageSortedKNs") TreeSet<KnowledgeNode> ageSortedKNs,
            @Assisted("backwardSearchMatchRatio") double backwardSearchMatchRatio,
            @Assisted("backwardSearchAgeLimit") long backwardSearchAgeLimit,
            DirectSearcherFactory directSearcherFactory,
            ForwardSearcherFactory forwardSearcherFactory,
            BackwardSearcherFactory backwardSearcherFactory,
            LambdaSearcherFactory lambdaSearcherFactory) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
        this.ageSortedKNs = ageSortedKNs;
        this.directSearcher = directSearcherFactory.create(mapKN, activeTags, ageSortedKNs);
        this.forwardSearcher = forwardSearcherFactory.create(directSearcher);
        this.backwardSearcher = backwardSearcherFactory.create(
                activeTags, ageSortedKNs, backwardSearchMatchRatio, backwardSearchAgeLimit);
        this.lambdaSearcher = lambdaSearcherFactory.create(forwardSearcher, backwardSearcher);
    }

    @Override
    public void resetEmpty() {
        mapKN.clear();
        activeTags.clear();
        ageSortedKNs.clear();
    }

    @Override
    public void clearActiveTags() {
        activeTags.clear();
    }

    @Override
    public void addKnowledgeNode(KnowledgeNode kn) {
        mapKN.put(kn.getInputTag(), kn);
        ageSortedKNs.add(kn);
    }

    @Override
    public void deleteExpiredKnowledgeNodes() {
        Set<Tag> tagsToDelete = new HashSet<>();
        for (KnowledgeNode kn : mapKN.values()) {
            if (kn.isExpired()) {
                tagsToDelete.add(kn.getInputTag());
                ageSortedKNs.remove(kn);
            }
        }
        for (Tag t : tagsToDelete) {
            mapKN.remove(t);
            activeTags.remove(t);
        }
    }

    @Override
    public void deleteKnowledgeNode(Tag tag) {
        mapKN.remove(tag);
    }

    @Override
    public void addActiveTag(Tag tag) {
        activeTags.add(tag);
    }

    @Override
    public void addActiveTags(Tag... tags) {
        activeTags.addAll(Arrays.asList(tags));
    }

    @Override
    public Set<Tag> getActiveTags() {
        return Collections.unmodifiableSet(activeTags);
    }

    @Override
    public KnowledgeNode getKnowledgeNode(Tag tag) {
        return mapKN.get(tag);
    }

    @Override
    public Set<KnowledgeNode> getKnowledgeNodes() {
        return Collections.unmodifiableSet(ageSortedKNs);
    }

    @Override
    public Set<Tag> directSearch(Tag inputTag) {
        return directSearcher.search(inputTag);
    }

    @Override
    public Set<Tag> forwardSearch(Set<Tag> inputTags, int ply) {
        return forwardSearcher.search(inputTags, ply);
    }

    @Override
    public Set<Tag> forwardThink(int ply) {
        return forwardSearcher.search(activeTags, ply);
    }

    @Override
    public Set<Tag> backwardSearch(Set<Tag> inputTags, int ply) {
        return backwardSearcher.search(inputTags, ply);
    }

    @Override
    public Set<Tag> backwardThink(int ply) {
        return backwardSearcher.search(activeTags, ply);
    }

    @Override
    public void setBackwardSearchMatchRatio(double ratio) {
        backwardSearcher.setMatchRatio(ratio);
    }

    @Override
    public Set<Tag> lambdaSearch(Set<Tag> inputTags, int ply) {
        return lambdaSearcher.search(inputTags, ply);
    }

    @Override
    public Set<Tag> lambdaThink(int ply) {
        return lambdaSearcher.search(activeTags, ply);
    }

    @Override
    public List<KnowledgeNode> loadData(String filename) {
        List<KnowledgeNode> knowledgeNodes = new ArrayList<>();
        resetEmpty();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] info = line.split(";\\s+");
                KnowledgeNode kn = new KnowledgeNode(info);
                knowledgeNodes.add(kn);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (KnowledgeNode knowledgeNode : knowledgeNodes) {
            addKnowledgeNode(knowledgeNode);
        }
        return knowledgeNodes;
    }

    @Override
    public void reset(String dbFilename) {
        // TODO: Reset the KNN from a database.
    }

    @Override
    public void save(String dbFilename) {
        // TODO: Save the KNN to a database.
    }
}
