/**
 * Created by Катерина on 01.04.2019.
 */

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.io.FileInputStream;
import java.io.InputStream;

public class getResources {

    private static String filename;
    private static Model model;
    private static OntModel ontModel;
    private static InfModel inf;

    private static String createNewSubject(String name) {
        return Constants.WWW + "#" + name;
    }

    public static void main(String args[]) {
        //Инициализация модели онтологии
        try {
            filename = getResources.class.getClassLoader().getResource("").getPath() + "/" + "MathOnt.rdf";
            model = ModelFactory.createDefaultModel();
            InputStream in = new FileInputStream(filename);
            model = model.read(in, null);
            inf = ModelFactory.createRDFSModel(model);
            ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, model);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        getIndividuals("Персона");
        getClassName("Algebra");
        getObjectProperties("Термин");
        getInstanceProperty("Algebra", "Содержится_в_названии_терм_англ");
        getRDFSProperty("Correlation", "comment");
        getPersonName("Блез_Паскаль", "en");
    }

    //Метод отправляет запрос к онтологии и получает ответ
    private static void getJenaResults(String query) {
        Query jenaQuery = QueryFactory.create(query) ;
        QueryExecution qexec = QueryExecutionFactory.create(jenaQuery, inf);
        ResultSet results = qexec.execSelect();

        while (results.hasNext()) {
            String result = results.next().get("variable").toString();
            System.out.println(result);
        }
    }

    //Поиск всех экземпляров заданного класса
    public static void getIndividuals(String className) {
        String classURI = createNewSubject(className);
        String query =
                "select ?variable where {\n" +
                        "?variable" + " <" + Constants.ELEMENT + ">" + " <" + classURI + ">\n" +
                "}";
        System.out.println("INDIVIDUALS:\n" + query);
        getJenaResults(query);
    }

    //Поиск НАЗВАНИЙ всех объектных свойств заданного класса
    public static void getObjectProperties(String className) {
        String classURI = createNewSubject(className);
        String query =
                "select ?variable where {\n" +
                        "?variable a " + "<" + Constants.OWL + "#ObjectProperty" + "> .\n" +
                        "?variable " + "<" + Constants.RDFS + "#domain" + "> " + "<" + classURI + ">\n" +
                "}";
        System.out.println("OBJECT PROPERTIES:\n" + query);
        getJenaResults(query);
    }

    //Поиск класса, которому принадлежит выбранный экземпляр.
    //Может дополнительно возвращать http://www.w3.org/2000/01/rdf-schema#Resource, это нормально и можно игнорировать
    public static void getClassName(String instanceName) {
        String instanceURI = createNewSubject(instanceName);
        String query =
                "select ?variable where {\n" +
                        "<" + instanceURI + "> " + "a " + "?variable\n" +
                "}";
        System.out.println("CLASS BY INSTANCE:\n" + query);
        getJenaResults(query);
    }

    //Поиск ЗНАЧЕНИЯ определённого в онтологии свойства для заданного экземпляра
    public static void getInstanceProperty(String instanceName, String propertyName) {
        String instanceURI = createNewSubject(instanceName);
        String propertyURI = createNewSubject(propertyName);
        String query =
                "select ?variable where {\n" +
                        "<" + instanceURI + "> " + "<" + propertyURI + "> " + "?variable\n" +
                "}";
        System.out.println("INSTANCE PROPERTY:\n" + query);
        getJenaResults(query);
    }

    //Поиск ЗНАЧЕНИЯ заданного RDFS-свойства заданного экземпляра (пример: label, comment...)
    //Для поиска OWL-свойств действия аналогичны, заменить Constants.RDFS на Constants.OWL
    public static void getRDFSProperty(String instanceName, String propertyName) {
        String instanceURI = createNewSubject(instanceName);
        String propertyURI = Constants.RDFS + "#" + propertyName;
        String query =
                "select ?variable where {\n" +
                        "<" + instanceURI + "> " + "<" + propertyURI + "> " + "?variable\n" +
                "}";
        System.out.println("RDFS PROPERTY:\n" + query);
        getJenaResults(query);
    }

    //Поиск имени заданной персоны на выбранном языке
    public static void getPersonName(String name, String language) {
        String query =
                "select ?variable where {\n" +
                        "<" + Constants.WWW + "#" + name + "> " + "<" + Constants.LABEL + "> " + "?variable\n" +
                        "FILTER(langMatches(lang(?variable), \"" +language + "\"))\n" +
                        "}";
        System.out.println("PERSON NAMES:\n" + query);
        getJenaResults(query);
    }
}
