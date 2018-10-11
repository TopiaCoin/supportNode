package io.topiacoin.node.utility;

import io.topiacoin.node.utilities.RelationshipMap;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/*
        a1  a2  a3  a4
        | \ | \ | \ | \
        |  \|  \|  \|  \
        b1  b2  b3  b4 (b1)
 */
public class RelationshipMapTest {

    @Test
    public void testAddingRelationships() {
        RelationshipMap relationshipMap = new RelationshipMap();

        String a1 = "a1" ;
        String a2 = "a2" ;
        String a3 = "a3" ;
        String a4 = "a4" ;

        String b1 = "b1";
        String b2 = "b2";
        String b3 = "b3";
        String b4 = "b4";

        assertTrue(relationshipMap.addRelationship(a1, b1));
        assertTrue(relationshipMap.addRelationship(a1, b2));
        assertTrue(relationshipMap.addRelationship(a2, b2));
        assertTrue(relationshipMap.addRelationship(a2, b3));
        assertTrue(relationshipMap.addRelationship(a3, b3));
        assertTrue(relationshipMap.addRelationship(a3, b4));
        assertTrue(relationshipMap.addRelationship(a4, b4));
        assertTrue(relationshipMap.addRelationship(a4, b1));

        // Verify that we cannot add duplicates
        assertFalse(relationshipMap.addRelationship(a1, b1));
        assertFalse(relationshipMap.addRelationship(a1, b2));
        assertFalse(relationshipMap.addRelationship(a2, b2));
        assertFalse(relationshipMap.addRelationship(a2, b3));
        assertFalse(relationshipMap.addRelationship(a3, b3));
        assertFalse(relationshipMap.addRelationship(a3, b4));
        assertFalse(relationshipMap.addRelationship(a4, b4));
        assertFalse(relationshipMap.addRelationship(a4, b1));

        // Check the Forward Relationships
        {
            Set<Object> relatedObjectsA1 = relationshipMap.getRelationships(a1);
            assertEquals(2, relatedObjectsA1.size());
            assertTrue(relatedObjectsA1.contains(b1));
            assertTrue(relatedObjectsA1.contains(b2));
        }

        {
            Set<Object> relatedObjectsA2 = relationshipMap.getRelationships(a2);
            assertEquals(2, relatedObjectsA2.size());
            assertTrue(relatedObjectsA2.contains(b2));
            assertTrue(relatedObjectsA2.contains(b3));
        }

        {
            Set<Object> relatedObjectsA3 = relationshipMap.getRelationships(a3);
            assertEquals(2, relatedObjectsA3.size());
            assertTrue(relatedObjectsA3.contains(b3));
            assertTrue(relatedObjectsA3.contains(b4));
        }

        {
            Set<Object> relatedObjectsA4 = relationshipMap.getRelationships(a4);
            assertEquals(2, relatedObjectsA4.size());
            assertTrue(relatedObjectsA4.contains(b4));
            assertTrue(relatedObjectsA4.contains(b1));
        }

        // Check the Reverse Relationships
        {
            Set<Object> relatedObjectsB1 = relationshipMap.getRelationships(b1);
            assertEquals(2, relatedObjectsB1.size());
            assertTrue(relatedObjectsB1.contains(a4));
            assertTrue(relatedObjectsB1.contains(a1));
        }

        {
            Set<Object> relatedObjectsB2 = relationshipMap.getRelationships(b2);
            assertEquals(2, relatedObjectsB2.size());
            assertTrue(relatedObjectsB2.contains(a1));
            assertTrue(relatedObjectsB2.contains(a2));
        }

        {
            Set<Object> relatedObjectsB3 = relationshipMap.getRelationships(b3);
            assertEquals(2, relatedObjectsB3.size());
            assertTrue(relatedObjectsB3.contains(a2));
            assertTrue(relatedObjectsB3.contains(a3));
        }

        {
            Set<Object> relatedObjectsB4 = relationshipMap.getRelationships(b4);
            assertEquals(2, relatedObjectsB4.size());
            assertTrue(relatedObjectsB4.contains(a3));
            assertTrue(relatedObjectsB4.contains(a4));
        }
    }

