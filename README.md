# Lab3 - Knowledge Graphs

### To Access and run the TBOX file on virtuoso server,
* Go to tbox_rdf file and you will see research_publication.owl file.
* To load and run the Tbox file, access the report/[SDM-12D]-[Nugroho_Arora].pdf file and refer to **Linking ABOX to TBOX** section.

### To Access and run the ABOX file on virtuoso server,
* Go to src/main/resources/out section to access all the .nt files.
* Alternatively, you can also run the code as a Maven project into Eclipse, IntelliJ or any other IDE to generate new .nt files in the same src/main/resources/out section,
* In the src/main/java/publicationOntology/App.java class you can see that different arguments are expected, and will drive the behavior of the program. The simplest way to define such arguments is by creating a **Run Configuration**
of type Java Application from the Run menu. In the tab menu called Arguments you will be able to edit such arguments. To create all the files, just pass **-all** as the argument in the Run Configuration. 
* To load and run the Abox files, access the  report/[SDM-12D]-[Nugroho_Arora].pdf file and refer to **Linking ABOX to TBOX** section.

All the relevant information to run queries and accessing virtuoso sparql endpoint can be accessed in the report/[SDM-12D]-[Nugroho_Arora].pdf file.
