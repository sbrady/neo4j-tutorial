package org.neo4j.tutorial.advanced;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Uniqueness;
import org.neo4j.tutorial.DoctorWhoRelationships;
import org.neo4j.tutorial.DoctorWhoUniverseGenerator;
import org.neo4j.tutorial.EmbeddedDoctorWhoUniverse;

import static org.junit.Assert.assertThat;

import static org.neo4j.tutorial.matchers.ContainsOnlySpecificActors.containsOnlyActors;
import static org.neo4j.tutorial.matchers.ContainsSpecificNumberOfNodes.containsNumberOfNodes;

/**
 * In this Koan we start using the new traversal framework to find interesting
 * information from the graph about the Doctor's past life.
 */
public class TraversalAPIFormerlyKoan07
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
    public void shouldDiscoverHowManyDoctorActorsHaveParticipatedInARegeneration() throws Exception
    {
        Node theDoctor = universe.theDoctor();
        TraversalDescription regeneratedActors = null;

        GraphDatabaseService database = universe.getDatabase();

        try ( Transaction tx = database.beginTx() )
        {
            // YOUR CODE GOES HERE
            // Note: every doctor has participated in a regeneration, including the first and last Doctors


            assertThat( regeneratedActors.traverse( theDoctor ).nodes(), containsNumberOfNodes( 13 ) );
            tx.success();
        }
    }

    @Test
    public void shouldFindTheFirstDoctor()
    {
        Node theDoctor = universe.theDoctor();
        TraversalDescription firstDoctor = null;

        GraphDatabaseService database = universe.getDatabase();

        try ( Transaction tx = database.beginTx() )
        {

            // YOUR CODE GOES HERE

            assertThat( firstDoctor.traverse( theDoctor ).nodes(), containsOnlyActors( "William Hartnell" ) );
            tx.success();
        }
    }
}
