package it.iaproject.iuland;

import it.iaproject.iuland.algorithm.ClassificationAlgorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Classifier {
	
	/**
	 * Classe che modella l'oggetto software Classificatore.
	 * Fornisce metodi e strutture utili alla classificazione e predizione
	 */
	
	//Dichiaro le costanti utili per la classificazione
	//Se modificate, modificare anche in ClassificationAlgorithm e Statistics
	public final int initializedPrediction = -1;
	public final int positivePrediction = 4;
	public final int negativePrediction = 0;

	//Dichiaro le informazioni utili al recupero del file
	private final String trainingFilePath = "train.txt";
	private final String testFilePath = "test.txt";
	private File trainingFile;
	private File testingFile;
	//Inserite per ridurre, il collo di bottiglia apportato dal parsing della libreria postagger
	private final String trainingBackupPath = "trainingBackup"; //Memorizza le informazioni esclusivamente parsate con la libreria della stanford
	private File trainingBackupFile;
	private final String testingBackupPath = "testingBackup"; //Memorizza le informazioni esclusivamente parsate con la libreria della stanford
	private File testingBackupFile;
	
	private final String positiveEmotionsFilePath = "positive_words.txt";
	private final String negativeEmotionsFilePath = "negative_words.txt";
	private File positiveEmotionsFile;
	private File negativeEmotionsFile;
	
	/** Dichiaro le strutture di stupporto, quali:
	 * - contenitore dei tweet di training
	 * - contenitore dei tweet di testing
	 * - classe responsabile dell'estrapolazione delle statistiche finali 
	 */
	private List<Tweet> trainingTweets;
	private List<Tweet> testingTweets;
	private Statistics statistics;
	private PrinterAndSaver printerAndSaver;
	
	//Array contenente le stopwords da filtrare
	private final String[] stopWords = { ".", ":", "_", "?", "!", ",", "(", ")", "|", ";" }; //Mi limito alla punteggiatura comune, la lista puo' essere facilmente ampliata leggendo da file
	private List<String> positiveEmotionsSet;
	private List<String> negativeEmotionsSet;

	public Classifier(){
		this.trainingFile = this.newFileIstance(this.trainingFilePath);
		this.testingFile = this.newFileIstance(this.testFilePath);
		this.positiveEmotionsFile = this.newFileIstance(this.positiveEmotionsFilePath);
		this.negativeEmotionsFile = this.newFileIstance(this.negativeEmotionsFilePath);
		this.prepareEmotionsSets();
		this.printerAndSaver = new PrinterAndSaver();
	}

	/**
	 * Inizializza le due liste con le parole positive/negative contenute nei rispettivi file
	 */
	private void prepareEmotionsSets() {
		this.positiveEmotionsSet = this.extractLinesFromFile(this.positiveEmotionsFile);
		this.negativeEmotionsSet = this.extractLinesFromFile(this.negativeEmotionsFile);
	}
	
	/**
	 * Riporta una lista di linee lette
	 * @param file
	 * @return
	 */
	private List<String> extractLinesFromFile(File file){
		if (!file.isFile()){
			System.out.println("Training file does not exist");
			return null;
		}			
		else{
			List<String> lines = new LinkedList<String>();
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				while (line != null){
					if (!line.equals("")){
						lines.add(line);
					}
					line = reader.readLine();
				}
				reader.close();
				return lines;
			} catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}				
	}

	/**
	 * Estrapola i tweet da un file sorgente, memorizzandoli in una lista
	 * @param file: file sorgente
	 * @return lista di tweet
	 */
	private List<Tweet> getTweetFromFile(File file){
		if (!file.isFile()){
			System.out.println("Training file does not exist");
			return null;
		}			
		else{
			List<Tweet> tweets = new LinkedList<Tweet>();
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				int i = 1;
				String line = reader.readLine();
				while (line != null){
					if (!line.equals("")){
						Tweet tweet = this.extractTweet(line);
						tweets.add(tweet);
					}
						
					System.out.println("lette " + i + " righe");
					i++;	
					line = reader.readLine();
				}
				reader.close();
				return tweets;
			} catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}		
	}

	
	/**
	 * Avvia il training, occupandosi della lettura, memorizzazione ed elaborazione dei tweet.
	 * Al termine del metodo si può considerare terminata la fase di training.
	 */
	public void startTraining() {			
		this.trainingTweets = this.getTweetFromFile(trainingFile);
	}
	
	/**
	 * Avvia il training solo se non è stato già fatto precedentemente.
	 * In tal caso memorizza il suo output su file
	 */
	public void startSmartTraining() {		
		this.trainingBackupFile = this.newFileIstance(trainingBackupPath);
		if (this.trainingBackupFile.isFile())
			this.trainingTweets = this.resumeBackup(this.trainingBackupFile);
		else {
			this.trainingTweets = this.readAndExecPosTagger(this.trainingFile);
			this.createBackup(this.trainingTweets,this.trainingBackupPath);
		}
		this.elaborateTweets(this.trainingTweets);
	}
	
	/**
	 * Si occupa di recuperare le informazioni precedentemente memorizzate su file,
	 * diminuendo di molto il tempo di elaborazione
	 * @param file
	 * @return
	 */
	private List<Tweet> resumeBackup(File file) {
		if (!file.isFile()){
			try {
				throw new Exception(file + " file does not exist");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}			
		else{
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();
				List<Tweet> tweets = new LinkedList<Tweet>();
				while (line != null){
					if (!line.equals("")){
						Tweet tweet = this.extractTweetFromBackUp(line);
						tweets.add(tweet);
					}
					line = reader.readLine();
				}
				reader.close();
				return tweets;
			} catch (IOException e){
				e.printStackTrace();
			}
		}	
		return null;
	}
	
	
	private Tweet extractTweetFromBackUp(String line) {
		String[] splitted = line.split("\t");
		return new Tweet(splitted[3], splitted[2], Integer.parseInt(splitted[1]), splitted[0],this.positiveEmotionsSet, this.negativeEmotionsSet);
	}

	private void elaborateTweets(List<Tweet> tweets) {
		for (Tweet tweet : tweets)
			tweet.elaborateTweet();
	}

	
	private List<Tweet> readAndExecPosTagger(File file) {		
		if (!file.isFile()){
			try {
				throw new Exception(file + " file does not exist");
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}			
		else{
			List<Tweet> tweets = new LinkedList<Tweet>();
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				
				int i = 1;
				String line = reader.readLine();
				while (line != null){
					if (!line.equals("")){
						Tweet tweet = this.extractTweet(line);
						tweet.startStanfordElaboration();
						tweets.add(tweet);
					}
						
					System.out.println("lette " + i + " righe");
					i++;	
					line = reader.readLine();
				}
				reader.close();
				return tweets;
			} catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}	
	}

	private void createBackup(List<Tweet> tweets, String path) {
		String backUp = "";
		for (Tweet tweet : tweets){
			backUp += tweet.getUser() + "\t"; 
			if (tweet.isPositive())
				backUp += "4\t";
			else
				backUp += "0\t";
			backUp += tweet.getPostaggerText() + "\t";
			backUp += tweet.getRawText() + "\n";
		}
		this.printerAndSaver.save(path, backUp);
	}

	/**
	 * Crea un nuova istanza di file, occupandosi di capire il sistema operativo sottostante (Unix o Windows)
	 * @return
	 */
	public File newFileIstance(String abstractPath){
		final String completeTrainingBackupFilePath;
		if (this.isUnixFileSystem()){
			completeTrainingBackupFilePath = System.getProperty("user.dir") + "/" + abstractPath;
		}
		else{
			completeTrainingBackupFilePath = System.getProperty("user.dir") + "\\" + abstractPath;
		}
		return new File(completeTrainingBackupFilePath);
	}

	/**
	 * Metodo di supporto ad List<Tweet> getTweetFromFile(File file), il cui compito è l'estrazione di un tweet a partire da una linea del file sorgente (e quindi grezza)
	 * @param line
	 * @param negativeEmotionsSet2 
	 * @param positiveEmotionsSet 
	 * @return
	 */
	private Tweet extractTweet(String line) {
		Tweet tweet =  this.dataCleaning(line);
		return tweet;
	}

	/**
	 * Metodo che si occupa di pulire l'informazione grezza (che separa i campi da ;;).
	 * E' sua responsabilità effettuare la completa pulizia, riportando un oggetto Tweet pulito.
	 * @param line
	 * @param negativeEmotionsSet 
	 * @param positiveEmotionsSet 
	 * @return
	 */
	private Tweet dataCleaning(String line) {
		String[] splitted = line.split(";;"); 
		return new Tweet(this.cleanStopWords(splitted[splitted.length-1]), Integer.parseInt(splitted[0]),splitted[splitted.length-2], positiveEmotionsSet,negativeEmotionsSet);
	}
	
	/**
	 * Esegue la fase di eliminazione delle stopwords
	 * @param text
	 * @return
	 */
	private String cleanStopWords(String text) {
		String filtered = "";
		boolean isChar;
		for (int i=0; i<text.length(); i++){
						
			//vado avanti fino allo spazio e nel caso filtro dopo
			isChar = this.isStopWord(text.charAt(i));
			if ( isChar && i+1<text.length() && text.charAt(i+1) != ' '){
				do {
					filtered += text.charAt(i);
					i++;
				} while (i<text.length() && text.charAt(i) != ' ');
				filtered += " ";
				//i--;					
			}
			else if (!isChar)
				filtered += text.charAt(i);			
		}	
		return filtered;
	}
	
	private boolean isStopWord(char c){
		for (String stopWord : this.stopWords)
			if (stopWord.charAt(0) == c)
				return true;
		return false;
	}
	
	public List<Tweet> getTrainingTweets(){
		return this.trainingTweets;
	}

	/**
	 * Avvia la fase di test
	 * @param algorithm algoritmo di classificazione scelto
	 */
	public void startTesting(ClassificationAlgorithm algorithm){
		if (this.testingTweets==null)
			this.smartGetTweetFromFile();
		
		/*
		 * Eseguo la predizione su ogni tweet, memorizzando il valore predetto all'interno di un apposito
		 * campo interno all'oggetto
		 */		
		for (int i=0; i<this.testingTweets.size(); i++){
			int prediction = algorithm.tweetPredict(this.trainingTweets, this.testingTweets.get(i));
			this.testingTweets.get(i).setPrediction(prediction);
		}		
	}
	
	public void smartGetTweetFromFile(){
		this.testingBackupFile = this.newFileIstance(testingBackupPath);
		if (this.testingBackupFile.isFile())
			this.testingTweets = this.resumeBackup(this.testingBackupFile);
		else {
			this.testingTweets = this.readAndExecPosTagger(this.testingFile);
			this.createBackup(this.testingTweets,this.testingBackupPath);
		}
		this.elaborateTweets(this.testingTweets);
	}



	public List<Tweet> getTestingTweets(){
		return this.testingTweets;
	}

	/**
	 * Riporta le statistiche della fase di testing
	 * statistiche:
	 * - matrice di confusione
	 * - error rate
	 * - accuracy
	 * - precision
	 * - breack-even
	 * - f1-measure
	 * - numero/lista dei tweet errati
	 * @return stringa contenente le informazioni
	 */
	public String getStatistics() {
		this.statistics = new Statistics(this.testingTweets);
		return this.statistics.getStatistics();
	}

	/**
	 * Riporta le statistiche relative all'influenza sulla positività/negatività dei tweet
	 * di tutte le parti del discorso interne alle frasi. 
	 * La formattazione dell'output è: 
	 * 	parte del discorso: 
	 *    numero di occorrenze i: tweet positivi con i parti del discorso 
	 * @return array di stringhe, tale che: a[0] = influenza su positivi, a[1] = influenza su negativi
	 */
	public String[] getPartOfSpeechInfluenceStatistics() {
		if (this.statistics==null)
			this.statistics = new Statistics(this.testingTweets);
		return this.statistics.getPartOfSpeachInfluenceStatistics();
	}
	
	/**
	 * Metodo che verifica se il file system in uso è basato su Unix o su Windows.
	 * Utile per la gestione del path
	 */
	private boolean isUnixFileSystem() {
		if (System.getProperty("os.name").toLowerCase().contains("windows"))
			return false;			
		return true;
	}
	
}

