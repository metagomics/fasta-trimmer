package org.uwpr.fasta_trimmer.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MainProgram {

	
	
	public static void main( String[] args ) throws Exception {
		
		if( args.length < 2 ) {
			printHelp();
			System.exit( 0 );
		}
		
		File fastaFile = new File( args[ 0 ] );
		if( !fastaFile.exists() ) {
			System.out.println( "Could not find FASTA file: " + fastaFile.getAbsolutePath() );
			System.exit( 1 );
		}
		
		File peptideFile = new File( args[ 1 ] );
		if( !peptideFile.exists() ) {
			System.out.println( "Could not find peptide file: " + peptideFile.getAbsolutePath() );
			System.exit( 1 );
		}
		
		
		FASTATrimmer.getInstance().trimFASTA( fastaFile,  peptideFile );
		
		
	}
	

	public static void printHelp() {
		
		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "help.txt" ) ) ) ) {
			
			String line = null;
			while ( ( line = br.readLine() ) != null )
				System.out.println( line );				
			
		} catch ( Exception e ) {
			System.out.println( "Error printing help." );
		}
		
		
	}
	
}

