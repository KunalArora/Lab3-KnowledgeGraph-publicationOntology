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
        } else if (args[0].equals("-volume")){
            Creator.createVolume();
        } else if (args[0].equals("-university")){
            Creator.createUniversity();
        } else if (args[0].equals("-review")){
            Creator.createReview();
        } else if (args[0].equals("-company")){
            Creator.createCompany();
        } else if (args[0].equals("-keyword")){
            Creator.createKeyword();
        } else if (args[0].equals("-journal")){
            Creator.createJournal();
        } else if (args[0].equals("-conference")) {
            Creator.createConference();
        } else if (args[0].equals("-edition")) {
            Creator.createEdition();
        } else if (args[0].equals("-proceeding")){
            Creator.createProceedings();
        } else if (args[0].equals("-all")){
            Creator.createCompany();
            Creator.createConference();
            Creator.createEdition();
            Creator.createJournal();
            Creator.createVolume();
            Creator.createKeyword();
            Creator.createPaper();
            Creator.createPerson();
            Creator.createProceedings();
            Creator.createReview();
            Creator.createUniversity();
        }
    }
}
