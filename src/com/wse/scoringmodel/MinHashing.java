package com.wse.scoringmodel;

import java.math.BigDecimal;

import com.itextpdf.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.wse.postgresdb.DbConnection;
import com.wse.postgresdb.DocumentsDB;
import com.wse.postgresdb.JaccardDB;

public class MinHashing {
	static Connection con;
	static PreparedStatement st1;
	static PreparedStatement st2;
	static CallableStatement cst;
	static ResultSet rs;

	public static void calculateNewJaccard(int docid, int withdocid, int n) {
		try {
			con = DbConnection.getDBConnection();

			String unionCount = "select count (*) as unionCNT from ( (select s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid "
					+ " where s.docid=? group by s.md5,s.shingle,s.docid "
					+ " order by md5val limit ?) "
					+ " union "
					+ " (select s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid "
					+ " where s.docid = ? "
					+ " group by s.md5,s.shingle,s.docid order by md5val limit ?)) a";

			String intersectCount = "select count (*) as intersectCNT from ( (select s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid "
					+ " where s.docid=? group by s.md5,s.shingle,s.docid "
					+ " order by md5val limit ?) "
					+ " intersect "
					+ " (select s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid "
					+ " where s.docid = ? "
					+ " group by s.md5,s.shingle,s.docid order by md5val limit ?)) a";

			st1 = con.prepareStatement(unionCount);
			st1.setInt(1, docid );
			st1.setInt(2, n );
			st1.setInt(3, withdocid);
			st1.setInt(4, n );
			st1.execute();
			rs = st1.getResultSet();
			int unionCnt = 0;
			while (rs.next()){
				unionCnt =  rs.getInt("unionCNT");
			}
			BigDecimal newJAccard;

			if (unionCnt != 0){
				st2 = con.prepareStatement(intersectCount);
				st2.setInt(1, docid );
				st2.setInt(2, n );
				st2.setInt(3, withdocid);
				st2.setInt(4, n );
				st2.execute();
				rs = st2.getResultSet();
				int intersectCnt = 0;
				while (rs.next()){
					intersectCnt =  rs.getInt("intersectCNT");
				}
				double newjaccard = (intersectCnt/unionCnt);
				newJAccard = BigDecimal.valueOf(newjaccard);
				System.out.println("New jaccard value "+ docid + " , " + withdocid +" is "+ newJAccard);

			}else{
				newJAccard = BigDecimal.valueOf(0);
				System.out.println("New jaccard value "+ docid + " , " + withdocid +" is "+ newJAccard);
			}

			String getOldJaccard = "select jaccardcoefficient from jaccard where docid = ? and withdocid = ?;"; 

			st1 = con.prepareStatement(getOldJaccard);
			st1.setInt(1, docid );
			st1.setInt(2, withdocid);
			st1.execute();
			rs = st1.getResultSet();
			BigDecimal OldJaccard = null ;
			while (rs.next()){
				OldJaccard =  rs.getBigDecimal("jaccardcoefficient");
			}

			BigDecimal errorJaccard;
			System.out.println("Old Jaccard is "+ docid + " , " + withdocid +" is "+ OldJaccard);
			
			if (OldJaccard != null){
				int res  = OldJaccard.compareTo(newJAccard);
				if( res == 1 ){
					errorJaccard = OldJaccard.subtract(newJAccard);
					System.out.println("The difference between old Jaccard and New Jaccard "+errorJaccard);
				}else{
					errorJaccard = OldJaccard.subtract(newJAccard);
					System.out.println("The difference between old Jaccard and New Jaccard "+errorJaccard);
					System.out.println();
				}	
			}
			

			rs.close();
			st1.close();
			st2.close();
			con.close();

		}catch (SQLException e) {
			System.out.println("Error while calculating new Jaccard!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		//MinHashing.calculateNewJaccard(548,561,4);
		
			ArrayList<Integer> docids = DocumentsDB.getAllDocId();
			int count = 0;
			int n = 1;
			for (int d1 : docids) {
				for (int d2 : docids) {
					if (d1 != d2) {
						if (++count > 10000) {
							break;
						}
						if (JaccardDB.isPairPresent(d1, d2)){
							MinHashing.calculateNewJaccard(d1, d2, n);
						}
					}
				}
				if (count > 10000) {
					break;
				}
			}
			System.out.println("New JaccardCoefficent Calculation done!!");
	}
}

/*
(select  distinct j.jaccardcoefficient,s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid 
where s.docid=319
group by j.jaccardcoefficient,s.md5,s.shingle,s.docid
order by md5val
limit 4)
--union
(select distinct j.jaccardcoefficient,s.shingle,s.docid,min(s.md5) md5val from shingles s inner join jaccard j on j.docid=s.docid 
where s.docid=320
group by j.jaccardcoefficient,s.md5,s.shingle,s.docid
order by md5val
limit 4)
--select * from shingles where shingle='research teaching and the'

select cast (cast (( select count(*) from 
(select  shingle from shingles where shingle= 'research teaching and the' and docid=319  intersect select  shingle from shingles where shingle = 'Frequently Asked Questions Sprachen' and docid=320) intersctcount) as decimal(5,1))
 / cast ((select count(*) from (select shingle from shingles where docid = 319 and shingle= 'research teaching and the' union select shingle from shingles 
where docid = 320 and shingle='Frequently Asked Questions Sprachen') b)
as decimal(5,1)) as decimal(5,5)) as jaccardcoefficient; */

