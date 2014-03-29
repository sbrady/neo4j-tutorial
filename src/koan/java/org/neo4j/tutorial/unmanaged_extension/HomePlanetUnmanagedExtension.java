package org.neo4j.tutorial.unmanaged_extension;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.sun.jersey.api.NotFoundException;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

import static java.lang.System.lineSeparator;

import static org.neo4j.kernel.impl.util.StringLogger.DEV_NULL;


@Path("/{character}")
public class HomePlanetUnmanagedExtension
{
    // YOUR CODE GOES HERE
}
