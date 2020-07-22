package tsml.data_containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Data structure able to store a time series instance. it can be standard
 * (univariate, no missing, equally sampled series) or complex (multivariate,
 * unequal length, unequally spaced, univariate or multivariate time series).
 *
 * Should Instances be immutable after creation? Meta data is calculated on
 * creation, mutability can break this
 */

public class TimeSeriesInstance implements Iterable<TimeSeries> {

    /* Meta Information */
    boolean isMultivariate;
    boolean isEquallySpaced;
    boolean hasMissing;

    int minLength;
    int maxLength;

    /* End Meta Information */


    /* Data */
    List<TimeSeries> series_channels;
    int classLabelIndex;

    // this ctor can be made way more sophisticated.
    public TimeSeriesInstance(List<List<Double>> series, Double label) {
        this(series);

        classLabelIndex = label.intValue();
    }

    // this ctor can be made way more sophisticated.
    public TimeSeriesInstance(List<List<Double>> series, int label) {
        this(series);

        classLabelIndex = label;
    }

    public TimeSeriesInstance(List<List<Double>> series) {
        // process the input list to produce TimeSeries Objects.
        // this allows us to pad if need be, or if we want to squarify the data etc.
        series_channels = new ArrayList<TimeSeries>();

        for (List<Double> channel : series) {
            // convert List<Double> to double[]
            series_channels.add(new TimeSeries(channel.stream().mapToDouble(Double::doubleValue).toArray()));
        }

        isMultivariate = series_channels.size() > 1;

        calculateLengthBounds();
        calculateIfMissing();
    }

    public TimeSeriesInstance(double[][] data, int labelIndex) {
        series_channels = new ArrayList<TimeSeries>();

        for(double[] in : data){
            series_channels.add(new TimeSeries(in));
        }

        classLabelIndex = labelIndex;

        isMultivariate = series_channels.size() > 1;

        calculateLengthBounds();
        calculateIfMissing();
	}

	private void calculateLengthBounds() {
        minLength = series_channels.stream().mapToInt(e -> e.getSeries().length).min().getAsInt();
        maxLength = series_channels.stream().mapToInt(e -> e.getSeries().length).max().getAsInt();
    }

    private void calculateIfMissing() {
        // if any of the series have a NaN value, across all dimensions then this is
        // true.
        hasMissing = series_channels.stream().map(e -> Arrays.stream(e.getSeries()).anyMatch(Double::isNaN))
                .anyMatch(Boolean::booleanValue);
    };

    public int getNumChannels() {
        return series_channels.size();
    }

    public int getLabelIndex(){
        return classLabelIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Num Channels: ").append(getNumChannels()).append(" Class Label Index: ").append(classLabelIndex);
        for (TimeSeries channel : series_channels) {
            sb.append(System.lineSeparator());
            sb.append(channel.toString());
        }

        return sb.toString();
    }

    @Override
    public Iterator<TimeSeries> iterator() {
        return series_channels.iterator();
    }


    public double[][] toValueArray(){
        double[][] output = new double[this.series_channels.size()][];
        for (int i=0; i<output.length; ++i){
             //clone the data so the underlying representation can't be modified
            output[i] = series_channels.get(i).getSeries().clone();
        }
        return output;
    }


}