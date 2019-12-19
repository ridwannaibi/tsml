package machine_learning.classifiers.tuned.incremental;

import com.google.common.primitives.Doubles;
import tsml.classifiers.EnhancedAbstractClassifier;
import tsml.classifiers.ProgressiveBuildClassifier;
import utilities.ArrayUtilities;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class IncTunedClassifier extends EnhancedAbstractClassifier implements ProgressiveBuildClassifier {

    private BenchmarkIterator benchmarkIterator = new BenchmarkIterator() {
        @Override
        public boolean hasNext() {
            return false;
        }
    };
    private List<Benchmark> collectedBenchmarks = new ArrayList<>();
    private BenchmarkCollector benchmarkCollector = new BestBenchmarkCollector(benchmark -> benchmark.getResults().getAcc());
    private BenchmarkEnsembler benchmarkEnsembler = BenchmarkEnsembler.byScore(benchmark -> benchmark.getResults().getAcc());
    private List<Double> ensembleWeights = new ArrayList<>();
    private Consumer<Instances> onDataFunction = instances -> {

    };

    @Override public void startBuild(final Instances data) throws Exception {
        onDataFunction.accept(data);
    }

    @Override
    public boolean hasNextBuildTick() throws Exception {
        return benchmarkIterator.hasNext();
    }

    @Override
    public void nextBuildTick() throws Exception {
        Set<Benchmark> nextBenchmarks = benchmarkIterator.next();
        collectedBenchmarks.removeAll(nextBenchmarks);
        collectedBenchmarks.addAll(nextBenchmarks);
    }

    @Override
    public void finishBuild() throws Exception {
        collectedBenchmarks = benchmarkCollector.getCollectedBenchmarks();
        if(collectedBenchmarks.isEmpty()) {
            throw new IllegalStateException("no benchmarks");
        }
        ensembleWeights = benchmarkEnsembler.weightVotes(collectedBenchmarks);
    }

    public BenchmarkIterator getBenchmarkIterator() {
        return benchmarkIterator;
    }

    public void setBenchmarkIterator(BenchmarkIterator benchmarkIterator) {
        this.benchmarkIterator = benchmarkIterator;
    }

    public List<Benchmark> getCollectedBenchmarks() {
        return collectedBenchmarks;
    }

    public BenchmarkCollector getBenchmarkCollector() {
        return benchmarkCollector;
    }

    public void setBenchmarkCollector(BenchmarkCollector benchmarkCollector) {
        this.benchmarkCollector = benchmarkCollector;
    }

    public BenchmarkEnsembler getBenchmarkEnsembler() {
        return benchmarkEnsembler;
    }

    public void setBenchmarkEnsembler(BenchmarkEnsembler benchmarkEnsembler) {
        this.benchmarkEnsembler = benchmarkEnsembler;
    }

    public List<Double> getEnsembleWeights() {
        return ensembleWeights;
    }

    @Override
    public double[] distributionForInstance(Instance testCase) throws Exception {
        double[] distribution = new double[numClasses];
        for(int i = 0; i < collectedBenchmarks.size(); i++) {
            Benchmark benchmark = collectedBenchmarks.get(i);
            double[] constituentDistribution = benchmark.getClassifier().distributionForInstance(testCase);
            ArrayUtilities.normaliseInPlace(constituentDistribution);
            ArrayUtilities.multiplyInPlace(constituentDistribution, ensembleWeights.get(i));
            ArrayUtilities.addInPlace(distribution, constituentDistribution);
        }
        ArrayUtilities.normaliseInPlace(distribution);
        return distribution;
    }

    @Override
    public double classifyInstance(Instance testCase) throws Exception {
        return ArrayUtilities.bestIndex(Doubles.asList(distributionForInstance(testCase)), rand);
    }

    public Consumer<Instances> getOnDataFunction() {
        return onDataFunction;
    }

    public void setOnDataFunction(final Consumer<Instances> onDataFunction) {
        this.onDataFunction = onDataFunction;
    }

    // todo param handler + put lambdas / anon classes in full class for str representation in get/setoptions
}