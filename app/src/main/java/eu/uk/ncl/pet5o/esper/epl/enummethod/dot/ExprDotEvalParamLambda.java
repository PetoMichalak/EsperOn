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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

import java.util.List;

public class ExprDotEvalParamLambda extends ExprDotEvalParam {

    private int streamCountIncoming;    // count of incoming streams
    private List<String> goesToNames;    // (x, y) => doSomething   .... parameter names are x and y
    private EventType[] goesToTypes;

    public ExprDotEvalParamLambda(int parameterNum, ExprNode body, ExprForge bodyEvaluator, int streamCountIncoming, List<String> goesToNames, EventType[] goesToTypes) {
        super(parameterNum, body, bodyEvaluator);
        this.streamCountIncoming = streamCountIncoming;
        this.goesToNames = goesToNames;
        this.goesToTypes = goesToTypes;
    }

    public int getStreamCountIncoming() {
        return streamCountIncoming;
    }

    public List<String> getGoesToNames() {
        return goesToNames;
    }

    public EventType[] getGoesToTypes() {
        return goesToTypes;
    }
}
