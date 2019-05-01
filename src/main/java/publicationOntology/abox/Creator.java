package publicationOntology.abox;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Creator {


    public static void createUniversity() throws IOException {

        Model model = ModelFactory.createDefaultModel();


        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");


            String universityName = row_data[2];
            String universityHomepage = row_data[3];

            String universityUri = universityName.replace(" ","_");
            Resource currentUniversity = model.createResource(Config.BASE_URL+universityUri)
                    // TODO: Decide whether it's better to use RDFS.label or FOAF.name or our own property
                    .addProperty(RDFS.label, universityName)
                    .addProperty(FOAF.homepage,universityHomepage);
        }
        csvReader.close();

        model.write(System.out);
    }

    public static void createPerson() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.AUTHOR_UNIVERSITY_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(";");
            String[] names = row_data[0].split(" ");
            String lastName = names[names.length-1];

            String firstName = names[0];
            String personUri = names[0];
            for(String name:names){
                if(!(name.equals(lastName) || name.equals(firstName))){
                    firstName += " "+name;
                    personUri += "_"+name;
                }
            }
            personUri = Config.BASE_URL + personUri + lastName;


            Resource currentPerson = model.createResource(personUri)
                    .addProperty(FOAF.firstName, firstName)
                    .addProperty(FOAF.lastName, lastName);
        }
        csvReader.close();

        model.write(System.out);
    }

    public static void createPaper() throws IOException {
        // author,cite,ee,journal,key,mdate,pages,title,volume,year,type,booktitle,crossref,corresponding_author,keyword,reviewer
        Model model = ModelFactory.createDefaultModel();

        // read the csv line by line
        BufferedReader csvReader = new BufferedReader(new FileReader(Config.PAPER_PATH));
        String row;
        while ((row = csvReader.readLine()) != null) {
            String[] row_data = row.split(",");

            String title = row_data[6];

            // the URI of paper is taken from its DBLP key
            String paperUri = Config.BASE_URL+row_data[3].replace("/","_");

            System.out.println(paperUri);
            Resource currentPaper = model.createResource(paperUri)
                    // TODO: Change RDFS.label to our own title property
                    .addProperty(RDFS.label, title);
        }
        csvReader.close();

        model.write(System.out);

    }
}
