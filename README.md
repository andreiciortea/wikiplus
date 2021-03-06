# Wikiplus

Wikiplus is a Java/Play Web application that augments Wikipedia pages with dynamic data retrieved from multiple datasets openly available on the Web. This prototype implementation was developed at the WISS2014 hackathon.

The proof of concept implementation supports several features, such as:
* widget for displaying the local time in a dbpedia-owl:PopulatedPlace;
* widget for displaying information about the weather in a dbpedia-owl:PopulatedPlace;
* content negatiation (text/html and text/turtle).


## Sample queries
Retrieve an HTML representation for Lyon:
curl -i -H "Accept: text/html" -X GET http://localhost:9000/Lyon

Retrieve an RDF representation of the dynamic data that is available for Lyon:
curl -i -H "Accept: text/turtle" -X GET http://localhost:9000/Lyon


## Extensibility
New widgets may be added on the back end by extending the Widget class and mapping them to DBpedia concepts in Application.getFilteredWidgetList(...).
