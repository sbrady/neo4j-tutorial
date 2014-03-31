package org.neo4j.tutorial;

import org.junit.Test;
import scala.collection.convert.Wrappers;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.neo4j.kernel.impl.util.StringLogger.DEV_NULL;

/**
 * In this Koan we learn how to create, update, and erase properties and labels in Cypher.
 */
public class Koan4
{
    @Test
    public void shouldCreateAnUnlabelledNodeWithActorPropertyToRepresentDavidTennant()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );

        String cql = null;

        // YOUR CODE GOES HERE

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a.actor" );

        assertEquals( "David Tennant", executionResult.javaColumnAs( "a.actor" ).next() );
    }

    @Test
    public void shouldAddOriginalNamePropertyForDavidTennantNode()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );

        engine.execute( "CREATE ({actor: 'David Tennant'}) " );

        String cql = "MATCH (a {actor: 'David Tennant'})\n";

        // YOUR CODE GOES HERE

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a" +
                ".original_name" );

        assertEquals( "David McDonald", executionResult.javaColumnAs( "a.original_name" ).next() );
    }

    @Test
    public void shouldChangeOriginalNamePropertyForDavidTennantNodeToSomethingComical()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );

        engine.execute( "CREATE ({actor: 'David Tennant', original_name: 'David McDonald'}) " );

        String cql = "MATCH (a {actor: 'David Tennant'})\n";

        // YOUR CODE GOES HERE

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a" +
                ".original_name" );

        assertEquals( "Ronald McDonald", executionResult.javaColumnAs( "a.original_name" ).next() );
    }


    @Test
    public void shouldCreateAnActorLabelledNodeRepresentingDavidTennant()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );


        String cql = null;

        // YOUR CODE GOES HERE

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a:Actor {actor: 'David Tennant'}) RETURN a.actor" );

        assertEquals( "David Tennant", executionResult.javaColumnAs( "a.actor" ).next() );
    }

    @Test
    public void shouldAddScottishNationalityLabelToAnExistingDavidTennantNode()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );

        engine.execute( "CREATE (:Actor {actor: 'David Tennant'})" );

        String cql = "MATCH (a {actor: 'David Tennant'})\n";

        // YOUR CODE GOES HERE

        cql += "SET a:Scottish";

        // SNIPPET_END

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a:Scottish {actor: 'David Tennant'}) RETURN " +
                "labels(a)" );

        Wrappers.SeqWrapper wrapper = (Wrappers.SeqWrapper) executionResult.javaColumnAs( "labels(a)" ).next();
        assertTrue( wrapper.contains( "Scottish" ) );
    }

    @Test
    public void shouldAddActorMaleAndScottishLabelsToAnExistingDavidTennantNode()
    {
        ExecutionEngine engine = new ExecutionEngine( DatabaseHelper.createDatabase(), DEV_NULL );

        engine.execute( "CREATE (:Actor {actor: 'David Tennant'})" );

        String cql = "MATCH (a:Actor {actor: 'David Tennant'})\n";

        // YOUR CODE GOES HERE

        engine.execute( cql );

        final ExecutionResult executionResult = engine.execute( "MATCH (a {actor: 'David Tennant'}) RETURN labels(a)" );

        Wrappers.SeqWrapper wrapper = (Wrappers.SeqWrapper) executionResult.javaColumnAs( "labels(a)" ).next();
        assertTrue( wrapper.contains( "Male" ) );
        assertTrue( wrapper.contains( "Actor" ) );
        assertTrue( wrapper.contains( "Scottish" ) );
    }
}
