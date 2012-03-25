package org.synote.linkeddata

/*
 * Defines all the vocabularies, including classes and properties,which will be used
 */
class Vocabularies {
	/*
	 * Define namespaces in form of XX_NS:
	 * [prefixString,URI]
	 * 
	 * URI should ends with # or /
	 */
	static final String[] RDF_NS = ["rdf",'''http://www.w3.org/1999/02/22-rdf-syntax-ns#''']
	static final String[] RDFS_NS = ["rdfs",'''http://www.w3.org/2000/01/rdf-schema#''']
	static final String[] DC_NS = ["dc",'''http://purl.org/dc/elements/1.1/''']
	static final String[] DCTERMS_NS = ["dcterms",'''http://purl.org/dc/terms/''']
	static final String[] SCHEMAORG_NS = ["schemaorg",'''http://schema.org/'''] //schema.org
	static final String[] FOAF_NS=["foaf",'''http://xmlns.com/foaf/0.1/''']
	static final String[] MAONT_NS=["ma",'''http://www.w3.org/ns/ma-ont#'''] //ontology for media resource
	static final String[] OAC_NS=["oac",'''http://www.openannotation.org/ns/'''] //open annotation collaboration
	static final String[] ORE_NS=["ore",'''http://www.openarchives.org/ore/terms/'''] //object reuse and exchange, part of open archieves initiative

	
	/*
	 * Define properties in form of XX_PropertyName:
	 * [nsPrefix+propertyName,propertyURI]
	 * 
	 */
	//static final String[] RDFS_LABEL = [RDF_NS[0]+":label",'''http://www.w3.org/2000/01/rdf-schema#label''']
	//static final String[] RDFS_SEEALSO = [RDF_NS[0]+":seeAlso",'''http://www.w3.org/2000/01/rdf-schema#seeAlso''']
}
