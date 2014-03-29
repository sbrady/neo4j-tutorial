package org.neo4j.tutorial.advanced;

import java.util.List;
import java.util.Map;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphmatching.CommonValueMatchers;
import org.neo4j.graphmatching.PatternMatch;
import org.neo4j.graphmatching.PatternMatcher;
import org.neo4j.graphmatching.PatternNode;
import org.neo4j.server.CommunityNeoServer;
import org.neo4j.server.rest.domain.JsonHelper;
import org.neo4j.tutorial.DoctorWhoLabels;
import org.neo4j.tutorial.DoctorWhoUniverseGenerator;
import org.neo4j.tutorial.ServerDoctorWhoUniverse;
import org.neo4j.tutorial.server.ServerBuilder;
import org.neo4j.tutorial.server.rest.BatchCommandBuilder;
import org.neo4j.tutorial.server.rest.RelationshipDescription;
import org.neo4j.tutorial.server.rest.TraversalDescription;
import org.neo4j.tutorial.server.rest.domain.EpisodeSearchResult;
import org.neo4j.tutorial.server.rest.domain.EpisodeSearchResults;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.helpers.collection.MapUtil.stringMap;
import static org.neo4j.tutorial.DoctorWhoRelationships.PLAYED;
import static org.neo4j.tutorial.server.rest.RelationshipDescription.IN;
import static org.neo4j.tutorial.server.rest.RelationshipDescription.OUT;

/**
 * In this Koan we use the default REST API to explore the Doctor Who universe.
 */
public class BasicRestApiFormerlyKoan10
{

    private static ServerDoctorWhoUniverse universe;

    @BeforeClass
    public static void createDatabase() throws Exception
    {
        DoctorWhoUniverseGenerator doctorWhoUniverseGenerator = new DoctorWhoUniverseGenerator();

        CommunityNeoServer server = ServerBuilder
                .server()
                .usingDatabaseDir( doctorWhoUniverseGenerator.getCleanlyShutdownDatabaseDirectory() )
                .build();

        universe = new ServerDoctorWhoUniverse( server );
    }

    @AfterClass
    public static void closeTheDatabase()
    {
        universe.stop();
    }

    @Test
    public void shouldCountTheEnemiesOfTheDoctor() throws Exception
    {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        String response = null;

        // YOUR CODE GOES HERE

        List<Map<String, Object>> json = JsonHelper.jsonToList( response );
        int numberOfEnemiesOfTheDoctor = 156;
        assertEquals( numberOfEnemiesOfTheDoctor, json.size() );
    }

    @Test
    public void shouldIdentifyWhichDoctorsTookPartInInvasionStories()
            throws Exception
    {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        ClientResponse response = null;
        TraversalDescription traversal = new TraversalDescription();

        // YOUR CODE GOES HERE

        String responseJson = response.getEntity( String.class );

        EpisodeSearchResults results = new EpisodeSearchResults( JsonHelper.jsonToList( responseJson ) );
        assertActorsAndInvasionEpisodes( results );
    }

    @Test
    public void canAddFirstAndSecondIncarnationInformationForTheDoctor()
    {

        // We'd like to update the model to add a new domain entity - "incarnation".
        // Timelords have one or more incarnations. In the TV series, an incarnation is played by one or
        // more actors (usually one). Here we're going to use the REST batch API to add a bit of this new
        // model. See the presentation for an example of the target graph structure.

        String PLAYED = "PLAYED";
        String INCARNATION_OF = "INCARNATION_OF";

        Map<String, Object> theDoctorJson = universe.theDoctor();
        String theDoctorUri = theDoctorJson.get( "self" ).toString();

        Map<String, Object> williamHartnellJson = universe.getJsonFor(
                universe.createUriForNode( "Actor", "actor", "William Hartnell" ) );
        Map<String, Object> richardHurndallJson = universe.getJsonFor(
                universe.createUriForNode( "Actor", "actor", "Richard Hurndall" ) );
        Map<String, Object> patrickTroughtonJson = universe.getJsonFor(
                universe.createUriForNode( "Actor", "actor", "Patrick Troughton" ) );

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create( config );

        BatchCommandBuilder cmds = new BatchCommandBuilder();

        // YOUR CODE GOES HERE

        assertFirstAndSecondDoctorCreatedAndLinkedToActors( universe.getServer().getDatabase().getGraph() );
    }

    private void assertActorsAndInvasionEpisodes( EpisodeSearchResults results )
    {

        Map<String, String> episodesAndActors = stringMap( "The Christmas Invasion", "David Tennant",
                "The Invasion of Time", "Tom Baker",
                "The Android Invasion", "Tom Baker",
                "Invasion of the Dinosaurs", "Jon Pertwee",
                "The Invasion", "Patrick Troughton",
                "The Dalek Invasion of Earth", "William Hartnell" );

        int count = 0;
        for ( EpisodeSearchResult result : results )
        {
            assertTrue( episodesAndActors.containsKey( result.getEpisode() ) );
            assertEquals( episodesAndActors.get( result.getEpisode() ), result.getActor() );
            count++;
        }

        assertEquals( episodesAndActors.keySet().size(), count );
    }

    private void assertFirstAndSecondDoctorCreatedAndLinkedToActors( GraphDatabaseService db )
    {
        try ( Transaction tx = db.beginTx() )
        {
            Node doctorNode = db.findNodesByLabelAndProperty( DoctorWhoLabels.CHARACTER, "character",
                    "Doctor" ).iterator().next();

            final PatternNode theDoctor = new PatternNode();
            theDoctor.addPropertyConstraint( "character", CommonValueMatchers.exact( "Doctor" ) );

            final PatternNode firstDoctor = new PatternNode();
            firstDoctor.addPropertyConstraint( "incarnation", CommonValueMatchers.exact( "First Doctor" ) );

            final PatternNode secondDoctor = new PatternNode();
            secondDoctor.addPropertyConstraint( "incarnation", CommonValueMatchers.exact( "Second Doctor" ) );

            final PatternNode williamHartnell = new PatternNode();
            williamHartnell.addPropertyConstraint( "actor", CommonValueMatchers.exact( "William Hartnell" ) );

            final PatternNode richardHurndall = new PatternNode();
            richardHurndall.addPropertyConstraint( "actor", CommonValueMatchers.exact( "Richard Hurndall" ) );

            final PatternNode patrickTroughton = new PatternNode();
            patrickTroughton.addPropertyConstraint( "actor", CommonValueMatchers.exact( "Patrick Troughton" ) );


            firstDoctor.createRelationshipTo( theDoctor, DynamicRelationshipType.withName( "INCARNATION_OF" ),
                    OUTGOING );
            secondDoctor.createRelationshipTo( theDoctor, DynamicRelationshipType.withName( "INCARNATION_OF" ),
                    OUTGOING );
            williamHartnell.createRelationshipTo( firstDoctor, PLAYED, OUTGOING );
            richardHurndall.createRelationshipTo( firstDoctor, PLAYED, OUTGOING );
            patrickTroughton.createRelationshipTo( secondDoctor, PLAYED, OUTGOING );

            PatternMatcher matcher = PatternMatcher.getMatcher();
            final Iterable<PatternMatch> matches = matcher.match( theDoctor, doctorNode );

            assertTrue( matches.iterator().hasNext() );

            tx.success();
        }
    }
}
