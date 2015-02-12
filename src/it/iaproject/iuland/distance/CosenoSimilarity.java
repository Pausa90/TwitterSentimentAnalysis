package it.iaproject.iuland.distance;

import it.iaproject.iuland.Tweet;

import java.util.List;
import java.util.Map;

public class CosenoSimilarity implements DistanceCalculator {

	@Override
	public double calculateDistance(Tweet tweet1, Tweet tweet2, List<String> allKeys) {
		return 1-this.calculateSimilarity(tweet1, tweet2, allKeys);
	}
	
	public double calculateSimilarity(Tweet tweet1, Tweet tweet2, List<String> allKeys) {
		double similarity_numerator = 0;
		double similarity_denominator_p1 = 0;
		double similarity_denominator_p2 = 0;
		
		Integer value1;
		Integer value2;
		Map<String,Integer> tweetMap1 = tweet1.getTweetMap();
		Map<String,Integer> tweetMap2 = tweet2.getTweetMap();
		
		for (String key : allKeys){
			value1 = tweetMap1.get(key);
			value2 = tweetMap2.get(key);
			if (value1==null)
				value1 = 0;
			if (value2==null)
				value2 = 0;
			similarity_numerator += (value1*value2);
		}		
		
		for (String key : tweetMap1.keySet())
			similarity_denominator_p1 += Math.pow(tweetMap1.get(key), 2);

		for (String key : tweetMap2.keySet())
			similarity_denominator_p2 += Math.pow(tweetMap2.get(key), 2);
			
		
		return similarity_numerator/Math.sqrt(similarity_denominator_p1 * similarity_denominator_p2);
	}

}
