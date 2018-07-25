/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.client.soda;

/**
 * Enumeration to represents the index type.
 */
public enum CreateIndexColumnType {
    /**
     * Hash-index.
     */
    HASH("hash"),

    /**
     * Binary-tree (sorted) index.
     */
    BTREE("btree");

    private final String nameLower;

    CreateIndexColumnType(String nameLower) {
        this.nameLower = nameLower;
    }

    /**
     * Returns the name (lowercase).
     * @return name
     */
    public String getNameLower() {
        return nameLower;
    }
}
