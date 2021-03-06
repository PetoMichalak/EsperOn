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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Set;

public class ResultSetProcessorHandThroughUtil {
    public final static String METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUVIEW = "getSelectEventsNoHavingHandThruView";
    public final static String METHOD_GETSELECTEVENTSNOHAVINGHANDTHRUJOIN = "getSelectEventsNoHavingHandThruJoin";

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param agentInstanceContext context
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsNoHavingHandThruView(SelectExprProcessor exprProcessor, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext agentInstanceContext) {
        if (events == null) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[events.length];
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        for (int i = 0; i < events.length; i++) {
            eventsPerStream[0] = events[i];
            result[i] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, agentInstanceContext);
        }

        return result;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * Applies the select-clause to the given events returning the selected events. The number of events stays the
     * same, i.e. this method does not filter it just transforms the result set.
     *
     * @param exprProcessor        - processes each input event and returns output event
     * @param events               - input events
     * @param isNewData            - indicates whether we are dealing with new data (istream) or old data (rstream)
     * @param isSynthesize         - set to true to indicate that synthetic events are required for an iterator result set
     * @param agentInstanceContext context
     * @return output events, one for each input event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getSelectEventsNoHavingHandThruJoin(SelectExprProcessor exprProcessor, Set<MultiKey<EventBean>> events, boolean isNewData, boolean isSynthesize, ExprEvaluatorContext agentInstanceContext) {
        int length = events.size();
        if (length == 0) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[length];
        int count = 0;
        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> key : events) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = key.getArray();
            result[count] = exprProcessor.process(eventsPerStream, isNewData, isSynthesize, agentInstanceContext);
            count++;
        }

        return result;
    }
}
