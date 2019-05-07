package publicationOntology.abox;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.Random;

public class Creator {


    public static void createCompany() throws IOException {

        Model model = ModelFactory.createDefaultModel();


        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.COMPANIES_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");


            String companyName = row_data[0];
            String industry = row_data[1];

            String companyUri = companyName.replace(" ","_");
            Resource currentCompany = model.createResource(Config.RESOURCE_URL+companyUri)
                    // TODO: Decide whether it's better to use RDFS.label or FOAF.name or our own property
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"industry"),industry)
                    .addProperty(FOAF.name, companyName);
        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"company.nt")), true), "NT");    }

    public static void createUniversity() throws IOException {

        Model model = ModelFactory.createDefaultModel();


        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {

            String[] row_data = row.split(";");


            String universityName = row_data[2];
            String universityHomepage = row_data[3];

            if (!(universityHomepage.equals("N/A"))){
                String universityUri = universityName.replace(" ","_");
                Resource currentUniversity = model.createResource(Config.RESOURCE_URL+universityUri)
                        .addProperty(FOAF.name, universityName)
                        .addProperty(FOAF.homepage,universityHomepage);
            }

        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"university.nt")), true), "NT");    }

    public static void createPerson() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");
            String[] names = row_data[0].split(" ");
            String[] papers = row_data[1].split("\\|");

            String lastName = names[names.length-1];

            String firstName = names[0];
            String personUri = names[0];
            for(String name:names){
                if(!(name.equals(lastName) || name.equals(firstName))){
                    firstName += " "+name;
                    personUri += "_"+name;
                }
            }
            personUri = Config.RESOURCE_URL + personUri + lastName;

            String workplaceUri = Config.RESOURCE_URL+row_data[2].replace(" ","_");

            Resource currentPerson = model.createResource(personUri)
                    .addProperty(FOAF.firstName, firstName)
                    .addProperty(FOAF.lastName, lastName)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"works_in"),workplaceUri);

            for(String paper:papers){
                currentPerson.addProperty(model.createProperty(Config.PROPERTY_URL+"writes"), Config.RESOURCE_URL+paper.replace("/","_"));
            }
        }
        csvReader.close();
        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"person.nt")), true), "NT");
    }

    public static void createPaper() throws IOException {
        Random randomGenerator = new Random();
        // author,cite,ee,journal,key,mdate,pages,title,volume,year,type,booktitle,crossref,corresponding_author,keyword,reviewer
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PAPER_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String title = row_data[6];
            String year = row_data[8];
            // the URI of paper is taken from its DBLP key
            String paperUri = Config.RESOURCE_URL+row_data[3].replace("/","_");

            Resource currentPaper = model.createResource(paperUri)
                    // TODO: Change RDFS.label to our own title property
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"title"), title)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"abstract"),Utils.getLoremIpsum())
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"publication_year"),year)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"isbn"),RandomStringUtils.randomAlphanumeric(7)+"-"
                            +RandomStringUtils.randomAlphanumeric(17)+"-"
                            +RandomStringUtils.randomAlphanumeric(7));
            Double randomDouble = Math.random();
            if(randomDouble < 0.25) {
                // shortPaper
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"page_size"), String.valueOf(Utils.getRandomNumberInRange(3,4)));
            } else if(randomDouble > 0.25 && randomDouble < 0.5) {
                // demoPaper
                String randomVideoUrl= RandomStringUtils.randomAlphanumeric(10);
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"video_url"),"http://youtube.com/watch?v="+randomVideoUrl);
            } else if (randomDouble > 0.5 && randomDouble < 0.75) {
                // surveyPaper
                String randomFormUrl = RandomStringUtils.randomAlphanumeric(5);
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"survey_url"),"https://www.surveymonkey.com/r/"+randomFormUrl);
            } else {
                // fullPaper
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"page_size"), String.valueOf(Utils.getRandomNumberInRange(10,12)));
            }

            // Add citation
            for(String citedPaper: row_data[0].split("\\|")){
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"cite"), Config.RESOURCE_URL+citedPaper);
            }

            // Add keyword (taking any from the title that have length > 3)
            for(String keyword:title.split(" ")){
                if (keyword.length()>3){
                    currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"has_keyword"),Config.RESOURCE_URL+"kw_"+keyword);
                }
            }

            // Presented In, only for conference paper
            if (row_data[3].contains("conf")){
                currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"presented_in"),Config.RESOURCE_URL+row_data[3]+"_event");
            }

            // Published In
            currentPaper.addProperty(model.createProperty(Config.PROPERTY_URL+"published_in"),Config.RESOURCE_URL+row_data[3]);
        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"paper.nt")), true), "NT");
    }

    public static void createVolume() throws IOException {
        // title, volume, year
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.JOURNAL_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String volumeUri = Config.RESOURCE_URL+row_data[0].replace(" ","_") + "_Volume_" + row_data[1];
            String journalUri = Config.RESOURCE_URL+row_data[0].replace(" ","_");
            Resource currentVolume = model.createResource(volumeUri)
                    .addProperty(model.createProperty(Config.BASE_URL+"volume_no"), row_data[1])
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"has_journal"),journalUri);
        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"volume.nt")), true), "NT");    }

    public static void createJournal() throws IOException {
        // title, volume, year
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.JOURNAL_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String journalUri = Config.RESOURCE_URL+row_data[0].replace(" ","_");

            Resource currentJournalVolume = model.createResource(journalUri)
                    .addProperty(model.createProperty(Config.BASE_URL+"publisher"), row_data[3])
                    .addProperty(FOAF.name, row_data[0]);
        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"journal.nt")), true), "NT");    }

    public static void createReview() throws IOException {
        // booktitle,editor,ee,isbn,key,mdate,publisher,series,title,volume,year,location
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.REVIEW_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String paperKey = row_data[1].replace("/","_");
            String paperUri = Config.RESOURCE_URL+paperKey;

            String name = row_data[0].replace(" ","_");

            String reviewUri = Config.RESOURCE_URL + name + "_" + paperKey;
            String personUri = Config.RESOURCE_URL + name;

            Resource currentReview = model.createResource(reviewUri)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"aboutPaper"),paperUri)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"hasReviewer"),personUri);

        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"reviews.nt")), true), "NT");    }

    public static void createKeyword() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PAPER_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String title = row_data[6];
            for(String keyword: title.split(" ")){
                if (keyword.length() > 3){
                    String keywordUri = Config.RESOURCE_URL+"kw_"+keyword;
                    Resource currentTitle = model.createResource(keywordUri)
                            .addProperty(model.createProperty(Config.PROPERTY_URL+"keyword"),keyword);
                }
            }

        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"keyword.nt")), true), "NT");    }

    public static void createConference() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PROCEEDING_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");

            String title = row_data[0];
            String publisher = row_data[6];
            String conferenceUri = Config.RESOURCE_URL+title.replace(" ","_");
            Resource currentConference = model.createResource(conferenceUri)
                    .addProperty(FOAF.name, title)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"publisher"), publisher);


        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"conference.nt")), true), "NT");    }

    public static void createEdition() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PROCEEDING_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");

            String editionNumber = row_data[9];
            String year = row_data[10];
            String venue = row_data[11];
            String conferenceUri = Config.RESOURCE_URL+row_data[0].replace(" ","_");
            String editionUri = Config.RESOURCE_URL+row_data[4];
            Resource currentEdition = model.createResource(editionUri)
                    .addProperty(model.createProperty(Config.RESOURCE_URL+"year"), year)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"edition_number"), editionNumber)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"venue"), venue)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"has_conference"), conferenceUri);

        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"edition.nt")), true), "NT");    }

    public static void createProceedings() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PROCEEDING_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");

            String title = row_data[0];
            String conferenceUri = Config.RESOURCE_URL+title.replace(" ","_");
            String proceedingsUri = Config.RESOURCE_URL+"proc_"+title.replace(" ","_");
            Resource currentConference = model.createResource(proceedingsUri)
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"abstract"), Utils.getLoremIpsum())
                    .addProperty(model.createProperty(Config.PROPERTY_URL+"linked_to"),conferenceUri);


        }
        csvReader.close();

        model.write(new PrintStream(
                new BufferedOutputStream(
                        new FileOutputStream(Config.OUTPUT_PATH+"proceeding.nt")), true), "NT");    }
}
