

One of five Student Work Projects on the Indian-summer school on Linked Data (http://lod2.eu/Article/ISSLOD2011). We implemented a simple entity disambiguation approach and made the results visible on a Web UI.

## Introduction ##
  * Given:
    * Reference knowledge base(s) K
    * Text fragment T
    * Set E of Named Entities
  * Task:
    * Find URI for each of the Named Entities
## URI lockup ##
  * Input: String I (label of an entity)
    * Get all entities with rdfs:label l
      * SELECT DISTINCT ?uri WHERE { ?uri rdfs:label “Paris”@en. }
    * or each entity ?e, merge all labels
      * SELECT ?label WHERE { ?e rdfs:label ?label. }
## Disambiguation Approach ##
  * Input: input text T, list of candidates C (Stanford NER http://nlp.stanford.edu/ner/index.shtml)
  1. Remove stopwords in T
  1. Stem each word in T
  1. For each c in C
    1. Remove stopwords in c
    1. Stem each word in c
    1. Calculate Jaccard coef. between c and T
  1. Return ranked list of entities
## Web UI ##
![http://content.screencast.com/users/michamichamicha/folders/Jing/media/15934cff-31eb-41bc-a7ba-6b6bbce70920/2011-09-16_1848.png](http://content.screencast.com/users/michamichamicha/folders/Jing/media/15934cff-31eb-41bc-a7ba-6b6bbce70920/2011-09-16_1848.png)