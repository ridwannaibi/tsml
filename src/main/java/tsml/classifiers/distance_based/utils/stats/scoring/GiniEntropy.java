package tsml.classifiers.distance_based.utils.stats.scoring;

import weka.core.Instances;

import java.util.List;

public class GiniEntropy implements PartitionScorer {

    @Override
    public double findScore(final Instances parent, final List<Instances> children) {
        return ScoreUtils.giniScore(children);
    }
}
