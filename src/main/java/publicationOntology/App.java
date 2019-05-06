package publicationOntology;

import publicationOntology.abox.Creator;

public class App 
{
    public static void main(String[] args) throws Exception {
        if (args[0].equals("-person")) {
            Creator.createPerson();
        } else if (args[0].equals("-university")){
            Creator.createUniversity();
        } else if (args[0].equals("-paper")){
            Creator.createPaper();
        } else if (args[0].equals("-journalVolume")){
            Creator.createJournalVolume();
        } else if (args[0].equals("-university")){
            Creator.createUniversity();
        } else if (args[0].equals("-proceeding")){
            Creator.createProceeding();
        } else if (args[0].equals("-review")){
            Creator.createReview();
        }
    }
}
