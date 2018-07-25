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
package eu.uk.ncl.pet5o.esper.spatial.quadtree.pointregion;

import eu.uk.ncl.pet5o.esper.spatial.quadtree.core.BoundingBox;

public abstract class PointRegionQuadTreeNode<L> {
    private final BoundingBox bb;
    private final int level;

    public PointRegionQuadTreeNode(BoundingBox bb, int level) {
        this.bb = bb;
        this.level = level;
    }

    public BoundingBox getBb() {
        return bb;
    }

    public int getLevel() {
        return level;
    }
}
