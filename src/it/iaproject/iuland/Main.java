package it.iaproject.iuland;

import it.iaproject.iuland.algorithm.ClassificationAlgorithm;
import it.iaproject.iuland.algorithm.KNNAlgorithm;
import it.iaproject.iuland.distance.CosenoSimilarity;
import it.iaproject.iuland.distance.DistanceCalculator;
import it.iaproject.iuland.distance.EuclideanDistanceCalculator;
import it.iaproject.iuland.distance.ManhattanDistanceCalculator;

public class Main {

	public static void main(String[] args) {
		
		//Creo gli oggetti necessari alla demo
		Classifier classifier = new Classifier();
		PrinterAndSaver printer = new PrinterAndSaver();
		
		//Avvio l'esecuzione
		System.out.println("Start Training");
		classifier.startSmartTraining();

		//Avvio il testing basato su 3-nn e 5-nn, calcolate con le tre tipologie di distanze implementate
		System.out.println("Start Testing");
		DistanceCalculator cosenoSimilarity = new CosenoSimilarity();
		DistanceCalculator manhattanDistance = new ManhattanDistanceCalculator();
		DistanceCalculator euclideanDistance = new EuclideanDistanceCalculator();
		ClassificationAlgorithm nn;
		String[] posInfluence;
	
		/** knn=3 **/
		nn = new KNNAlgorithm(3, manhattanDistance);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 3-nn - Manhattan ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("3-nn-manhattan.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("3-nn-manhattan-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("3-nn-manhattan-neg_stats.txt");
		printer.clean();

		nn = new KNNAlgorithm(3, euclideanDistance);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 3-nn - Euclidean ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("3-nn-euclidean.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("3-nn-euclidean-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("3-nn-euclidean-neg_stats.txt");
		printer.clean();

		nn = new KNNAlgorithm(3, cosenoSimilarity);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 3-nn - Coseno Similarity ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("3-nn-coseno.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("3-nn-coseno-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("3-nn-coseno-neg_stats.txt");
		printer.clean();

		/** knn=5 **/

		nn = new KNNAlgorithm(5, manhattanDistance);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 5-nn - Manhattan ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("5-nn-manhattan.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("5-nn-manhattan-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("5-nn-manhattan-neg_stats.txt");
		printer.clean();

		nn = new KNNAlgorithm(5, euclideanDistance);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 5-nn - Euclidean ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("5-nn-euclidean.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("5-nn-euclidean-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("5-nn-euclidean-neg_stats.txt");
		printer.clean();

		nn = new KNNAlgorithm(5, cosenoSimilarity);
		classifier.startTesting(nn);	

		printer.printAndSave("\n/***********   Statistics 5-nn - Coseno Similarity ****************/");
		printer.printAndSave(classifier.getStatistics());		
		printer.save("5-nn-coseno.txt");
		printer.clean();
		posInfluence = classifier.getPartOfSpeechInfluenceStatistics();
		printer.addToBackup(posInfluence[0]);
		printer.save("5-nn-coseno-pos_stats.txt");
		printer.clean();
		printer.addToBackup(posInfluence[1]);
		printer.save("5-nn-coseno-neg_stats.txt");
		printer.clean();

		System.out.println();
		System.out.println("Done");
	}

}
