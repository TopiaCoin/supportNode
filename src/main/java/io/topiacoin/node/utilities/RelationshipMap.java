package io.topiacoin.node.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tracks relationships between objects.  The relationships can be added, removed, retrieved, and checked as necessary.
 * This class maintains strong references to all of the related objects it is tracking.
 */
public final class RelationshipMap {

    private Map<Object, Set<Object>> _relationshipMap;

    /**
     * Constructs a new Relationship Map with an empty set of relationships.
     */
    public RelationshipMap() {
        _relationshipMap = new HashMap<>();
    }

    /**
     * Adds a relationship between two objects. A relationship between object <code>a</code> and object <code>b</code>
     * we be established. This relationship is bidirectional and will also establish that <code>b</code> is related top
     * <code>a</code>.
     *
     * @param a The first object in the relationship.
     * @param b The second object in the relationship.
     */
    public boolean addRelationship(Object a, Object b) {

        boolean added = false ;

        // Update the Forward Relationships
        Set<Object> forwardRelationships = _relationshipMap.computeIfAbsent(a, o -> new HashSet<>());
        added |= forwardRelationships.add(b);

        // Update the Reverse Relationships
        Set<Object> reverseRelationships = _relationshipMap.computeIfAbsent(b, o -> new HashSet<>());
        added |= reverseRelationships.add(a);

        return added;
    }

    /**
     * Removes a relationship between two objects.  The relationship between object <code>a</code> and object
     * <code>b</code> will be removed.  This will also remove the reverse relationship as well.  The method will return
     * true if the relationship was removed, or false if the relationship did not exist.
     *
     * @param a The first object in the relationship.
     * @param b The second object in the relationship.
     *
     * @return True if the relationship between the objects was removed, false if there was no existing relationship
     * between the objects.
     */
    public boolean removeRelationship(Object a, Object b) {
        boolean removed = false;

        // Update the Forward Relationships
        Set<Object> forwardRelationships = _relationshipMap.get(a);
        if (forwardRelationships != null) {
            removed |= forwardRelationships.remove(b);
        }

        // Update the Reverse Relationships
        Set<Object> reverseRelationships = _relationshipMap.get(b);
        if (reverseRelationships != null) {
            removed |= reverseRelationships.remove(a);
        }

        return removed;
    }

    /**
     * Removes all relationships for an object.  All relationships between object <code>a</code> will be removed.
     * This will also remove the reverse relationships between other objects and this object.
     *
     * @param a The object whose relationships are being removed.
     */
    public Set<Object> removeAllRelationships(Object a) {
        // Update the Relationships Map to remove all the relationships of a
        Set<Object> forwardRelationships = _relationshipMap.remove(a);
        if ( forwardRelationships == null ) {
            return Collections.emptySet();
        }

        // Update all the Reverse Relationships to remove a.
        for (Object b : forwardRelationships) {

            Set<Object> reverseRelationships = _relationshipMap.get(b);
            if (reverseRelationships != null) {

                reverseRelationships.remove(a);

                // If there are no more relationships in the list for B, remove it from the relationship map.
                if (reverseRelationships.size() == 0) {
                    _relationshipMap.remove(b);
                }
            } else {
                System.err.println("Did not find expected reverse relationship list");
            }
        }

        return forwardRelationships;
    }

    /**
     * Returns a set of all the objects to which the specified object is related.  The returned set is unordered.
     *
     * @param a The object whose relationships are being retrieved.
     *
     * @return A Set containing all of the objects related to the specified object.
     */
    public Set<Object> getRelationships(Object a) {
        Set<Object> objects = _relationshipMap.get(a);
        if ( objects == null ) {
            objects = Collections.emptySet();
        }
        return objects;
    }

    /**
     * Returns whether the two specified objects are related to one another.
     *
     * @param a The first object in the relationship check.
     * @param b The second object in the relationship check.
     *
     * @return True if there is a relationship between the objects. False if the objects are not related.
     */
    public boolean areRelated(Object a, Object b) {
        boolean related = false;
        Set<Object> forwardRelationships = _relationshipMap.get(a);
        if (forwardRelationships != null) {
            related = forwardRelationships.contains(b);
        }

        return related;
    }

}
