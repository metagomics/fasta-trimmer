# fasta-trimmer
FASTA trimmer takes in a FASTA file and a list of unmodified peptide sequences
and outputs text for a new FASTA file where all protein entries for which no
peptide could be matched are removed.

Because FASTA files can be very large, the FASTA files is processed 1,000
sequences at a time. You will notice this in the screen output as the
program is running.

# How to run:
Ensure Java is installed on your system: https://java.com/en/download/

Download the latest release: https://github.com/metagomics/fasta-trimmer/releases

Run with: java -jar fastaTrimmer.jar /path/to/file.fasta /path/to/peptide.list >/path/to/new/file.fasta


# Usage:
java -jar fastaTrimmer.jar /path/to/file.fasta /path/to/peptide.list >/path/to/new/file.fasta

"peptide.list" should be a list of unmodified peptide sequences, one per line.

Alternatively, "peptide.list" may be a tab-delimited file, so long as the
peptide sequence is the first column.
