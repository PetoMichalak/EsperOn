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
package eu.uk.ncl.pet5o.esper.dataflow.interfaces;

import com.espertech.esper.dataflow.util.GraphTypeDesc;

public class DataFlowOpInitializeResult {

    private GraphTypeDesc[] typeDescriptors;

    public DataFlowOpInitializeResult() {
    }

    public DataFlowOpInitializeResult(GraphTypeDesc[] typeDescriptors) {
        this.typeDescriptors = typeDescriptors;
    }

    public GraphTypeDesc[] getTypeDescriptors() {
        return typeDescriptors;
    }
}
