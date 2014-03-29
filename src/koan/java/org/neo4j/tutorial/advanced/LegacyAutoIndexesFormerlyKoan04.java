package org.neo4j.tutorial.advanced;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.tutorial.DoctorWhoUniverseGenerator;
import org.neo4j.tutorial.EmbeddedDoctorWhoUniverse;

import static org.junit.Assert.assertThat;

import static org.neo4j.tutorial.matchers.CharacterAutoIndexContainsSpecificCharacters.containsSpecificCharacters;

/**
 * After having done the hard work of managing an index for ourselves in the
 * previous Koan, this Koan will introduce auto-indexing which, in exchange for
 * following some conventions, will handle the lifefcycle of nodes and
 * relationships in the indexes automatically.
 */

@Ignore("Convert this into a legacy auto index test towards the end of the koans, or better still consider deleting")
public class LegacyAutoIndexesFormerlyKoan04
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
    public void shouldCreateAnAutoIndexForAllTheCharacters()
    {
        GraphDatabaseService database = universe.getDatabase();
        Set<String> allCharacterNames = getAllCharacterNames();
        AutoIndexer<Node> charactersAutoIndex = null;

        // YOUR CODE GOES HERE

        try ( Transaction tx = database.beginTx() )
        {
            for ( String characterName : allCharacterNames )
            {
                Node n = database
                        .createNode();
                n.setProperty( "character", characterName );
            }

            assertThat( charactersAutoIndex, containsSpecificCharacters( allCharacterNames ) );
            tx.success();
        }
    }

    private Set<String> getAllCharacterNames()
    {
        try ( Transaction tx = universe.getDatabase().beginTx() )
        {
            Index<Node> characters = universe.getDatabase()
                    .index()
                    .forNodes( "characters" );
            IndexHits<Node> results = characters.query( "character", "*" );

            HashSet<String> characterNames = new HashSet<>();

            for ( Node character : results )
            {
                characterNames.add( (String) character.getProperty( "character" ) );
            }

            tx.success();
            return characterNames;
        }
    }
}
