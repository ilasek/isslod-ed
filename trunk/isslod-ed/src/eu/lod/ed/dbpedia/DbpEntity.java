package eu.lod.ed.dbpedia;

public class DbpEntity {
    private String label;
    private String uri;
    private String rdfType;
    
    public String getRdfType() {
        return rdfType;
    }
    public void setRdfType(String rdfType) {
        this.rdfType = rdfType;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }

}
