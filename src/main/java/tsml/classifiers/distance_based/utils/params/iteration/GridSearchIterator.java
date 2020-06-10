package tsml.classifiers.distance_based.utils.params.iteration;

import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import tsml.classifiers.distance_based.utils.params.ParamSet;
import tsml.classifiers.distance_based.utils.params.ParamSpace;
import tsml.classifiers.distance_based.utils.params.ParamSpaceTest;
import tsml.classifiers.distance_based.utils.params.dimensions.IndexedParameterSpace;

/**
 * Purpose: // todo - docs - type the purpose of the code here
 * <p>
 * Contributors: goastler
 */
public class GridSearchIterator implements Iterator<ParamSet> {

    private IndexedParameterSpace indexedParameterSpace;
    private int iterationCount = 0;
    private int numIterations = -1;

    public GridSearchIterator(final ParamSpace paramSpace) {
        setParameterSpace(paramSpace);
    }

    public ParamSpace getParameterSpace() {
        return indexedParameterSpace.getParamSpace();
    }

    public IndexedParameterSpace getIndexedParameterSpace() {
        return indexedParameterSpace;
    }

    protected void setParameterSpace(
        final ParamSpace paramSpace) {
        indexedParameterSpace = new IndexedParameterSpace(paramSpace);
        numIterations = indexedParameterSpace.size();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{iterationCount=" + iterationCount + ", parameterSpace=" + indexedParameterSpace.getParamSpace().toString() + "}";
    }

    @Override
    public boolean hasNext() {
        return iterationCount < numIterations;
    }

    @Override
    public ParamSet next() {
        ParamSet paramSet = getIndexedParameterSpace().get(iterationCount);
        iterationCount++;
        return paramSet;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public void setNumIterations(final int numIterations) {
        Assert.assertTrue(numIterations >= 0);
        this.numIterations = numIterations;
    }
}
