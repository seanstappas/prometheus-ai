package knn.internal;

import com.google.inject.assistedinject.Assisted;
import knn.api.KnowledgeNode;
import knn.api.KnowledgeNodeNetwork;
import knn.api.Tuple;
import tags.Fact;
import tags.Recommendation;
import tags.Rule;
import tags.Tag;

import javax.inject.Inject;
import java.util.*;

class KnowledgeNodeNetworkImpl implements KnowledgeNodeNetwork {
    private Map<Tag, KnowledgeNode> mapKN;
    private Map<Tag, Double> inputTags;
    private Map<Tag, Double> activeTags;
    // TODO: Remove Maps here, associate Object truth (belief...) with KN object
    // TODO: Rename Object truth to belief

    @Inject
    public KnowledgeNodeNetworkImpl(
            @Assisted("mapKN") Map<Tag, KnowledgeNode> mapKN,
            @Assisted("inputTags") Map<Tag, Double> inputTags,
            @Assisted("activeTags") Map<Tag, Double> activeTags) {
        this.mapKN = mapKN;
        this.inputTags = inputTags;
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
        inputTags.clear();
    }

    @Override
    public void addKN(KnowledgeNode kn) {
        if (kn.type.equals(KnowledgeNode.InputType.FACT)) {
            mapKN.put(kn.fact, kn);
        } else if (kn.type.equals(KnowledgeNode.InputType.RECOMMENDATION)) {
            mapKN.put(kn.recommendation, kn);
        } else if (kn.type.equals(KnowledgeNode.InputType.RULE)) {
            mapKN.put(kn.rule, kn);
        }
    }

    @Override
    public void delKN(Tag tag) {
        mapKN.remove(tag);
    }

    @Override
    public void addFiredTag(Tag tag, double objectTruth) {
        activeTags.put(tag, objectTruth);
    }

    @Override
    public Map<Tag, Double> getInputTags() {
        return inputTags;
    }

    @Override
    public Map<Tag, Double> getActiveTags() {
        return activeTags;
    }

