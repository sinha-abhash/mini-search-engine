package com.wse.scoringmodel;

import org.la4j.Matrix;
import org.la4j.matrix.sparse.CCSMatrix;

import com.wse.helpers.PageRankHelper;

public class PageRank {
	public static double alpha = 0.1;
	public static double beta = 0.9;
	
	public static void formulatePageRankScore(){
	//public static void main (String[] args) {
		int matrixSize = PageRankHelper.getMatrixSize();
		if ( matrixSize != 0 ){
		Matrix linkCounts = new CCSMatrix(matrixSize,matrixSize);
		Matrix outdegrees = new CCSMatrix(matrixSize,1);
		
		PageRankHelper.populateLinksCountAndOutdegreesMatrix(matrixSize, linkCounts, outdegrees);
		
		Matrix leapProbabilities = PageRankHelper.getLeapProbabilites(matrixSize, alpha);
		Matrix linkProbabilities = PageRankHelper.getLinkProbabilities(matrixSize, linkCounts, outdegrees, beta);
		
		Matrix transitionMatrix = PageRankHelper.getTransitionMatrix(matrixSize,leapProbabilities,linkProbabilities);
		
		Matrix pageRank = PageRankHelper.powerIteration(matrixSize, transitionMatrix, alpha);
		
		PageRankHelper.persistPagerank(matrixSize, pageRank);
		
		System.out.println("Pagerank calculated successfully.");
		System.out.println("----------------");
		}
		else{
			System.out.println("Link table is empty, therefore pagerank cannot be computed.");
		}
	}
}
