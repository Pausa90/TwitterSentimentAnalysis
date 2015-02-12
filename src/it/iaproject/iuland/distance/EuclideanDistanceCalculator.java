package it.iaproject.iuland.distance;

import java.util.List;

import it.iaproject.iuland.Tweet;

public class EuclideanDistanceCalculator implements DistanceCalculator{

	@Override
	public double calculateDistance(Tweet tweet1, Tweet tweet2, List<String> allKeys) {
		double distance = 0;
		Integer value1;
		Integer value2;
		
		for (String key : allKeys){
			value1 = tweet1.getTweetMap().get(key);
			value2 = tweet2.getTweetMap().get(key);
			if (value1==null)
				value1 = 0;
			if (value2==null)
				value2 = 0;
			distance += Math.pow(value1-value2, 2);
		}		
		return Math.sqrt(distance);
	}

}
