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
package eu.uk.ncl.pet5o.esper.epl.join.assemble;

import eu.uk.ncl.pet5o.esper.util.IndentWriter;

/**
 * Assembly factory node for an event stream that is a root with a one optional child node below it.
 */
public class RootOptionalAssemblyNodeFactory extends BaseAssemblyNodeFactory {
    public RootOptionalAssemblyNodeFactory(int streamNum, int numStreams) {
        super(streamNum, numStreams);
    }

    public void print(IndentWriter indentWriter) {
        indentWriter.println("RootOptionalAssemblyNode streamNum=" + streamNum);
    }

    public BaseAssemblyNode makeAssemblerUnassociated() {
        return new RootOptionalAssemblyNode(streamNum, numStreams);
    }
}
