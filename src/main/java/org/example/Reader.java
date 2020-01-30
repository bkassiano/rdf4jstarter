package org.example;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class Reader {

	public static void main(String[] args) throws IOException {

		Repository db = new SailRepository(new MemoryStore());
		db.init();

		// Open a connection to the database
		try (RepositoryConnection conn = db.getConnection()) {
			String filename = "final_ontology.owl";
			try (InputStream input = Reader.class.getResourceAsStream("/" + filename)) {
				// add the RDF data from the inputstream directly to our database
				conn.add(input, "", RDFFormat.TURTLE);
			}

			// let's check that our data is actually in the database
			try (RepositoryResult<Statement> result = conn.getStatements(null, null, null)) {
				while (result.hasNext()) {
					Statement st = result.next();
					System.out.println("db contains: " + st);
				}
			}

			//Sparql query for C1
			String queryString = "PREFIX ho: <http://www.semanticweb.org/kass/ontologies/2020/0/home-activities-ontology#> \n";
			queryString += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
			queryString += "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
			queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
			queryString += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";
			queryString += "SELECT ?activity ?start ?end ?content \n";
			queryString += "WHERE { \n";
			queryString += "    ?activity rdf:type ho:Activity . \n";
			queryString += "    ?activity ho:act_start_time ?start . \n";
			queryString += "    ?activity ho:act_end_time ?end .\n";
			queryString += "    ?activity ho:act_content ?content \n";
			queryString += "}";
			TupleQuery query = conn.prepareTupleQuery(queryString);
			try (TupleQueryResult result = query.evaluate()) {
				// we just iterate over all solutions in the result...
				System.out.println("Query C1 results: ");
				while (result.hasNext()) {
					BindingSet solution = result.next();
					// ... and print out the value of the variable bindings
					// for all the variables
					System.out.println("?activity = " + solution.getValue("activity"));
					System.out.println("?start = " + solution.getValue("start"));
					System.out.println("?end = " + solution.getValue("end"));
					System.out.println("?content = " + solution.getValue("content"));
					System.out.println();
				}
			}
			
			System.out.println();
			System.out.println();
			
			//Sparql query for C2
			String queryString2 = "PREFIX ho: <http://www.semanticweb.org/kass/ontologies/2020/0/home-activities-ontology#> \n";
			queryString2 += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
			queryString2 += "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
			queryString2 += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
			queryString2 += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";
			queryString2 += "SELECT DISTINCT ?content \n";
			queryString2 += "WHERE { \n";
			queryString2 += "    ?observation rdf:type ho:Observation . \n";
			queryString2 += "    ?observation ho:obs_content ?content \n";
			queryString2 += "}";
			TupleQuery query2 = conn.prepareTupleQuery(queryString2);
			try (TupleQueryResult result2 = query2.evaluate()) {
				// we just iterate over all solutions in the result...
				System.out.println("Query C2 results: ");
				while (result2.hasNext()) {
					BindingSet solution = result2.next();
					// ... and print out the value of the variable bindings
					// for ?content
					System.out.println("?content = " + solution.getValue("content"));
				}
			}
			
			System.out.println();
			System.out.println();
			
			//Sparql query for C3
			String queryString3 = "PREFIX ho: <http://www.semanticweb.org/kass/ontologies/2020/0/home-activities-ontology#> \n";
			queryString3 += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
			queryString3 += "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
			queryString3 += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
			queryString3 += "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";
			queryString3 += "SELECT ?obs ?content \n";
			queryString3 += "WHERE { \n";
			queryString3 += "    ?obs rdf:type ho:Observation . \n";
			queryString3 += "    ?obs ho:obs_content ?content . \n";
			queryString3 += "    ?obs ho:obs_start_time ?start . \n";
			queryString3 += "    ?obs ho:obs_end_time ?end . \n";
			queryString3 += "    FILTER (?start >= \"2014-05-05T18:34:54Z\"^^xsd:dateTime && ?end <= \"2014-05-05T18:55:40Z\"^^xsd:dateTime) \n";
			queryString3 += "}";
			TupleQuery query3 = conn.prepareTupleQuery(queryString3);
			try (TupleQueryResult result3 = query3.evaluate()) {
				// we just iterate over all solutions in the result...
				System.out.println("Query C3 results: ");
				while (result3.hasNext()) {
					BindingSet solution = result3.next();
					// ... and print out the value of the variable bindings
					// for ?obs and ?content
					System.out.println("?obs = " + solution.getValue("obs"));
					System.out.println("?content = " + solution.getValue("content"));
					System.out.println();
				}
			}

		} finally {
			// before our program exits, make sure the database is properly shut down.
			db.shutDown();
		}

	}
}
