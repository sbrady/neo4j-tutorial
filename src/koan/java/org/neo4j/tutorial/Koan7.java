package org.neo4j.tutorial;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import static org.neo4j.helpers.collection.IteratorUtil.asIterable;
import static org.neo4j.kernel.impl.util.StringLogger.DEV_NULL;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificStrings.containsOnlySpecificStrings;

/**
 * In this Koan we focus on aggregate functions from the Cypher graph pattern matching language
 * to process some statistics about the Doctor Who universe.
 */
public class Koan7
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
    public void shouldCountTheNumberOfActorsKnownToHavePlayedTheDoctor()
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        cql = "MATCH(:Character{character : 'Doctor' })<-[:PLAYED]-(actor:Actor)" +
                " RETURN count(actor) AS numberOfActorsWhoPlayedTheDoctor";
        ExecutionResult result = engine.execute( cql );

        Long actorsCount = (Long) result.javaColumnAs( "numberOfActorsWhoPlayedTheDoctor" ).next();

        assertEquals( (long) 13, actorsCount.longValue() );
    }

    @Test
    public void shouldFindEarliestAndLatestRegenerationYears()
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE

        cql = "MATCH(:Character{character : 'Doctor' })<-[:PLAYED]-(:Actor)-[r:REGENERATED_TO]->()" +
                "RETURN max(r.year) AS latest, min(r.year) AS earliest";

        ExecutionResult result = engine.execute( cql );

        Map<String, Object> map = result.javaIterator().next();
        assertEquals( 2013l, map.get( "latest" ) );
        assertEquals( 1966l, map.get( "earliest" ) );
    }

    @Test
    public void shouldFindTheEarliestEpisodeWhereFreemaAgyemanAndDavidTennantWorkedTogether() throws Exception
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE

        cql = "Match (:Actor{actor : 'Freema Agyeman'})-[:PLAYED]->()-[:APPEARED_IN]->(e:Episode)" +
                "<-[:APPEARED_IN]-(:Actor{actor : 'David Tennant'})" +
                " RETURN min(e.episode) AS earliest";

        ExecutionResult result = engine.execute( cql );

        assertEquals( "177", result.javaColumnAs( "earliest" ).next() );
    }

    @Test
    public void shouldFindAverageSalaryOfActorsWhoPlayedTheDoctor()
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE
        cql = "MATCH (c:Character{character : 'Doctor'})<-[:PLAYED]-(actor:Actor)" +
                " RETURN avg(actor.salary) AS cash";

        ExecutionResult result = engine.execute( cql );

        assertEquals( 600000.0, result.javaColumnAs( "cash" ).next() );
    }

    @Test
    public void shouldListTheEnemySpeciesAndCharactersForEachEpisodeWithPeterDavisonOrderedByIncreasingEpisodeNumber()
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE

        cql = "MATCH (episode:Episode)<-[:APPEARED_IN]-(:Actor{actor : 'Peter Davison'})-[:PLAYED]->(doc:Character)" +
                "<-[:ENEMY_OF]-(enemy)-[:APPEARED_IN]->(episode)" +
                " RETURN episode.episode, episode.title, collect(enemy.species) AS species, collect(enemy.character) AS characters" +
                " ORDER BY episode.episode" ;

        ExecutionResult result = engine.execute( cql );

        final List<String> columnNames = result.javaColumns();
        assertThat( columnNames,
                containsOnlySpecificStrings( "episode.episode", "episode.title", "species", "characters" ) );

        assertDavisonEpisodesRetrievedCorrectly( result.javaIterator() );
    }

    @Test
    public void shouldFindTheEnemySpeciesThatRoseTylerFought()
    {
        ExecutionEngine engine = new ExecutionEngine( universe.getDatabase(), DEV_NULL );
        String cql = null;

        // YOUR CODE GOES HERE
        cql = "MATCH (:Character{character : 'Rose Tyler'})-[:APPEARED_IN]->(episode:Episode)," +
                "(:Character{character : 'Doctor'})-[:ENEMY_OF]->(enemy:Species)-[:APPEARED_IN]->(episode)" +
                "RETURN DISTINCT enemy.species AS enemySpecies";

        ExecutionResult result = engine.execute( cql );

        Iterator<String> enemySpecies = result.javaColumnAs( "enemySpecies" );

        assertThat( asIterable( enemySpecies ),
                containsOnlySpecificStrings( "Krillitane", "Sycorax", "Cyberman", "Dalek", "Auton", "Slitheen",
                        "Clockwork Android" ) );

    }

    @SuppressWarnings("unchecked")
    private void assertDavisonEpisodesRetrievedCorrectly( Iterator<Map<String, Object>> iterator )
    {
        Map<String, Object> next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "116", next.get( "episode.episode" ) );
        assertEquals( "Castrovalva", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Monarch" ), next.get( "characters" ) );
        assertEquals( "117", next.get( "episode.episode" ) );
        assertEquals( "Four to Doomsday", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Mara" ), next.get( "characters" ) );
        assertEquals( "118", next.get( "episode.episode" ) );
        assertEquals( "Kinda", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Terileptils" ), next.get( "characters" ) );
        assertEquals( "119", next.get( "episode.episode" ) );
        assertEquals( "The Visitation", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "George Cranleigh" ), next.get( "characters" ) );
        assertEquals( "120", next.get( "episode.episode" ) );
        assertEquals( "Black Orchid", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Cyberman" ), next.get( "species" ) );
        assertEquals( "121", next.get( "episode.episode" ) );
        assertEquals( "Earthshock", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "122", next.get( "episode.episode" ) );
        assertEquals( "Time-Flight", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Omega" ), next.get( "characters" ) );
        assertEquals( "123", next.get( "episode.episode" ) );
        assertEquals( "Arc of Infinity", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Mara" ), next.get( "characters" ) );
        assertEquals( "124", next.get( "episode.episode" ) );
        assertEquals( "Snakedance", next.get( "episode.title" ) );

        next = iterator.next();
        final List chars = (List) next.get( "characters" );
        assertTrue( chars.contains( "Mawdryn" ) );
        assertTrue( chars.contains( "Black Guardian" ) );
        assertEquals( "125", next.get( "episode.episode" ) );
        assertEquals( "Mawdryn Undead", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Vanir" ), next.get( "characters" ) );
        assertEquals( "126", next.get( "episode.episode" ) );
        assertEquals( "Terminus", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Black Guardian" ), next.get( "characters" ) );
        assertEquals( "127", next.get( "episode.episode" ) );
        assertEquals( "Enlightenment", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "128", next.get( "episode.episode" ) );
        assertEquals( "The King's Demons", next.get( "episode.title" ) );

        next = iterator.next();
        assertThat( (Iterable<String>) next.get( "species" ), hasItems( "Dalek" ) );
        assertThat( (Iterable<String>) next.get( "characters" ), hasItems( "Master" ) );
        assertEquals( "129", next.get( "episode.episode" ) );
        assertEquals( "The Five Doctors", next.get( "episode.title" ) );

        next = iterator.next();
        assertThat( (Iterable<String>) next.get( "species" ), hasItems( "Sea Devil", "Silurian" ) );
        assertEquals( "130", next.get( "episode.episode" ) );
        assertEquals( "Warriors of the Deep", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Malus" ), next.get( "characters" ) );
        assertEquals( "131", next.get( "episode.episode" ) );
        assertEquals( "The Awakening", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Tractator" ), next.get( "species" ) );
        assertEquals( "132", next.get( "episode.episode" ) );
        assertEquals( "Frontios", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Dalek" ), next.get( "species" ) );
        assertEquals( "133", next.get( "episode.episode" ) );
        assertEquals( "Resurrection of the Daleks", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "134", next.get( "episode.episode" ) );
        assertEquals( "Planet of Fire", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "135", next.get( "episode.episode" ) );
        assertEquals( "The Caves of Androzani", next.get( "episode.title" ) );
    }
}
