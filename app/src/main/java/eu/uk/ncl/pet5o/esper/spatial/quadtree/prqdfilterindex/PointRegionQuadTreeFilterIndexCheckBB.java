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
package eu.uk.ncl.pet5o.esper.spatial.quadtree.prqdfilterindex;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.spatial.quadtree.core.BoundingBox;

public class PointRegionQuadTreeFilterIndexCheckBB {
    public static void checkBB(BoundingBox bb, double x, double y) throws EPException {
        if (!bb.containsPoint(x, y)) {
            throw new EPException("Point (" + x + "," + y + ") not in " + bb);
        }
    }
}
