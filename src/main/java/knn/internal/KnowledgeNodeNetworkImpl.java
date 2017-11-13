package knn.internal;

import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import knn.api.Tuple;
import tags.Fact;
import tags.Recommendation;
import tags.Tag;

import javax.inject.Inject;
import java.util.*;

class KnowledgeNodeNetworkImpl implements KnowledgeNodeNetwork {
    private Map<Tag, KnowledgeNode> mapKN;
    private Set<Tag> activeTags;

    @Inject
    public KnowledgeNodeNetworkImpl(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("activeTags") Set<Tag> activeTags) {
        this.mapKN = mapKN;
        this.activeTags = activeTags;
    }

    @Override
    public void reset(String dbFilename) {

    }

    @Override
    public void resetEmpty() {
        clearKN();
    }

    @Override
    public void saveKNN(String dbFilename) {

    }

    @Override
    public void clearKN() {
        mapKN.clear();
        activeTags.clear();
    }

    @Override
    public void addKN(KnowledgeNode kn) {
        mapKN.put(kn.inputTag, kn);
    }

    @Override
    public void delKN(Tag tag) {
        mapKN.remove(tag);
    }

    @Override
    public void addFiredTag(Tag tag) {
        activeTags.add(tag);
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
    public void lambdaSearch(List<Tuple> nnOutputs, Tag item) {
        Map<Tag, Double> bestPath = new HashMap<>();
        double bestObjectTruth = 0;

        //Convert information from the NN to tags
        getInputForBackwardSearch(nnOutputs);

        for (Tag startPoint : activeTags) {
            List<Tag> commonParents = findCommonParents(item, startPoint);

            //For each parent of the startPoint, find if there is a non cycle path that connect both the startPoint and the Item tag
            for (Tag parentOfStartPoint : commonParents) {
                List<Tag> descendants = new ArrayList<>();
                depthFirstSearch(parentOfStartPoint, descendants);
                for (Tag tag : descendants) {
                    if (tag.equals(item)) {
                        resetAllObjectTruth(startPoint, bestPath); //ObjectTruth of Tag t should not be reset because it is the starting point to calculate a new path
                        List<Tag> bads = new ArrayList<>();
                        List<Tag> pathParentToItem = pathFinder(parentOfStartPoint, tag, bads); //path from the common parent the item
                        List<Tag> pathParentToStartPoint; //path from the common parent to the startPoint
                        List<Tag> prevPathParentToStartPoint;
                        int numOfSame;

                        //find if there is any tag beside the startPoint and the parentOfStartPoint from the pathParentToStartPoint is also belong to pathParentToItem
                        //if it does, then try to find another path for pathParentToStartPoint until there is no more possible path.
                        do {
                            pathParentToStartPoint = pathFinder(parentOfStartPoint, startPoint, bads);
                            numOfSame = 0;
                            for (Tag tagInParentToStartPoint : pathParentToStartPoint) {
                                if (!tagInParentToStartPoint.equals(startPoint) && !tagInParentToStartPoint.equals(parentOfStartPoint)) {
                                    if (pathParentToItem.contains(tagInParentToStartPoint)) {
                                        bads.add(tagInParentToStartPoint);
                                        numOfSame++;
                                        break;
                                    }
                                }
                            }
                            prevPathParentToStartPoint = pathFinder(parentOfStartPoint, startPoint, bads);
                        } while (numOfSame > 0 && !pathParentToStartPoint.equals(prevPathParentToStartPoint));

                        //If all the possible for pathParentToStartPoint has a tag that is also belong to the path of pathParentToItem, then this parent is will create a path containing a cycle within, therefore we need to skip
                        if (numOfSame > 0) {
                            break;
                        }

                        objectTruthEstimate(pathParentToItem, pathParentToStartPoint);
                        bestObjectTruth = getBestObjectTruth(bestPath, bestObjectTruth, tag, pathParentToItem, pathParentToStartPoint);
                    }
                }
            }
        }

        //add all the tag from the best path to the activeTags list of the KNN
        for (Tag t : bestPath.keySet()) {
            mapKN.get(t).setBelief(bestPath.get(t));
        }
        activeTags.addAll(bestPath.keySet());
    }

    @Override
    public void backwardSearch(List<Tuple> nnOutputs, double score) {
        getInputForBackwardSearch(nnOutputs);
        Set<Tag> pendingFacts = new HashSet<>();
        do {
            backwardSearchCycle(score, pendingFacts);
        } while (!pendingFacts.isEmpty());
    }

    @Override
    public void backwardSearch(List<Tuple> nnOutputs, double score, int ply) {
        getInputForBackwardSearch(nnOutputs);
        Set<Tag> pendingFacts = new HashSet<>();
        for (int i = 0; i < ply; i++) {
            backwardSearchCycle(score, pendingFacts);
        }

    }

    private void backwardSearchCycle(double score, Set<Tag> pendingFacts) {
        pendingFacts.clear();
        Set<Tag> prevActiveTags = new HashSet<>(activeTags);
        for (KnowledgeNode kn : mapKN.values()) {
            int matching = 0;
            for (Tag t : kn.outputs.keySet()) {
                if (prevActiveTags.contains(t)) {
                    matching++;
                    double backwardConfidence = (kn.outputs.get(t) * mapKN.get(t).belief) / 100;
                    kn.listOfRelatedTruths.put(t, backwardConfidence);
                }
            }
            if ((double) matching / kn.outputs.size() >= score) {
                Tag knType = kn.inputTag;
                kn.updateBelief();
                if (!activeTags.contains(knType)) {
                    pendingFacts.add(knType);
                }
            }
        }
        activeTags.addAll(pendingFacts);
    }

    @Override
    public void getInputForBackwardSearch(List<Tuple> nnOutputs) {
        for (Tuple tp : nnOutputs) {
            for (KnowledgeNode kn : mapKN.values()) {
                if (kn.inputTag.type.equals(Tag.TagType.FACT)) {
                    if (((Fact) kn.inputTag).getPredicateName().equals(tp.s)) {
                        kn.listOfRelatedTruths.put(kn.inputTag, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        activeTags.add(kn.inputTag);
                    }
                } else if (kn.inputTag.type.equals(Tag.TagType.RECOMMENDATION)) {
                    if (((Recommendation) kn.inputTag).getPredicateName().equals(tp.s)) {
                        kn.listOfRelatedTruths.put(kn.inputTag, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        activeTags.add(kn.inputTag);
                    }
                } else if (kn.inputTag.type.equals(Tag.TagType.RULE)) {
                    if (kn.inputTag.toString().equals(tp.s)) {
                        kn.listOfRelatedTruths.put(kn.inputTag, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        activeTags.add(kn.inputTag);
                    }
                }
            }
        }
    }

    @Override
    public void forwardSearch(List<Tuple> nnOutputs, int ply) {
        getInputForForwardSearch(nnOutputs);

        if (ply > 0 && !activeTags.isEmpty()) {
            for (int i = 0; i < ply; i++) {
                ArrayList<Tag> activeList = new ArrayList<>();
                activeList.addAll(activeTags);

                for (Tag t : activeList) {
                    if (mapKN.containsKey(t)) {
                        if (!mapKN.get(t).isFired && mapKN.get(t).activation >= mapKN.get(t).threshold) {
                            excite(mapKN.get(t), 0);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void forwardSearch(List<Tuple> nnOutputs) {
        getInputForForwardSearch(nnOutputs);

        boolean allActivated;
        do {
            allActivated = true;
            ArrayList<Tag> activeList = new ArrayList<>();

            activeList.addAll(activeTags);

            for (Tag t : activeList) {
                if (mapKN.containsKey(t)) {
                    if (!mapKN.get(t).isFired && mapKN.get(t).activation >= mapKN.get(t).threshold) {
                        excite(mapKN.get(t), 0);
                        allActivated = false;
                    }
                }
            }
        } while (!allActivated);
    }

    @Override
    public void getInputForForwardSearch(List<Tuple> nnOutputs) {
        for (Tuple tp : nnOutputs) {
            for (KnowledgeNode kn : mapKN.values()) {
                if (kn.inputTag.type.equals(Tag.TagType.FACT)) {
                    if (((Fact) kn.inputTag).getPredicateName().equals(tp.s)) {
                        excite(kn, tp.value);
                    }
                } else if (kn.inputTag.type.equals(Tag.TagType.RECOMMENDATION)) {
                    if (((Recommendation) kn.inputTag).getPredicateName().equals(tp.s)) {
                        excite(kn, tp.value);
                    }
                } else if (kn.inputTag.type.equals(Tag.TagType.RULE)) {
                    if (kn.inputTag.toString().equals(tp.s)) {
                        excite(kn, tp.value);
                    }
                }
            }
        }
    }

    @Override
    public void excite(KnowledgeNode kn, int value) {
        kn.increaseActivation(value);
        if (kn.activation * kn.strength >= kn.threshold) {
            Tag ownTag = kn.inputTag;
            if (value != 0) {
                kn.listOfRelatedTruths.put(ownTag, kn.accuracy[value]);
                kn.updateBelief();
                activeTags.add(ownTag);
            }
            kn.isActivated = true;
            fire(kn);
            kn.isFired = true;
            for (Tag t : kn.outputs.keySet()) {
                updateConfidence(mapKN.get(t));
            }
        }
    }

    @Override
    public void fire(KnowledgeNode kn) {
        for (Tag t : kn.outputs.keySet()) {
            KnowledgeNode currentKN = mapKN.get(t);
            currentKN.activation += 100;
            if (currentKN.activation >= currentKN.threshold) {
                Tag parentTag = kn.inputTag;
                currentKN.listOfRelatedTruths.put(parentTag, (kn.belief * kn.outputs.get(t)) / 100);
                currentKN.updateBelief();
                currentKN.isActivated = true;
                activeTags.add(t);
            }
        }
        kn.age = 0;
    }

    @Override
    public void updateConfidence(KnowledgeNode kn) {
        for (Tag t : kn.outputs.keySet()) {
            KnowledgeNode currentKN = mapKN.get(t);
            if (currentKN.isActivated) {
                Tag parentTag = kn.inputTag;
                currentKN.listOfRelatedTruths.put(parentTag, (kn.belief * kn.outputs.get(t)) / 100);
                currentKN.updateBelief();
                activeTags.add(t);
                updateConfidence(currentKN);
            }
        }
    }

    /**
     * @param parentToItem     path from common parent to the item
     * @param prentToSartPoint path from common to stratPoint
     */
    private void objectTruthEstimate(List<Tag> parentToItem, List<Tag> prentToSartPoint) {
        Collections.reverse(prentToSartPoint); //prentToSartPoint become start point to parent because the first KN that has an belief is the startPoint and it is calculate from the Neural Network
        backwardConfidence(prentToSartPoint);
        forwardConfidence(parentToItem);
    }

    /**
     * @param bestPath           previous found best path
     * @param bestObjectTruth    previous best found best confidence
     * @param item               the item tag wanted to search
     * @param parentToItem       the
     * @param parentToStartPoint
     * @return the actual maximum belief found
     */
    private double getBestObjectTruth(Map<Tag, Double> bestPath, double bestObjectTruth, Tag item,
                                      List<Tag> parentToItem, List<Tag> parentToStartPoint) {
        if (mapKN.get(item).belief > bestObjectTruth) {
            bestPath.clear();
            for (Tag tag : parentToStartPoint) {
                bestPath.put(tag, mapKN.get(tag).belief);
            }
            for (Tag tag : parentToItem) {
                bestPath.put(tag, mapKN.get(tag).belief);
            }
            bestObjectTruth = mapKN.get(item).belief;
        }
        return bestObjectTruth;
    }

    /**
     * @param start the tag that start the path which the belief should not be reset
     * @param path  path that contains tags needed to be reset their belief value
     */
    private void resetAllObjectTruth(Tag start, Map<Tag, Double> path) {
        for (Tag tag : path.keySet()) {
            if (!tag.equals(start)) {
                mapKN.get(tag).belief = 0;
            }
        }
    }

    /**
     * Calculate the confidence of each KN starting from its parental KN (one of the KN in its output list) in a path.
     *
     * @param path a path of KN with descending order, parental to child KN
     */
    private void forwardConfidence(List<Tag> path) {
        double totalConfidence = 100;
        if (path.size() != 1) {
            List<Double> listOfObjectTruth = new ArrayList<>();
            for (int i = 0; i < path.size() - 1; i++) {
                Tag current = path.get(i);
                Tag next = path.get(i + 1);
                listOfObjectTruth.add(mapKN.get(current).belief);
                mapKN.get(next).belief = mapKN.get(current).belief * mapKN.get(current).outputs.get(next) / 100;
            }
            listOfObjectTruth.add(mapKN.get(path.get(path.size() - 1)).belief);

            for (double objectTruth : listOfObjectTruth) {
                totalConfidence = (totalConfidence * objectTruth) / 100;
            }
        }
    }

    /**
     * Calculate the confidence of each KN starting from its child KN (one of the KN in its output list) in a path.
     *
     * @param path a path of KN with ascending order, child to parental KN
     *             Note the calculation does not follow a mathematic logic, and this need to be modify in the future
     */
    private void backwardConfidence(List<Tag> path) {
        double totalConfidence = 100;
        if (path.size() != 1) {
            List<Double> listOfObjectTruth = new ArrayList<>();
            for (int i = 0; i < path.size() - 1; i++) {
                Tag current = path.get(i);
                Tag next = path.get(i + 1);
                listOfObjectTruth.add(mapKN.get(current).belief);
                mapKN.get(next).listOfRelatedTruths.put(current, (mapKN.get(current).belief * mapKN.get(next).outputs.get(current) / 100));
                mapKN.get(next).updateBelief();
            }
            listOfObjectTruth.add(mapKN.get(path.get(path.size() - 1)).belief);

            for (double objectTruth : listOfObjectTruth) {
                totalConfidence = (totalConfidence * objectTruth) / 100;
            }
        }

    }

    /**
     * Find a path between two given tag and all the tag involved in that path
     *
     * @param start         the given tag to start the path
     * @param end           the given tag to finish the path
     * @param badComponents tags that can have a down stream path to both the start and end point
     * @return an ArrayList that stores all the tags of the found path. The first slot of the list is the starting tag and the last slot is the ending tag
     * Note this method can only go descendant order
     */
    private List<Tag> pathFinder(Tag start, Tag end, List<Tag> badComponents) {
        List<Tag> currentPath = new ArrayList<>();
        currentPath.add(start);
        if (mapKN.get(start).outputs.containsKey(end) && !badComponents.contains(start)) {
            currentPath.add(end);
            return currentPath;
        } else {
            for (Tag t : mapKN.get(start).outputs.keySet()) {
                List<Tag> template = pathFinder(t, end, badComponents);
                if (template.get(template.size() - 1).equals(end) && !badComponents.contains(start)) {
                    currentPath.addAll(template);
                    break;
                }
            }
        }
        return currentPath;
    }

    /**
     * @param item       the item tag wanted to search for
     * @param startPoint the starting tag to go
     * @return a list of common parent that has a path to both item and start point
     */
    private List<Tag> findCommonParents(Tag item, Tag startPoint) {
        Set<Tag> allParentsofStartPoint = findAllParentOfGivenTag(startPoint);

        List<Tag> commonParents = new ArrayList<>();
        for (Tag parent : allParentsofStartPoint) {
            List<Tag> belowTags = new ArrayList<>();
            depthFirstSearch(parent, belowTags);
            for (Tag tg : belowTags) {
                if (tg.equals(item)) {
                    commonParents.add(parent);
                    break;
                }
            }
        }
        return commonParents;
    }

    /**
     * @param t the wanted tag to find its parents
     * @return a HashSet of tags that has a path to the wanted tag t
     */
    private Set<Tag> findAllParentOfGivenTag(Tag t) {
        Set<Tag> allParents = new HashSet<>();
        allParents.add(t);
        boolean added;
        do {
            added = false;
            for (KnowledgeNode kn : mapKN.values()) {
                for (Tag tg : kn.outputs.keySet()) {
                    if (allParents.contains(tg)) {
                        Tag knType = kn.inputTag;
                        if (!allParents.contains(knType)) {
                            allParents.add(knType);
                            added = true;
                        }
                    }
                }
            }
        } while (added);

        return allParents;
    }


    /**
     * Depth first search (DFS) on a specific tag
     *
     * @param tag       the tag to start the DFS
     * @param tagsFound to store all the tag found during DFS
     */
    private void depthFirstSearch(Tag tag, List<Tag> tagsFound) {
        tagsFound.add(tag);
        for (Tag t : mapKN.get(tag).outputs.keySet()) {
            if (!tagsFound.contains(t)) {
                depthFirstSearch(t, tagsFound);
            }
        }
    }
}
