package it.iaproject.iuland;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Statistics {

	public final int initializedPrediction = -1;
	public final int positivePrediction = 4;
	public final int negativePrediction = 0;

	private List<Tweet> tweets;
	private Map<String,int[]> partOfSpeechPositiveInfluence; //key: partOfSpeach, value / value[i]=numero di tweet positivi con i partOfSpeach
	private Map<String, int[]> partOfSpeechNegativeInfluence;

	public Statistics(List<Tweet> tweets){
		this.tweets = tweets;
		this.partOfSpeechPositiveInfluence = new HashMap<String,int[]>();
		this.partOfSpeechNegativeInfluence = new HashMap<String,int[]>();
	}

	public String getStatistics(){

		String out = "";

		List<Tweet> fails = this.getFails();

		int[][] confusionMatrix = this.getConfusionMatrix();

		out += "ConfusionMatrix:\n";
		out += "\tTruthPositive\tTruthNegative\n"
				+ "SystemPositive\t" + confusionMatrix[0][0] + "\t" + confusionMatrix[0][1] + "\n"
				+ "SystemNegative\t" + confusionMatrix[1][0] + "\t" + confusionMatrix[1][1] + "\n\n";

		double errorRate = (confusionMatrix[0][1] + confusionMatrix[1][0]/(double)this.tweets.size());
		double accuracy = (confusionMatrix[0][0] + confusionMatrix[1][1])/(double)(confusionMatrix[0][0]+confusionMatrix[0][1]+confusionMatrix[0][0] + confusionMatrix[1][1]);
		double precision = confusionMatrix[0][0]/(double)(confusionMatrix[0][0]+confusionMatrix[0][1]);
		double recall = confusionMatrix[0][0]/(double)(confusionMatrix[0][0]+confusionMatrix[1][0]);
		double breackEven = (precision + recall)/2.;
		double f1Measure = 2*precision*recall / (double)(precision+recall);

		out += "error rate: " + errorRate + "\n";
		out += "accuracy: " + accuracy + "\n";
		out += "precision: " + precision + "\n";
		out += "recall: " + recall + "\n";
		out += "breack-even: " + breackEven + "\n";
		out += "f1-measure: " + f1Measure + "\n";

		out += "Fails Number: " + fails.size() + "\n";
		out += "Fails:\n";
		for (Tweet fail : fails)
			out += "positive: " + fail.isPositive() + ", prediction: " + fail.getPrediction() + ", text: " + fail.getUser() + ":" + fail.getText() + "\n";

		return out;
	}

	private int[][] getConfusionMatrix(){
		int[][] confusionMatrix = new int[2][2];
		for (Tweet tweet : this.tweets){
			if (tweet.isPositive() && tweet.getPrediction()==positivePrediction)
				confusionMatrix[0][0]++;
			else if ((!tweet.isPositive()) && tweet.getPrediction()==positivePrediction)
				confusionMatrix[0][1]++;
			else if (tweet.isPositive() && tweet.getPrediction()==negativePrediction)
				confusionMatrix[1][0]++;
			else if ((!tweet.isPositive()) && tweet.getPrediction()==negativePrediction)
				confusionMatrix[1][1]++;
		}
		return confusionMatrix;
	}

	private List<Tweet> getFails(){
		List<Tweet> fails = new LinkedList<Tweet>();

		for (Tweet tweet : this.tweets){
			if (tweet.isPositive() && tweet.getPrediction()!=positivePrediction)
				fails.add(tweet);
			else if ((!tweet.isPositive()) && tweet.getPrediction()==positivePrediction)
				fails.add(tweet);
		}
		return fails;		
	}


	/**
	 *  Estrapola le informazioni e le inserisce nella mappa
	 */
	private void initializePartOfSpeachInfluence(){
		boolean correct;
		for (Tweet tweet : this.tweets){
			correct = this.isCorrectPrediction(tweet);	
			if (correct)
				this.insertStatistics(tweet, this.partOfSpeechPositiveInfluence);
			else
				this.insertStatistics(tweet, this.partOfSpeechNegativeInfluence);
		}
	}


	private void insertStatistics(Tweet tweet, Map<String, int[]> map) {
		Map<String, Integer> tweetMap = tweet.getTweetMap();
		//Registro le parti del discorso presenti
		for (String key : tweetMap.keySet()){
			incrementValueInMap(tweetMap.get(key),key, map);  }
		//Registro le parti del discorso non presenti
		for (String key : getNotUsedPartOfSpeech(tweetMap.keySet()))
			incrementValueInMap(0, key, map);		
	}

	private List<String> getNotUsedPartOfSpeech(Set<String> set) {
		List<String> output = new LinkedList<String>(); 
		for (String partOfSpeech : this.getAllPartOfSpeech())
			if (!set.contains(partOfSpeech))
				output.add(partOfSpeech);
		return output;
	}

	private void incrementValueInMap(int value, String key, Map<String, int[]> map) {
		if (!map.containsKey(key)){
			int[] statistics;
			if (value>10)
				statistics = new int[value];
			else
				statistics = new int[10];
			statistics[value] = 1; 
			map.put(key, statistics);
		}
		else{
			int[] statistics = map.get(key);
			if (value>statistics.length-1)
				statistics = this.expandArray(statistics);
			statistics[value]++;
			map.put(key,statistics);
		}
	}

	private int[] expandArray(int[] array) {
		int[] newArray = new int[array.length+5];
		for (int i=0; i<array.length; i++)
			newArray[i] = array[i];
		return newArray;
	}

	private boolean isCorrectPrediction(Tweet tweet) {
		if ((tweet.isPositive() && tweet.getPrediction()==positivePrediction) ||
				((!tweet.isPositive()) && tweet.getPrediction()==negativePrediction))
			return true;
		return false;
	}

	private List<String> getAllPartOfSpeech() {
		List<String> partsOfSpeech = new LinkedList<String>();
		for (Tweet tweet : this.tweets)
			for (String part : tweet.getTweetMap().keySet())
				if (!partsOfSpeech.contains(part))
					partsOfSpeech.add(part);
		return partsOfSpeech;
	}

	/**
	 * Riporta le statistiche relative all'influenza sulla positività/negatività dei tweet
	 * di tutte le parti del discorso interne alle frasi. 
	 * La formattazione dell'output è: 
	 * 	parte del discorso: 
	 *    numero di occorrenze i: tweet positivi con i parti del discorso 
	 * @return array di stringhe/ a[0] = influenza su positivi, a[1] = influenza su negativi
	 */
	public String[] getPartOfSpeachInfluenceStatistics(){
		this.initializePartOfSpeachInfluence();
		return new String[] { getPOSInfluenceAux(this.partOfSpeechPositiveInfluence), getPOSInfluenceAux(this.partOfSpeechNegativeInfluence) };
	}

	private String getPOSInfluenceAux(Map<String,int[]> map){
		String outPos = "";
		for (String pos : map.keySet()){

			outPos += pos + ":\n";
			int[] stats = map.get(pos);
			for (int i=0; i<stats.length; i++)				
				outPos += "\t" + i + ": " + stats[i] + "\n";
			outPos += "\n";
		}
		return outPos;
	}

}
