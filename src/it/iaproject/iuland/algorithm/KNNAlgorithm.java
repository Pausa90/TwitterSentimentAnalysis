package it.iaproject.iuland.algorithm;

import it.iaproject.iuland.Tweet;
import it.iaproject.iuland.distance.DistanceCalculator;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KNNAlgorithm extends ClassificationAlgorithm {
	
	/**
	 * I conti di KNN vengono realizzati tramite una matrice sparsa.
	 * Il suo output è una lista di oggetti, che vengono ordinati sulla base della distanza dal
	 * tweet i-esimo.
	 * Si considerano infine i primi n elementi, riportando il valore scelto.
	 */
	private int k;
	private List<CoupleTweetDistance> neighborsList;
	private List<String> allKeys; //Lista di chiavi delle mappe di entrambi i tweet
	
	public KNNAlgorithm(int k,DistanceCalculator method){
		super(method);
		this.k = k;
		this.neighborsList = new LinkedList<CoupleTweetDistance>();
		this.allKeys = new LinkedList<String>();
	}

	@Override
	public int tweetPredict(List<Tweet> tweets, Tweet newTweet) {
				
		for (Tweet t : tweets){
			
			this.setAllKeys(t, newTweet);
			double distance = this.calculateDistance(newTweet,t);
			this.neighborsList.add(new CoupleTweetDistance(distance, t));
			this.allKeys.clear();
		}
		

		int prediction = this.predict(newTweet);
		this.neighborsList.clear();
		
		return prediction;
	}

	private int predict(Tweet tweet) {
		Collections.sort(this.neighborsList, new MinDistance());
		
		double positive = 0;
		double negative = 0;
		Tweet tmpTweet;
		
		for (int i=0; i<this.k; i++){
			tmpTweet = this.neighborsList.get(i).getTweet();
			if (tmpTweet.isPositive())
				positive++;
			else
				negative++;
		}
		
		//Eventuali emotion positive e/o negative influiscono il giudizio
		int emotions = tweet.getPositiveEmotionNumber() - tweet.getNegativeEmotionNumber();
		if (emotions>0) //ho più emotion positive che negative
			positive += emotions * (this.emotionPredictionFactor);
		else if (emotions<0) //ho più emotion negative che positive
			negative += Math.abs(emotions) * (this.emotionPredictionFactor);
		
		
		if (positive<negative)
			return negativePrediction;
		else 
			return positivePrediction;
	}

	
	private double calculateDistance(Tweet tweet1, Tweet tweet2) {
		return this.calculator.calculateDistance(tweet1, tweet2, this.allKeys);
	}

	private void setAllKeys(Tweet t, Tweet newTweet) {
		for (String key : t.getTweetMap().keySet())
			this.allKeys.add(key);
		
		for (String key : newTweet.getTweetMap().keySet())
			if (!this.allKeys.contains(key))
				this.allKeys.add(key);		
	}

	
	public int getK() {
		return k;
	}
	

	public void setK(int k) {
		this.k = k;
	}

}

/**
 * Classe di supporto che memorizza la coppia (Tweet, distanza).
 * @author andrea
 *
 */
class CoupleTweetDistance {
	private double distance;
	private Tweet tweet;
	
	public CoupleTweetDistance(double distance, Tweet tweet){
		this.distance = distance;
		this.tweet = tweet;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public Tweet getTweet() {
		return tweet;
	}
	
	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}
	
	public String toString(){
		return "("+this.distance+","+this.tweet+")";
	}
	
}

/**
 * Metodo di comparazione basato sulla distanza tra due tweet
 * @author andrea
 *
 */
class MinDistance implements Comparator<CoupleTweetDistance>{

	public int compare(CoupleTweetDistance c1, CoupleTweetDistance c2) {
		return Double.compare(c1.getDistance(),c2.getDistance());
	}
	
}
