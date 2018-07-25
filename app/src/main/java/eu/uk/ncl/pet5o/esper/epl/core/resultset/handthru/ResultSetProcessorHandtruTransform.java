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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru;

import eu.uk.ncl.pet5o.esper.collection.TransformEventMethod;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;

/**
 * Method to transform an event based on the select expression.
 */
public class ResultSetProcessorHandtruTransform implements TransformEventMethod {
    private final ResultSetProcessor resultSetProcessor;
    private final eu.uk.ncl.pet5o.esper.client.EventBean[] newData;

    /**
     * Ctor.
     *
     * @param resultSetProcessor is applying the select expressions to the events for the transformation
     */
    public ResultSetProcessorHandtruTransform(ResultSetProcessor resultSetProcessor) {
        this.resultSetProcessor = resultSetProcessor;
        newData = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean transform(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        newData[0] = theEvent;
        UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair = resultSetProcessor.processViewResult(newData, null, true);
        return pair == null ? null : (pair.getFirst() == null ? null : pair.getFirst()[0]);
    }
}
