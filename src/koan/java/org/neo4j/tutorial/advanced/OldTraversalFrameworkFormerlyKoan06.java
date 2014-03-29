package org.neo4j.tutorial.advanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.tutorial.DoctorWhoLabels;
import org.neo4j.tutorial.DoctorWhoRelationships;
import org.neo4j.tutorial.DoctorWhoUniverseGenerator;
import org.neo4j.tutorial.EmbeddedDoctorWhoUniverse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * In this Koan we start using the simple traversal framework to find
 * interesting information from the graph.
 */
// TODO: consider deleting this, the API is deprecated anyway.
public class OldTraversalFrameworkFormerlyKoan06
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
    public void shouldFindAllCompanions()
    {
        Node theDoctor = universe.theDoctor();
        Traverser t = null;

        GraphDatabaseService database = universe.getDatabase();

        try ( Transaction tx = database.beginTx() )
        {

            // YOUR CODE GOES HERE

            Collection<Node> foundCompanions = t.getAllNodes();

            int knownNumberOfCompanions = 47;
            assertEquals( knownNumberOfCompanions, foundCompanions.size() );
            tx.success();
        }
    }

    @Test
    public void shouldFindAllDalekProps()
    {
        GraphDatabaseService database = universe.getDatabase();

        try ( Transaction tx = database.beginTx() )
        {
            Node theDaleks = database.findNodesByLabelAndProperty( DoctorWhoLabels.SPECIES, "species",
                    "Dalek" ).iterator().next();

            Traverser t = null;

            // YOUR CODE GOES HERE

            assertCollectionContainsAllDalekProps( t.getAllNodes() );
            tx.success();
        }
    }

    private void assertCollectionContainsAllDalekProps( Collection<Node> nodes )
    {
        String[] dalekProps = new String[]{"Dalek One-7", "Imperial 4", "Imperial 3", "Imperial 2", "Imperial 1",
                "Supreme Dalek", "Remembrance 3", "Remembrance 2", "Remembrance 1", "Dalek V-VI", "Goon IV", "Goon II",
                "Goon I", "Dalek Six-5", "Dalek Seven-2", "Dalek V-5", "Dalek Seven-V", "Dalek Six-Ex",
                "Dalek Seven-8", "Dalek 8", "Dalek 7", "Dalek Five-6", "Dalek Two-1", "Dalek 2", "Dalek 1", "Dalek 6",
                "Dalek 5", "Dalek 4", "Dalek 3", "Dalek IV-Ex", "Dalek Seven-II", "Necros 3", "Necros 2", "Necros 1",
                "Goon III", "Goon VII", "Goon VI", "Goon V", "Gold Movie Dalek", "Dalek Six-7", "Dalek One-5"};

        List<String> propList = new ArrayList<>();
        for ( Node n : nodes )
        {
            propList.add( n.getProperty( "prop" ).toString() );
        }

        assertEquals( dalekProps.length, propList.size() );
        for ( String prop : dalekProps )
        {
            assertTrue( propList.contains( prop ) );
        }
    }

    @Test
    public void shouldFindAllTheEpisodesTheMasterAndDavidTennantWereInTogether()
    {
        GraphDatabaseService database = universe.getDatabase();

        try ( Transaction tx = database.beginTx() )
        {
            Node theMaster = database.findNodesByLabelAndProperty( DoctorWhoLabels.CHARACTER, "character",
                    "Master" ).iterator().next();
            Traverser t = null;


            // YOUR CODE GOES HERE

            int numberOfEpisodesWithTennantVersusTheMaster = 4;
            assertEquals( numberOfEpisodesWithTennantVersusTheMaster, t.getAllNodes()
                    .size() );
            tx.success();
        }
    }
}
