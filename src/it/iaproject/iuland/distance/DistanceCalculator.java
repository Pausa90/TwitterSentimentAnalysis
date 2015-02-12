package it.iaproject.iuland.distance;

import java.util.List;

import it.iaproject.iuland.Tweet;

public interface DistanceCalculator {

	public double calculateDistance(Tweet tweet1, Tweet tweet2, List<String> allKeys);
	
}
