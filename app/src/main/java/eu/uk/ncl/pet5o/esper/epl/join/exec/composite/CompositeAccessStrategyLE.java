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
package eu.uk.ncl.pet5o.esper.epl.join.exec.composite;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CompositeAccessStrategyLE extends CompositeAccessStrategyRelOpBase implements CompositeAccessStrategy {

    public CompositeAccessStrategyLE(boolean isNWOnTrigger, int lookupStream, int numStreams, ExprEvaluator key, Class coercionType) {
        super(isNWOnTrigger, lookupStream, numStreams, key, coercionType);
    }

    public Set<EventBean> lookup(EventBean theEvent, Map parent, Set<EventBean> result, CompositeIndexQuery next, ExprEvaluatorContext context, ArrayList<Object> optionalKeyCollector, CompositeIndexQueryResultPostProcessor postProcessor) {
        TreeMap index = (TreeMap) parent;
        Object comparable = super.evaluateLookup(theEvent, context);
        if (optionalKeyCollector != null) {
            optionalKeyCollector.add(comparable);
        }
        if (comparable == null) {
            return null;
        }
        comparable = EventBeanUtility.coerce(comparable, coercionType);
        return CompositeIndexQueryRange.handle(theEvent, index.headMap(comparable, true), null, result, next, postProcessor);
    }

    public Collection<EventBean> lookup(EventBean[] eventPerStream, Map parent, Collection<EventBean> result, CompositeIndexQuery next, ExprEvaluatorContext context, ArrayList<Object> optionalKeyCollector, CompositeIndexQueryResultPostProcessor postProcessor) {
        TreeMap index = (TreeMap) parent;
        Object comparable = super.evaluatePerStream(eventPerStream, context);
        if (optionalKeyCollector != null) {
            optionalKeyCollector.add(comparable);
        }
        if (comparable == null) {
            return null;
        }
        comparable = EventBeanUtility.coerce(comparable, coercionType);
        return CompositeIndexQueryRange.handle(eventPerStream, index.headMap(comparable, true), null, result, next, postProcessor);
    }
}
