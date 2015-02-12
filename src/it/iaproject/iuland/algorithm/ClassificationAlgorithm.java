package it.iaproject.iuland.algorithm;

import it.iaproject.iuland.Tweet;
import it.iaproject.iuland.distance.DistanceCalculator;

import java.util.List;

public abstract class ClassificationAlgorithm {
	public final int initializedPrediction = -1;
	public final int positivePrediction = 4;
	public final int negativePrediction = 0;
	public DistanceCalculator calculator;

	protected final double emotionPredictionFactor = 2.01;
	
	public ClassificationAlgorithm(DistanceCalculator method){
		this.calculator = method;
	}
	

	public int getInitializedPrediction() {
		return initializedPrediction;
	}

	public int getPositivePrediction() {
		return positivePrediction;
	}

	public int getNegativePrediction() {
		return negativePrediction;
	}
	
	public abstract int tweetPredict(List<Tweet> tweets, Tweet newTweet);
	
}
