package knn.internal;

import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import tags.Tag;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

class KnowledgeNodeNetworkImpl implements KnowledgeNodeNetwork {
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;
    private DirectSearcher directSearcher;
    private ForwardSearcher forwardSearcher;
    private BackwardSearcher backwardSearcher;
    private LambdaSearcher lambdaSearcher;

    // TODO: Sort nodes by age before searching. (Better: order by age in map, update at search)

    @Inject
    public KnowledgeNodeNetworkImpl(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags,
            @Assisted("backwardSearchMatchRatio") double backwardSearchMatchRatio,
            DirectSearcherFactory directSearcherFactory,
            ForwardSearcherFactory forwardSearcherFactory,
            BackwardSearcherFactory backwardSearcherFactory,
            LambdaSearcherFactory lambdaSearcherFactory) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
        this.directSearcher = directSearcherFactory.create(mapKN, activeTags);
        this.forwardSearcher = forwardSearcherFactory.create(directSearcher);
        this.backwardSearcher = backwardSearcherFactory.create(mapKN, activeTags, backwardSearchMatchRatio);
        this.lambdaSearcher = lambdaSearcherFactory.create(forwardSearcher, backwardSearcher);
    }

    @Override
    public void reset(String dbFilename) {

    }

    @Override
    public void resetEmpty() {
        clearKnowledgeNodes();
    }

    @Override
    public void save(String dbFilename) {

    }

    @Override
    public void clearKnowledgeNodes() {
        mapKN.clear();
        activeTags.clear();
    }

    @Override
    public void addKnowledgeNode(KnowledgeNode kn) {
        mapKN.put(kn.getInputTag(), kn);
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
        return activeTags;
    }

    @Override
    public KnowledgeNode getKnowledgeNode(Tag tag) {
        return mapKN.get(tag);
    }

    @Override
    public Collection<KnowledgeNode> getKnowledgeNodes() {
        return Collections.unmodifiableCollection(mapKN.values());
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
    public void loadData(String filename) {
        loadData(filename, new ArrayList<>());
    }

    @Override
    public void loadData(String filename, List<KnowledgeNode> knowledgeNodes) {
        resetEmpty();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename)); //change the local directory for the integration file to run
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
    }
}