    @Test
    public void testRemovingRelationships() throws Exception {
        RelationshipMap relationshipMap = new RelationshipMap();

        String a1 = "a1" ;
        String a2 = "a2" ;
        String a3 = "a3" ;
        String a4 = "a4" ;

        String b1 = "b1";
        String b2 = "b2";
        String b3 = "b3";
        String b4 = "b4";

        relationshipMap.addRelationship(a1, b1);
        relationshipMap.addRelationship(a1, b2);
        relationshipMap.addRelationship(a2, b2);
        relationshipMap.addRelationship(a2, b3);
        relationshipMap.addRelationship(a3, b3);
        relationshipMap.addRelationship(a3, b4);
        relationshipMap.addRelationship(a4, b4);
        relationshipMap.addRelationship(a4, b1);

        assertTrue(relationshipMap.removeRelationship(a1, b1));
        assertTrue(relationshipMap.removeRelationship(a2, b2));
        assertTrue(relationshipMap.removeRelationship(a3, b3));
        assertTrue(relationshipMap.removeRelationship(a4, b4));

        assertFalse(relationshipMap.removeRelationship(a1, b1));
        assertFalse(relationshipMap.removeRelationship(a2, b2));
        assertFalse(relationshipMap.removeRelationship(a3, b3));
        assertFalse(relationshipMap.removeRelationship(a4, b4));

        // Check the Forward Relationships
        {
            Set<Object> relatedObjectsA1 = relationshipMap.getRelationships(a1);
            assertEquals(1, relatedObjectsA1.size());
            assertFalse(relatedObjectsA1.contains(b1));
            assertTrue(relatedObjectsA1.contains(b2));
        }

        {
            Set<Object> relatedObjectsA2 = relationshipMap.getRelationships(a2);
            assertEquals(1, relatedObjectsA2.size());
            assertFalse(relatedObjectsA2.contains(b2));
            assertTrue(relatedObjectsA2.contains(b3));
        }

        {
            Set<Object> relatedObjectsA3 = relationshipMap.getRelationships(a3);
            assertEquals(1, relatedObjectsA3.size());
            assertFalse(relatedObjectsA3.contains(b3));
            assertTrue(relatedObjectsA3.contains(b4));
        }

        {
            Set<Object> relatedObjectsA4 = relationshipMap.getRelationships(a4);
            assertEquals(1, relatedObjectsA4.size());
            assertFalse(relatedObjectsA4.contains(b4));
            assertTrue(relatedObjectsA4.contains(b1));
        }

        // Check the Reverse Relationships
        {
            Set<Object> relatedObjectsB1 = relationshipMap.getRelationships(b1);
            assertEquals(1, relatedObjectsB1.size());
            assertTrue(relatedObjectsB1.contains(a4));
            assertFalse(relatedObjectsB1.contains(a1));
        }

        {
            Set<Object> relatedObjectsB2 = relationshipMap.getRelationships(b2);
            assertEquals(1, relatedObjectsB2.size());
            assertTrue(relatedObjectsB2.contains(a1));
            assertFalse(relatedObjectsB2.contains(a2));
        }

        {
            Set<Object> relatedObjectsB3 = relationshipMap.getRelationships(b3);
            assertEquals(1, relatedObjectsB3.size());
            assertTrue(relatedObjectsB3.contains(a2));
            assertFalse(relatedObjectsB3.contains(a3));
        }

        {
            Set<Object> relatedObjectsB4 = relationshipMap.getRelationships(b4);
            assertEquals(1, relatedObjectsB4.size());
            assertTrue(relatedObjectsB4.contains(a3));
            assertFalse(relatedObjectsB4.contains(a4));
        }
    }

    @Test
    public void testRemoveAllReplationships() throws Exception {
        RelationshipMap relationshipMap = new RelationshipMap();

        String a1 = "a1" ;
        String a2 = "a2" ;
        String a3 = "a3" ;
        String a4 = "a4" ;

        String b1 = "b1";
        String b2 = "b2";
        String b3 = "b3";
        String b4 = "b4";

        relationshipMap.addRelationship(a1, b1);
        relationshipMap.addRelationship(a1, b2);
        relationshipMap.addRelationship(a2, b2);
        relationshipMap.addRelationship(a2, b3);
        relationshipMap.addRelationship(a3, b3);
        relationshipMap.addRelationship(a3, b4);
        relationshipMap.addRelationship(a4, b4);
        relationshipMap.addRelationship(a4, b1);

        Set<Object> a1Relationships = relationshipMap.removeAllRelationships(a1);
        Set<Object> a3Relationships = relationshipMap.removeAllRelationships(a3);

        assertNotNull(a1Relationships) ;
        assertEquals(2, a1Relationships.size()) ;
        assertTrue(a1Relationships.contains(b1)) ;
        assertTrue(a1Relationships.contains(b2)) ;

        assertNotNull(a3Relationships) ;
        assertEquals(2, a3Relationships.size()) ;
        assertTrue(a3Relationships.contains(b3)) ;
        assertTrue(a3Relationships.contains(b4)) ;

        // Check the Forward Relationships
        {
            Set<Object> relatedObjectsA1 = relationshipMap.getRelationships(a1);
            assertEquals(0, relatedObjectsA1.size()) ;
        }

        {
            Set<Object> relatedObjectsA2 = relationshipMap.getRelationships(a2);
            assertEquals(2, relatedObjectsA2.size());
            assertTrue(relatedObjectsA2.contains(b2));
            assertTrue(relatedObjectsA2.contains(b3));
        }

        {
            Set<Object> relatedObjectsA3 = relationshipMap.getRelationships(a3);
            assertEquals(0, relatedObjectsA3.size()) ;
        }

        {
            Set<Object> relatedObjectsA4 = relationshipMap.getRelationships(a4);
            assertEquals(2, relatedObjectsA4.size());
            assertTrue(relatedObjectsA4.contains(b4));
            assertTrue(relatedObjectsA4.contains(b1));
        }

        // Check the Reverse Relationships
        {
            Set<Object> relatedObjectsB1 = relationshipMap.getRelationships(b1);
            assertEquals(1, relatedObjectsB1.size());
            assertTrue(relatedObjectsB1.contains(a4));
            assertFalse(relatedObjectsB1.contains(a1));
        }

        {
            Set<Object> relatedObjectsB2 = relationshipMap.getRelationships(b2);
            assertEquals(1, relatedObjectsB2.size());
            assertFalse(relatedObjectsB2.contains(a1));
            assertTrue(relatedObjectsB2.contains(a2));
        }

        {
            Set<Object> relatedObjectsB3 = relationshipMap.getRelationships(b3);
            assertEquals(1, relatedObjectsB3.size());
            assertTrue(relatedObjectsB3.contains(a2));
            assertFalse(relatedObjectsB3.contains(a3));
        }

        {
            Set<Object> relatedObjectsB4 = relationshipMap.getRelationships(b4);
            assertEquals(1, relatedObjectsB4.size());
            assertFalse(relatedObjectsB4.contains(a3));
            assertTrue(relatedObjectsB4.contains(a4));
        }
    }

