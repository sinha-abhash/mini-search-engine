package com.wse.postgresdb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTableCreation {

	static Connection con;
	static ResultSet rs;

	public static void tableDocuments(){
		con = DbConnection.getDBConnection();
		Statement stmt = null;
		try
		{
			if (con != null)
			{
				stmt = con.createStatement();
				String createDocuments = "CREATE TABLE if NOT Exists public.documents "
						+ " (docid integer NOT NULL DEFAULT nextval('documents_docid_seq'::regclass),url text,crawled_on_date date, "
						+ " language character(10), pagerank double precision, doctext text, "
						+ " CONSTRAINT documents_pkey PRIMARY KEY (docid));";

				String createFeatures = "CREATE TABLE if NOT Exists public.features (  docid integer,  term character varying,  term_frequency integer,  "
						+ " tf double precision,  idf double precision, "
						+ " tfidf double precision,  term_document_frequency integer,  termscount integer,  "
						+ " avgdl double precision,  newidf double precision,  bm25score double precision,  "
						+ " normalizebm25 double precision,  normalizepgrank double precision,  combined_score double precision, "
						+ " CONSTRAINT features_docid_fkey FOREIGN KEY (docid)  "
						+ " REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION);";

				String createLinks = "CREATE TABLE if NOT Exists public.links(from_docid integer,to_docid integer,CONSTRAINT links_from_docid_fkey FOREIGN KEY (from_docid)"
						+ " REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION, CONSTRAINT links_to_docid_fkey FOREIGN KEY (to_docid)"
						+ " REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION);";

				String createStatus = "CREATE TABLE if not exists public.status ("
						+ " seeddocid integer, fromdocid integer,todocid integer,depth integer,complete boolean,"
						+ " CONSTRAINT status_fromdocid_fkey FOREIGN KEY (fromdocid) REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
						+ " CONSTRAINT status_seeddocid_fkey FOREIGN KEY (seeddocid) REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"
						+ " CONSTRAINT status_todocid_fkey FOREIGN KEY (todocid)  REFERENCES public.documents (docid) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION);";

				String createCrawRecovery = "CREATE TABLE if not exists public.crawler_recovery (seeddocid integer, docid integer, depth integer, numberOfDocs integer, crawlComplete boolean,"
						+ " CONSTRAINT crawler_recovery_docid_fkey FOREIGN KEY (docid) REFERENCES public.documents (docid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION);";
				
				String createImageidSequence = " CREATE SEQUENCE if not exists image_imageid_seq;";
				
				String createImage = " CREATE TABLE if not exists public.image( imageid integer NOT NULL DEFAULT nextval('image_imageid_seq'::regclass),"
						+ " docid integer,  imageurl text, CONSTRAINT image_pkey PRIMARY KEY (imageid),"
						+ " CONSTRAINT image_docid_fkey FOREIGN KEY (docid) REFERENCES public.documents (docid) MATCH SIMPLE"
						+ " ON UPDATE NO ACTION ON DELETE NO ACTION);";

				String createImageFeatures = "CREATE TABLE if not exists public.imagefeatures ("
						+ "  imageid integer, imageterms text, expscore double precision, CONSTRAINT imagefeatures_imageid_fkey FOREIGN KEY (imageid)"
						+ "  REFERENCES public.image (imageid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";

				String createSearchEngines = "CREATE TABLE if NOT Exists public.searchengines (  seid integer NOT NULL DEFAULT nextval('searchengines_seid_seq'::regclass),"
						+ "   searchengineurl text,  status boolean,  cw double precision,  CONSTRAINT searchengines_pkey PRIMARY KEY (seid))";
				
				String createStatistics = "CREATE TABLE if not exists statistics (  seid integer,  term character varying(200),"
						+ " df bigint, cw double precision, t double precision,  i double precision, "
						+ "  score double precision,  CONSTRAINT stats_seid_fkey FOREIGN KEY (seid)"
						+ "  REFERENCES searchengines (seid));";
				String createShingles = "Create table  if not exists shingles ( docid integer,  shingle text,  md5 integer,"
						+ "   CONSTRAINT shingles_docid_fkey FOREIGN KEY (docid) "
						+ " REFERENCES public.documents (docid) MATCH SIMPLE  ON UPDATE NO ACTION ON DELETE NO ACTION);";
				
				String createJackkard = "create table if not exists jaccard ( docid integer, withDocid integer , jaccardCoefficient double precision,"
						+ " CONSTRAINT jaccard_docid_fkey FOREIGN KEY (docid) REFERENCES public.documents (docid));";
				String createSorterTemp = "create table if not exists temp (rank int, url text,score double precision );";
				
				String createMergeResults="create table if not exists mergeresults (seid int, term varchar(200),ipaddress text, url text, score double precision, norm_doc_score double precision, coll_score double precision,"
						+ " CONSTRAINT mergeresults_seid_fkey FOREIGN KEY (seid) REFERENCES searchengines (seid));";
				String createcustSeq="CREATE SEQUENCE if not exists customer_custid_seq;";
				String createCustomer="CREATE TABLE if not exists customer (custid integer NOT NULL DEFAULT nextval('customer_custid_seq'::regclass),"
						+ "addurl text,adddesc text,budget integer,moneyperclick integer,clicksleft integer, CONSTRAINT customer_pkey PRIMARY KEY (custid))";
				
				String createIndexDocs = "CREATE INDEX if not exists Index_DocID ON documents (docid);";
				String createIndexFeatures = "CREATE INDEX if not exists Index_Featuresdocid ON features (docid);"; //add index on term column imp
				String createIndexFeaturesOnTerm = "CREATE INDEX if not exists Index_FeaturesTerm ON features (term);";
				String createIndexLinks = "CREATE INDEX if not exists Index_fromID ON links (from_docid);";
				String createIndexStatus = "CREATE INDEX if not exists Index_fromID_todocid ON status (fromdocid,todocid);";
				String createIndexCrawRecovery = "CREATE INDEX if not exists Index_CrawRecovdocid ON crawler_recovery (docid);";

				String features_tfidf = "Create or Replace View features_tfidf as select tfidf from features;";
				String features_bm25 = "Create or Replace View features_BM25 as select bm25score from features;";
				String features_combined = "Create or Replace View features_combined as select combined_score from features";

				stmt.executeUpdate(createDocuments);
				stmt.executeUpdate(createFeatures);
				stmt.executeUpdate(createLinks);
				stmt.executeUpdate(createStatus);
				stmt.executeUpdate(createCrawRecovery);
				
				stmt.executeUpdate(createSorterTemp);
				stmt.executeUpdate(createImageidSequence);
				stmt.executeUpdate(createcustSeq);
				stmt.executeUpdate(createCustomer);
				
				stmt.executeUpdate(createImage);
				stmt.executeUpdate(createImageFeatures);
				stmt.executeUpdate(createShingles);
				stmt.executeUpdate(createJackkard);
				stmt.executeUpdate(createStatistics);
				stmt.executeUpdate(createSearchEngines);
				stmt.executeUpdate(createMergeResults);
				stmt.executeUpdate(createIndexDocs);
				stmt.executeUpdate(createIndexFeatures);
				stmt.executeUpdate(createIndexFeaturesOnTerm);
				stmt.executeUpdate(createIndexLinks);
				stmt.executeUpdate(createIndexStatus);
				stmt.executeUpdate(createIndexCrawRecovery);

				stmt.executeUpdate(features_tfidf);
				stmt.executeUpdate(features_bm25);
				stmt.executeUpdate(features_combined);
				System.out.println("Tables , Indexes and Views created.");
			}
		}
		catch(SQLException se){
			se.printStackTrace();
			System.out.println("Error in table/index Creation");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error in table/index Creation");

		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se){
			}
			try{
				if(con!=null)
					con.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}
}
