package es.internal;

import com.google.inject.assistedinject.Assisted;
import es.api.ExpertSystem;
import tags.*;

import javax.inject.Inject;
import java.util.*;

class ExpertSystemImpl implements ExpertSystem {
    private final Thinker thinker;
    private final Teacher teacher;
    private final Rester rester;
    private Set<Rule> readyRules;
    private Set<Rule> activeRules;
    private Set<Fact> facts;
    private Set<Recommendation> recommendations;

    @Inject
    public ExpertSystemImpl(
            @Assisted("readyRules") Set<Rule> readyRules,
            @Assisted("activeRules") Set<Rule> activeRules,
            @Assisted("facts") Set<Fact> facts,
            @Assisted("recommendations") Set<Recommendation> recommendations,
            ThinkerFactory thinkerFactory,
            TeacherFactory teacherFactory,
            ResterFactory resterFactory) {
        this.readyRules = readyRules;
        this.activeRules = activeRules;
        this.facts = facts;
        this.recommendations = recommendations;
        this.thinker = thinkerFactory.create(readyRules, activeRules, facts, recommendations);
        this.teacher = teacherFactory.create(readyRules);
        this.rester = resterFactory.create(readyRules);
    }

    @Override
    public Set<Recommendation> think() {
        return thinker.think(false, Integer.MAX_VALUE);
    }

    @Override
    public Set<Recommendation> think(boolean shouldGenerateRule) {
        return thinker.think(shouldGenerateRule, Integer.MAX_VALUE);
    }

    @Override
    public Set<Recommendation> think(boolean shouldGenerateRule, int numberOfCycles) {
        return thinker.think(shouldGenerateRule, numberOfCycles);
    }

    @Override
    public boolean teach(String sentence) {
        return teacher.teach(sentence);
    }

    @Override
    public boolean rest(int numberOfCycles) {
        return rester.rest(numberOfCycles);
    }

    @Override
    public void reset() {
        activeRules.clear();
        readyRules.clear();
        facts.clear();
        recommendations.clear();
    }

    @Override
    public void deactivateRules() {
        readyRules.addAll(activeRules);
        activeRules.clear();
    }

    @Override
    public void addTags(Set<Tag> tags) {
        for (Tag t : tags) {
            addTag(t);
        }
    }

    @Override
    public boolean addTag(Tag tag) {
        switch (tag.getType()) {
            case RULE:
                return addReadyRule((Rule) tag);
            case FACT:
                return addFact((Fact) tag);
            case RECOMMENDATION:
                return addRecommendation((Recommendation) tag);
        }
        return false;
    }

    @Override
    public boolean addReadyRule(Rule rule) {
        return readyRules.add(rule);
    }

    @Override
    public boolean removeReadyRule(Rule rule) {
        return readyRules.remove(rule);
    }

    @Override
    public boolean addFact(Fact fact) {
        return facts.add(fact);
    }

    @Override
    public boolean removeFact(Fact fact) {
        return facts.remove(fact);
    }

    @Override
    public boolean addRecommendation(Recommendation rec) {
        return recommendations.add(rec);
    }

    @Override
    public Set<Rule> getReadyRules() {
        return Collections.unmodifiableSet(readyRules);
    }

    @Override
    public Set<Rule> getActiveRules() {
        return Collections.unmodifiableSet(activeRules);
    }

    @Override
    public Set<Fact> getFacts() {
        return Collections.unmodifiableSet(facts);
    }

    @Override
    public Set<Recommendation> getRecommendations() {
        return Collections.unmodifiableSet(recommendations);
    }
}