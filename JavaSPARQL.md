
```
String query;
Query sparqlQuery = QueryFactory.create(query);
QueryExecution qexec;
if (graph != null) 
{
 qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery,  graph);
} 

else 
{
  qexec = QueryExecutionFactory.sparqlService(endpoint, sparqlQuery);
}
ResultSet results = qexec.execSelect();

String uri, property, value;
try {
 while (results.hasNext()) {
  QuerySolution soln = results.nextSolution();
   {
    try {
     uri = soln.get(var).toString();
    } catch (Exception e) {
    logger.fatal("YIKES!!!");
   }
.... 
```