package org.neo4j.tutorial;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;

import static junit.framework.Assert.assertEquals;

import static org.neo4j.kernel.impl.util.StringLogger.DEV_NULL;

/**
 * In this Koan we focus on paths in Cypher.
 */
public class Koan10
{
    private static EmbeddedDoctorWhoUniverse universe;

    @BeforeClass
    public static void createDatabase() throws Exception
    {
        universe = new EmbeddedDoctorWhoUniverse( new DoctorWhoUniverseGenerator().getDatabase() );
    }

    @AfterClass
    public static void closeTheDatabase()
    {
        universe.stop();
    }

    @Test
    public void shouldFindHowManyRegenerationsBetweenTomBakerAndChristopherEccleston() throws Exception
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE
        cql = "MATCH (tom:Actor{actor : 'Tom Baker'})-[r:REGENERATED_TO*]->(chris:Actor{actor : 'Christopher Eccleston'})" +
                " return length(r) as regenerations";

        ExecutionResult result = engine.execute( cql );

        assertEquals( 6, result.javaColumnAs( "regenerations" ).next() );
    }

    @Test
    public void shouldFindTheLongestContinuousStoryArcWithTheMaster() throws Exception
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE
        cql = "MATCH (master:Character{character : 'Master'})-[:APPEARED_IN]->(master_eps:Episode), \n" +
                " path = (master_eps)-[next_ep_hops:NEXT*]->(:Episode)" +
                " WITH nodes(path) as eps, master as master, path as path "+
                " WHERE ALL(ep in eps WHERE master-[:APPEARED_IN]->ep)" +
                " RETURN length(path) as noOfPathHops"+
                " ORDER BY noOfPathHops DESC LIMIT 1";
        ExecutionResult result = engine.execute( cql );
        // noOfPathHops is one less than the number of episodes in a story arc
        final int noOfStories = 5;
        assertEquals( noOfStories - 1, result.javaColumnAs( "noOfPathHops" ).next() );
    }
}