    @Override
    public void lambdaSearch(ArrayList<Tuple> nnOutputs, Tag item) {
        HashMap<Tag, Double> bestPath = new HashMap<>();
        double bestObjectTruth = 0;

        //Convert information from the NN to tags
        getInputForBackwardSearch(nnOutputs);

        for (Tag startPoint : activeTags.keySet()) {
            ArrayList<Tag> commonParents = findCommonParents(item, startPoint);

            //For each parent of the startPoint, find if there is a non cycle path that connect both the startPoint and the Item tag
            for (Tag parentOfStartPoint : commonParents) {
                ArrayList<Tag> descendants = new ArrayList<>();
                depthFirstSearch(parentOfStartPoint, descendants);
                for (Tag tag : descendants) {
                    if (tag.equals(item)) {
                        resetAllObjectTruth(startPoint, bestPath); //ObjectTruth of Tag t should not be reset because it is the starting point to calculate a new path
                        ArrayList<Tag> bads = new ArrayList<>();
                        ArrayList<Tag> pathParentToItem = pathFinder(parentOfStartPoint, tag, bads); //path from the common parent the item
                        ArrayList<Tag> pathParentToStartPoint; //path from the common parent to the startPoint
                        ArrayList<Tag> prevPathParentToStartPoint;
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
        activeTags.putAll(bestPath);
    }

    @Override
    public void backwardSearch(ArrayList<Tuple> nnOutputs, double score) {
        getInputForBackwardSearch(nnOutputs);
        HashMap<Tag, Double> pendingFacts = new HashMap<>();
        do {
            backwardSearchCycle(score, pendingFacts);
        } while (!pendingFacts.isEmpty());
    }

    @Override
    public void backwardSearch(ArrayList<Tuple> nnOutputs, double score, int ply) {
        getInputForBackwardSearch(nnOutputs);
        HashMap<Tag, Double> pendingFacts = new HashMap<>();
        for (int i = 0; i < ply; i++) {
            backwardSearchCycle(score, pendingFacts);
        }

    }

    private void backwardSearchCycle(double score, HashMap<Tag, Double> pendingFacts) {
        pendingFacts.clear();
        Set<Tag> prevActiveTags = new HashSet<>(activeTags.keySet());
        for (KnowledgeNode kn : mapKN.values()) {
            int matching = 0;
            for (Tag t : kn.outputs.keySet()) {
                if (prevActiveTags.contains(t)) {
                    matching++;
                    double backwardConfidence = (kn.outputs.get(t) * mapKN.get(t).belief) / 100;
                    kn.listOfRelatedTruth.put(t, backwardConfidence);
                }
            }
            if ((double) matching / kn.outputs.size() >= score) {
                Tag knType = kn.typeChecker();
                kn.updateBelief();
                if (!activeTags.containsKey(knType)) {
                    pendingFacts.put(knType, kn.belief);
                }
            }
        }
        activeTags.putAll(pendingFacts);
    }

    @Override
    public void getInputForBackwardSearch(ArrayList<Tuple> nnOutputs) {
        for (Tuple tp : nnOutputs) {
            boolean found = false;
            for (KnowledgeNode kn : mapKN.values()) {
                if (kn.type.equals(KnowledgeNode.InputType.FACT)) {
                    if (kn.fact.getPredicateName().equals(tp.s)) {
                        kn.listOfRelatedTruth.put(kn.fact, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        inputTags.put(kn.fact, kn.belief);
                        activeTags.put(kn.fact, kn.belief);
                        found = true;
                    }
                } else if (kn.type.equals(KnowledgeNode.InputType.RECOMMENDATION)) {
                    if (kn.recommendation.getPredicateName().equals(tp.s)) {
                        kn.listOfRelatedTruth.put(kn.recommendation, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        inputTags.put(kn.recommendation, kn.belief);
                        activeTags.put(kn.recommendation, kn.belief);
                        found = true;
                    }
                } else if (kn.type.equals(KnowledgeNode.InputType.RULE)) {
                    if (kn.rule.toString().equals(tp.s)) {
                        kn.listOfRelatedTruth.put(kn.rule, kn.accuracy[tp.value]);
                        kn.updateBelief();
                        inputTags.put(kn.rule, kn.belief);
                        activeTags.put(kn.rule, kn.belief);
                        found = true;
                    }
                }
            }
            if (!found) {
                createKNfromTuple(tp);
            }
        }
    }

    @Override
    public void forwardSearch(ArrayList<Tuple> nnOutputs, int ply) {
        getInputForForwardSearch(nnOutputs);

        if (ply > 0 && !activeTags.isEmpty()) {
            for (int i = 0; i < ply; i++) {
                ArrayList<Tag> activeList = new ArrayList<>();
                activeList.addAll(activeTags.keySet());

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
    public void forwardSearch(ArrayList<Tuple> nnOutputs) {
        getInputForForwardSearch(nnOutputs);

        boolean allActivated;
        do {
            allActivated = true;
            ArrayList<Tag> activeList = new ArrayList<>();

            activeList.addAll(activeTags.keySet());

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
    public void getInputForForwardSearch(ArrayList<Tuple> nnOutputs) {
        for (Tuple tp : nnOutputs) {
            boolean found = false;
            for (KnowledgeNode kn : mapKN.values()) {
                if (kn.type.equals(KnowledgeNode.InputType.FACT)) {
                    if (kn.fact.getPredicateName().equals(tp.s)) {
                        excite(kn, tp.value);
                        inputTags.put(kn.fact, kn.belief);
                        found = true;
                    }
                } else if (kn.type.equals(KnowledgeNode.InputType.RECOMMENDATION)) {
                    if (kn.recommendation.getPredicateName().equals(tp.s)) {
                        excite(kn, tp.value);
                        inputTags.put(kn.recommendation, kn.belief);
                        found = true;
                    }
                } else if (kn.type.equals(KnowledgeNode.InputType.RULE)) {
                    if (kn.rule.toString().equals(tp.s)) {
                        excite(kn, tp.value);
                        inputTags.put(kn.rule, kn.belief);
                        found = true;
                    }
                }
            }
            if (!found) {
                createKNfromTuple(tp);
            }
        }
    }

    @Override
    public void createKNfromTuple(Tuple tp) {
        if (tp.s.charAt(0) == '@') {
            Recommendation rc = new Recommendation(tp.s);
            inputTags.put(rc, 0.0);
        } else if (tp.s.contains("->")) {
            Rule r = new Rule(tp.s);
            inputTags.put(r, 0.0);
        } else if (tp.s.matches(".*\\(.*\\).*")) {
            Fact f = new Fact(tp.s);
            inputTags.put(f, 0.0);
        } else {
            String str = tp.s + "()";
            Fact f = new Fact(str);
            inputTags.put(f, 0.0);
        }
    }

    @Override
    public void excite(KnowledgeNode kn, int value) {
        kn.increaseActivation(value);
        if (kn.activation * kn.strength >= kn.threshold) {
            Tag ownTag = kn.typeChecker();
            if (value != 0) {
                kn.listOfRelatedTruth.put(ownTag, kn.accuracy[value]);
                kn.updateBelief();
                activeTags.put(ownTag, kn.belief);
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
                Tag parentTag = kn.typeChecker();
                currentKN.listOfRelatedTruth.put(parentTag, (kn.belief * kn.outputs.get(t)) / 100);
                currentKN.updateBelief();
                currentKN.isActivated = true;
                activeTags.put(t, currentKN.belief);
            }
        }
    }

    @Override
    public void updateConfidence(KnowledgeNode kn) {
        for (Tag t : kn.outputs.keySet()) {
            KnowledgeNode currentKN = mapKN.get(t);
            if (currentKN.isActivated) {
                Tag parentTag = kn.typeChecker();
                currentKN.listOfRelatedTruth.put(parentTag, (kn.belief * kn.outputs.get(t)) / 100);
                currentKN.updateBelief();
                activeTags.put(t, currentKN.belief);
                updateConfidence(currentKN);
            }
        }
    }

    /**
     * @param parentToItem     path from common parent to the item
     * @param prentToSartPoint path from common to stratPoint
     */
    private void objectTruthEstimate(ArrayList<Tag> parentToItem, ArrayList<Tag> prentToSartPoint) {
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
    private double getBestObjectTruth(HashMap<Tag, Double> bestPath, double bestObjectTruth, Tag item, ArrayList<Tag> parentToItem, ArrayList<Tag> parentToStartPoint) {
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
    private void resetAllObjectTruth(Tag start, HashMap<Tag, Double> path) {
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
    private void forwardConfidence(ArrayList<Tag> path) {
        double totalConfidence = 100;
        if (path.size() != 1) {
            ArrayList<Double> listOfObjectTruth = new ArrayList<>();
            for (int i = 0; i < path.size() - 1; i++) {
                Tag current = path.get(i);
                Tag next = path.get(i + 1);
                listOfObjectTruth.add(mapKN.get(current).belief);
                mapKN.get(next).belief = mapKN.get(current).belief * mapKN.get(current).outputs.get(next) / 100;
            }
            listOfObjectTruth.add(mapKN.get(path.get(path.size() - 1)).belief);

            for (Double objectTruth : listOfObjectTruth) {
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
    private void backwardConfidence(ArrayList<Tag> path) {
        double totalConfidence = 100;
        if (path.size() != 1) {
            ArrayList<Double> listOfObjectTruth = new ArrayList<>();
            for (int i = 0; i < path.size() - 1; i++) {
                Tag current = path.get(i);
                Tag next = path.get(i + 1);
                listOfObjectTruth.add(mapKN.get(current).belief);
                mapKN.get(next).listOfRelatedTruth.put(current, (mapKN.get(current).belief * mapKN.get(next).outputs.get(current) / 100));
                mapKN.get(next).updateBelief();
            }
            listOfObjectTruth.add(mapKN.get(path.get(path.size() - 1)).belief);

            for (Double objectTruth : listOfObjectTruth) {
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
    private ArrayList<Tag> pathFinder(Tag start, Tag end, ArrayList<Tag> badComponents) {
        ArrayList<Tag> currentPath = new ArrayList<>();
        currentPath.add(start);
        if (mapKN.get(start).outputs.containsKey(end) && !badComponents.contains(start)) {
            currentPath.add(end);
            return currentPath;
        } else {
            for (Tag t : mapKN.get(start).outputs.keySet()) {
                ArrayList<Tag> template = pathFinder(t, end, badComponents);
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
    private ArrayList<Tag> findCommonParents(Tag item, Tag startPoint) {
        HashSet<Tag> allParentsofStartPoint = findAllParentOfGivenTag(startPoint);

        ArrayList<Tag> commonParents = new ArrayList<>();
        for (Tag parent : allParentsofStartPoint) {
            ArrayList<Tag> belowTags = new ArrayList<>();
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
    private HashSet<Tag> findAllParentOfGivenTag(Tag t) {
        HashSet<Tag> allParents = new HashSet<>();
        allParents.add(t);
        boolean added;
        do {
            added = false;
            for (KnowledgeNode kn : mapKN.values()) {
                for (Tag tg : kn.outputs.keySet()) {
                    if (allParents.contains(tg)) {
                        Tag knType = kn.typeChecker();
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
    private void depthFirstSearch(Tag tag, ArrayList<Tag> tagsFound) {
        tagsFound.add(tag);
        for (Tag t : mapKN.get(tag).outputs.keySet()) {
            if (!tagsFound.contains(t)) {
                depthFirstSearch(t, tagsFound);
            }
        }
    }
}
