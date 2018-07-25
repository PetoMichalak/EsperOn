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
package eu.uk.ncl.pet5o.esper.spatial.quadtree.mxciffilterindex;

import eu.uk.ncl.pet5o.esper.spatial.quadtree.mxcif.MXCIFQuadTree;
import eu.uk.ncl.pet5o.esper.spatial.quadtree.mxcif.MXCIFQuadTreeNode;
import eu.uk.ncl.pet5o.esper.spatial.quadtree.mxcif.MXCIFQuadTreeNodeLeaf;

public class MXCIFQuadTreeFilterIndexEmpty {
    public static boolean isEmpty(MXCIFQuadTree<Object> quadTree) {
        return isEmpty(quadTree.getRoot());
    }

    public static boolean isEmpty(MXCIFQuadTreeNode<Object> node) {
        if (node instanceof MXCIFQuadTreeNodeLeaf) {
            MXCIFQuadTreeNodeLeaf<Object> leaf = (MXCIFQuadTreeNodeLeaf<Object>) node;
            return leaf.getData() == null;
        }
        return false;
    }
}