    @Test
    public void testAreRelated() throws Exception {
        RelationshipMap relationshipMap = new RelationshipMap();

        String a1 = "a1" ;
        String a2 = "a2" ;
        String a3 = "a3" ;
        String a4 = "a4" ;

        String b1 = "b1";
        String b2 = "b2";
        String b3 = "b3";
        String b4 = "b4";

        relationshipMap.addRelationship(a1, b1);
        relationshipMap.addRelationship(a1, b2);
        relationshipMap.addRelationship(a2, b2);
        relationshipMap.addRelationship(a2, b3);
        relationshipMap.addRelationship(a3, b3);
        relationshipMap.addRelationship(a3, b4);
        relationshipMap.addRelationship(a4, b4);
        relationshipMap.addRelationship(a4, b1);

        assertTrue(relationshipMap.areRelated(a1, b1)) ;
        assertTrue(relationshipMap.areRelated(a1, b2)) ;
        assertTrue(relationshipMap.areRelated(a2, b2)) ;
        assertTrue(relationshipMap.areRelated(a2, b3)) ;
        assertTrue(relationshipMap.areRelated(a3, b3)) ;
        assertTrue(relationshipMap.areRelated(a3, b4)) ;
        assertTrue(relationshipMap.areRelated(a4, b4)) ;
        assertTrue(relationshipMap.areRelated(a4, b1)) ;

        assertFalse(relationshipMap.areRelated(a1, b3));
        assertFalse(relationshipMap.areRelated(a1, b4));
        assertFalse(relationshipMap.areRelated(a2, b4));
        assertFalse(relationshipMap.areRelated(a2, b1));
        assertFalse(relationshipMap.areRelated(a3, b1));
        assertFalse(relationshipMap.areRelated(a3, b2));
        assertFalse(relationshipMap.areRelated(a4, b2));
        assertFalse(relationshipMap.areRelated(a4, b3));

        // Check that the associative relationship checks are also correct.
        assertTrue(relationshipMap.areRelated(b1, a1)) ;
        assertTrue(relationshipMap.areRelated(b2, a1)) ;
        assertTrue(relationshipMap.areRelated(b2, a2)) ;
        assertTrue(relationshipMap.areRelated(b3, a2)) ;
        assertTrue(relationshipMap.areRelated(b3, a3)) ;
        assertTrue(relationshipMap.areRelated(b4, a3)) ;
        assertTrue(relationshipMap.areRelated(b4, a4)) ;
        assertTrue(relationshipMap.areRelated(b1, a4)) ;

        assertFalse(relationshipMap.areRelated(b3, a1));
        assertFalse(relationshipMap.areRelated(b4, a1));
        assertFalse(relationshipMap.areRelated(b4, a2));
        assertFalse(relationshipMap.areRelated(b1, a2));
        assertFalse(relationshipMap.areRelated(b1, a3));
        assertFalse(relationshipMap.areRelated(b2, a3));
        assertFalse(relationshipMap.areRelated(b2, a4));
        assertFalse(relationshipMap.areRelated(b3, a4));
    }
}
