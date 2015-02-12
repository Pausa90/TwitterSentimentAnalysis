package it.iaproject.iuland;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tweet {

	/*
	 * Elimino il rumore, ossia trascuro i campi come: 
	 * -id (secondo campo)
	 * -data-ora (irrilevante e simile (intervallo di due giorni)
	 * -NO-QUERY 
	 * -utente: l'utente non determina la positività del tweet, ma lo fa il testo
	 */
	public final int positivePrediction = 4;

	private int positive;
	private String user;
	private List<Word> words;
	private final String libraryModelPath = "models/english-left3words-distsim.tagger";
	private String postaggerText;
	private String rawText;

	private int prediction = -1; //non inizializzato, 0 negativo, 1 positivo
	private Map<String,Integer> tweetMap;

	private List<String> positiveEmotion;
	private List<String> negativeEmotion;
	private int positiveEmotionNumber;
	private int negativeEmotionNumber;
	

	public Tweet(String rawText, int positive, String user, List<String> positiveEmotionsSet, List<String> negativeEmotionsSet){
		this.positive = positive;
		this.positiveEmotionNumber = 0;
		this.negativeEmotionNumber = 0;
		this.user = user;
		this.rawText = rawText;
		this.positiveEmotion = positiveEmotionsSet;
		this.negativeEmotion = negativeEmotionsSet;
	}
	
	public Tweet(String rawText, String postagger,int positive, String user, List<String> positiveEmotionsSet, List<String> negativeEmotionsSet){
		this.positive = positive;
		this.positiveEmotionNumber = 0;
		this.negativeEmotionNumber = 0;
		this.user = user;
		this.postaggerText = postagger;
		this.rawText = rawText;
		this.positiveEmotion = positiveEmotionsSet;
		this.negativeEmotion = negativeEmotionsSet;
	}

	public String getPostaggerText(){
		return this.postaggerText;
	}
	
	public String getRawText(){
		return this.rawText;
	}

	public void elaborateTweet(){
		this.tweetMap = new HashMap<String,Integer>();
		this.setWordsFromRawText();
		this.setEmotionNumbers();
		this.classifyTweet();
	}

	private void setEmotionNumbers() {
		for (String pemotion : this.positiveEmotion )
			if (rawText.contains(pemotion))
				this.positiveEmotionNumber++;

		for (String nemotion : this.negativeEmotion )
			if (rawText.contains(nemotion))
				this.negativeEmotionNumber++;

	}

	private void setWordsFromRawText() {
		this.words = new LinkedList<Word>();

		for(String w : this.postaggerText.split("\\s+")){// \\s+: reg expr spazi bianchi
			this.words.add(createNewWords(w));
		}
	}

	//Splitto il testo dalla valutazione della libreria
	private Word createNewWords(String w) {
		String[] tokenized = w.split("_");
		String word = "";

		//Se il tweet contiene il carattere _ devo reinserirlo
		if (tokenized.length>2){
			word = tokenized[0];
			for (int i=1; i < tokenized.length-1; i++)
				word += "_" + tokenized[i];
		}
		else 
			word = tokenized[0];

		return new Word(word, tokenized[tokenized.length-1]);
	}

	private String startStanfordElaboration(String rawText) {		
		MaxentTagger tagger = new MaxentTagger(libraryModelPath);
		return tagger.tagString(rawText);
	}

	public void startStanfordElaboration(){
		this.postaggerText = startStanfordElaboration(this.rawText);
	}

	public int getPositive() {
		return positive;
	}

	public void setPositive(int positive) {
		this.positive = positive;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}

	public Map<String,Integer> getTweetMap(){
		return this.tweetMap;
	}

	public void setTweetMap(Map<String,Integer> map){
		this.tweetMap = map;
	}

	public String toString(){
		String output = "[" + this.user + ": ";

		try{
			for (Word s : words)
				output += s.getText()+" "+s.getPartOfSpeech()+",";
			
			output += " positive:" + this.positive +"]";

			output += "\n";
			output += "raw: [ ";
			for (String key : this.tweetMap.keySet())
				output += key + ": " + this.tweetMap.get(key) + " ";
			output += "]";
		} catch (Exception e){
			output += this.rawText;
			output += " positive:" + this.positive +"]";
		}
		return output;
	}

	public String getText(){
		String text = "";

		for (Word s : words)
			text += s.getText()+" ";

		return text;
	}

	public String getUser(){
		return this.user;
	}


	/** Classifico i tweet sulla base della loro similarità strutturale: numero di verbi, di nomi, di avverbi, ecc **/
	public void classifyTweet() {
		for (Word w : this.words){
			if (!this.tweetMap.containsKey(w.getPartOfSpeech()))
				this.tweetMap.put(w.getPartOfSpeech(), 1);
			else
				this.tweetMap.put(w.getPartOfSpeech(), this.tweetMap.get(w.getPartOfSpeech())+1);
		}
		return;
	}

	public int getPrediction() {
		return prediction;
	}

	public void setPrediction(int prediction) {
		this.prediction = prediction;
	}

	public boolean isPositive() {
		return this.positive==positivePrediction;
	}

	public int getPositiveEmotionNumber(){
		return this.positiveEmotionNumber;
	}

	public int getNegativeEmotionNumber(){
		return this.negativeEmotionNumber;
	}

}

