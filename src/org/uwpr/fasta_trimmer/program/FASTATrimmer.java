package org.uwpr.fasta_trimmer.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.fasta.FASTAEntry;
import org.yeastrc.fasta.FASTAHeader;
import org.yeastrc.fasta.FASTAReader;

public class FASTATrimmer {

	public static FASTATrimmer getInstance() { return new FASTATrimmer(); }
	
	private static final int ENTRIES_TO_PROCESS_AT_ONCE = 1000;
	
	public void trimFASTA( File fastaFile, File peptideFile ) throws Exception {
		
		Map<String, List<String>> fastaEntries = new HashMap<>();				
		Collection<String> peptidesToParse = getPeptidesToParse( peptideFile );
		
		int fastaEntriesTested = 0;
						
		FASTAReader faReader = FASTAReader.getInstance( fastaFile );
		try {
			
			FASTAEntry entry = faReader.readNext();
			
			if( entry == null ) {
				throw new Exception( "Could not find a FASTA entry in the file. Is it a FASTA file?" );
			}
			
			int counter = 1;
			
			System.err.println( "Loading batch of " + ENTRIES_TO_PROCESS_AT_ONCE + " FASTA entries into memory..." );
			
			while ( entry != null ) {

				
				if( counter % ENTRIES_TO_PROCESS_AT_ONCE == 0 ) {
					
					
					System.err.println( "\tMatching peptides to proteins...\n" );
					
					for( String name : fastaEntries.keySet() ) {
							
						fastaEntriesTested++;
						
						for( String peptideToTest : peptidesToParse ) {
																					
							if( proteinContainsPeptide( fastaEntries.get( name ).get( 0 ), peptideToTest ) ) {
								System.err.println( "\t\tMatched protein: " + name );
								
								System.out.println( createFastaEntry( name, fastaEntries.get( name ).get( 1 ), fastaEntries.get( name ).get( 0 ) ) );
								break; // don't need to test more peptides, we're keeping this name
							}							
						}														
					}
										
					fastaEntries = new HashMap<>();
							
					System.err.println( "Loading batch of " + ENTRIES_TO_PROCESS_AT_ONCE + " FASTA entries into memory..." + "(" + entry.getHeaderLineNumber() + ")" );
					
				}
				
				
				Set<FASTAHeader> headers = entry.getHeaders();
				
				// only supporting one header per line right now
				if( headers.size() > 1 ) {
					throw new Exception( "Got more than one header for line: " + entry.getHeaderLine() + " (" + entry.getHeaderLineNumber() + ")" );
				}
				
				for( FASTAHeader header : headers ) {
					
					fastaEntries.put( header.getName(), new ArrayList<>(2));
					fastaEntries.get( header.getName() ).add( entry.getSequence() );
					fastaEntries.get( header.getName() ).add( header.getDescription() );					
					
				}
				
				counter++;
				entry = faReader.readNext();
			}
			
			
		} finally {			
			try { faReader.close(); }
			catch( Exception e ) { ; }
		}
				
		
		System.err.println( "\tMatching peptides to proteins...   " );
		
		for( String name : fastaEntries.keySet() ) {
			
			fastaEntriesTested++;
			
			for( String peptideToTest : peptidesToParse ) {
																		
				if( proteinContainsPeptide( fastaEntries.get( name ).get( 0 ), peptideToTest ) ) {
					System.out.println( createFastaEntry( name, fastaEntries.get( name ).get( 1 ), fastaEntries.get( name ).get( 0 ) ) );
					break; // don't need to test more peptides, we're keeping this name
				}							
			}														
		}
		
		System.err.println( "Tested " + fastaEntriesTested + " fasta entries." );
		
	}
	
	private String createFastaEntry( String name, String description, String sequence ) {
		return ">" + name + " " + description + "\n" + sequence;
	}
	
	
	/**
	 * Return true if the protein sequence contains the given peptide sequence--assuming trypsin was used
	 * to digest the proteins. Handle I/L substitution
	 * 
	 * @param proteinSequence
	 * @param peptideSequence
	 * @return
	 */
	private boolean proteinContainsPeptide( String proteinSequence, String peptideSequence ) {
		
		//System.err.println( "Testing if " + proteinSequence + " contains " + peptideSequence );

		proteinSequence = proteinSequence.replaceAll( "L", "I" );
		peptideSequence = peptideSequence.replaceAll( "L", "I" );

		if( proteinSequence.startsWith( peptideSequence ) )
			return true;
		
		if( proteinSequence.contains( "K" + peptideSequence ) )
			return true;
		
		if( proteinSequence.contains( "R" + peptideSequence ) )
			return true;
		
		return false;		
	}
	
	
	private Collection<String> getPeptidesToParse( File file ) throws Exception {
		
		Collection<String> peptidesToParse = new HashSet<>();
		
		BufferedReader br = null;
		try {
			
			br = new BufferedReader( new FileReader( file ) );
			String line = br.readLine();
			
			while( line != null ) {
				
				String[] fields = line.split( "\t" );
				
				peptidesToParse.add( fields[ 0 ] );
				
				line = br.readLine();
			}
			
		} finally {
			br.close();
			br = null;
		}
		
		
		
		return peptidesToParse;
		
	}
	
}
