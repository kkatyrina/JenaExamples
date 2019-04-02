/**
 * Created by Катерина on 05.03.2019.
 */
public enum Constants {
    ELEMENT("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
    RDFS("http://www.w3.org/2000/01/rdf-schema"),
    LABEL("http://www.w3.org/2000/01/rdf-schema#label"),
    OWL("http://www.w3.org/2002/07/owl"),
    WWW("http://www.mathEnc.ru");

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}

