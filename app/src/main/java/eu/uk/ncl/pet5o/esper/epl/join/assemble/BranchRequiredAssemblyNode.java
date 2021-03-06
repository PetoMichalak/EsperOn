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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.join.rep.Node;
import eu.uk.ncl.pet5o.esper.util.IndentWriter;

import java.util.Collection;
import java.util.List;

/**
 * Assembly node for an event stream that is a branch with a single required child node below it.
 */
public class BranchRequiredAssemblyNode extends BaseAssemblyNode {
    /**
     * Ctor.
     *
     * @param streamNum  - is the stream number
     * @param numStreams - is the number of streams
     */
    public BranchRequiredAssemblyNode(int streamNum, int numStreams) {
        super(streamNum, numStreams);
    }

    public void init(List<Node>[] result) {
        // need not be concerned with results, all is passed from the child node
    }

    public void process(List<Node>[] result, Collection<EventBean[]> resultFinalRows, EventBean resultRootEvent) {
        // no action here, since we have a required child row
        // The single required child generates all events that may exist
    }

    public void result(EventBean[] row, int fromStreamNum, EventBean myEvent, Node myNode, Collection<EventBean[]> resultFinalRows, EventBean resultRootEvent) {
        row[streamNum] = myEvent;
        Node parentResultNode = myNode.getParent();
        parentNode.result(row, streamNum, myNode.getParentEvent(), parentResultNode, resultFinalRows, resultRootEvent);
    }

    public void print(IndentWriter indentWriter) {
        indentWriter.println("BranchRequiredAssemblyNode streamNum=" + streamNum);
    }
}
