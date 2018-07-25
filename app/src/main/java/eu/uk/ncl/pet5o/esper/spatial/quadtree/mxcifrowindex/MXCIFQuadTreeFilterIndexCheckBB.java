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
package eu.uk.ncl.pet5o.esper.spatial.quadtree.mxcifrowindex;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.spatial.quadtree.core.BoundingBox;

public class MXCIFQuadTreeFilterIndexCheckBB {
    public static void checkBB(BoundingBox bb, double x, double y, double width, double height) throws EPException {
        if (!bb.intersectsBoxIncludingEnd(x, y, width, height)) {
            throw new EPException("Rectangle (" + x + "," + y + "," + width + "," + height + ") not in " + bb);
        }
    }
}
