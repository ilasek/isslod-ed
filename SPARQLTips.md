# Get things with label X #
```
SELECT * WHERE { ?s rdfs:label "X"@en }
```
# Get things whose label contains X #
```
SELECT * WHERE { ?s rdfs:label ?o. ?o bif:contains "X" }
```
# Count things whose label contains X #
```
SELECT count(*) WHERE { ?s rdfs:label ?o. ?o bif:contains "X" }
```
# Get the types T of a thing X #
```
SELECT ?T WHERE { X rdf:type ?t}
```
# Count the number of direct links between X and Y #
```
SELECT count(?p) WHERE {{X ?p Y} UNION {Y ?p X}}
```

# Count the number of indirect links between X and Y #
```
SELECT count(?s) WHERE { {X ?p ?s} UNION {?s ?p X}. {Y ?q ?s} UNION {?s ?q Y}}
```