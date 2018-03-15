package com.wse.helpers;

import java.util.ArrayList;
import java.util.Random;

import org.la4j.Matrix;
import org.la4j.matrix.sparse.CCSMatrix;

import com.wse.bean.OutdegreeBean;
import com.wse.postgresdb.DocumentsDB;
import com.wse.postgresdb.LinksDB;

public class PageRankHelper {
	
	public static int getMatrixSize() {
		int result = LinksDB.fetchDistinctFromIdCount();
		return result;
	}
	
	public static void populateLinksCountAndOutdegreesMatrix (int matrixSize, Matrix linkCounts, Matrix outdegrees) {
		ArrayList<OutdegreeBean> outdegreeBeans = LinksDB.getLinks();
		int i = 0;
		int j = 0;
		for (OutdegreeBean out : outdegreeBeans) {
			i = out.getFrom_docId()-1;		//since matrix row number starts from position 0
			j = out.getTo_docId()-1;		//since matrix column number starts from position 0
			linkCounts.set(i, j, linkCounts.get(i, j) + 1);
			outdegrees.set(i, 0, outdegrees.get(i, 0)+1);
		}
		
		//Handling Dangling Links
		Random rand = new Random();
		
		for ( int k = 0; k<matrixSize; k++){
			int value = (int)outdegrees.get(k, 0);
			if (value == 0){
					int number = rand.nextInt(matrixSize);
					if (number != k && number != matrixSize) {
						linkCounts.set(k, number, linkCounts.get(k, number) + 1);
						outdegrees.set(k, 0, 1);
						//System.out.println("value set is: " + number);
					}
			}
		}
		//System.out.println(linkCounts);
		//System.out.println("----------------------");
		//System.out.println(outdegrees);
		System.out.println("----------------");
	}

	public static Matrix getLeapProbabilites(int matrixSize, double alpha) {
		Matrix leapProbabilites = new CCSMatrix(matrixSize,matrixSize);
		double value = alpha/matrixSize;
		leapProbabilites.setAll(value);
		//System.out.println(leapProbabilites);
		return leapProbabilites;
	}

	public static Matrix getLinkProbabilities(int matrixSize, Matrix linkCounts, Matrix outdegrees, double beta) {
		Matrix linkProbabilities = new CCSMatrix(matrixSize,matrixSize);
		int i = 0;	//i is row, j is column
		int j = 0;
		for (i = 0; i<matrixSize; i++) {
			for( j = 0; j<matrixSize; j++ ){
				if ( outdegrees.get(i, 0) != 0){ 
					double value = (linkCounts.get(i, j) / outdegrees.get(i, 0)) * beta;
					linkProbabilities.set(i, j, value);
				}
			}
		}
			
		return linkProbabilities;
	}

	public static Matrix getTransitionMatrix(int matrixSize,
			Matrix leapProbabilities, Matrix linkProbabilities) {
		Matrix getTransitionMatrix = leapProbabilities.add(linkProbabilities);
		return getTransitionMatrix;
	}

	public static Matrix powerIteration(int matrixSize, Matrix transitionMatrix, double alpha) {
		Matrix rank = new CCSMatrix(1,matrixSize);
		rank.set(0, 0, 1);		//initialize the rank matrix with value as 1.
		Matrix newRank = new CCSMatrix(1,matrixSize);
		Matrix subRank = new CCSMatrix(1,matrixSize);
		double leapProbility = alpha/matrixSize;
		double magnitude = 0;
		int i;
		for (i = 0; i < 120; i++) {
			newRank = rank.multiply(transitionMatrix);
			subRank = rank.subtract(newRank);
			rank = newRank;
			magnitude = getMatrixMagnitude(matrixSize, subRank);
			if (magnitude < leapProbility) {	//Check for convergence
				break;
			}
			//System.out.println(rank);
		}
		//System.out.println(i);
		return rank;
	}

	private static double getMatrixMagnitude(int matrixSize, Matrix newRank) {
		double result = 0;
		for (int i = 0; i < matrixSize; i++) {
			result += Math.pow(newRank.get(0, i), 2);
		}
		result = Math.sqrt(result);
		return result;
	}

	public static void persistPagerank(int matrixSize, Matrix pageRank) {
		DocumentsDB.updateDocumentsWithPagerank(matrixSize, pageRank);
	}
}
