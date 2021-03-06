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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.core;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Processor for result sets coming from 2 sources. First, out of a simple view (no join).
 * And second, out of a join of event streams. The processor must apply the select-clause, grou-by-clause and having-clauses
 * as supplied. It must state what the event type of the result rows is.
 */
public interface ResultSetProcessor extends StopCallback {
    /**
     * Returns the event type of processed results.
     *
     * @return event type of the resulting events posted by the processor.
     */
    public EventType getResultEventType();

    /**
     * For use by views posting their result, process the event rows that are entered and removed (new and old events).
     * Processes according to select-clauses, group-by clauses and having-clauses and returns new events and
     * old events as specified.
     *
     * @param newData      - new events posted by view
     * @param oldData      - old events posted by view
     * @param isSynthesize - set to true to indicate that synthetic events are required for an iterator result set
     * @return pair of new events and old events
     */
    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isSynthesize);

    /**
     * For use by joins posting their result, process the event rows that are entered and removed (new and old events).
     * Processes according to select-clauses, group-by clauses and having-clauses and returns new events and
     * old events as specified.
     *
     * @param newEvents    - new events posted by join
     * @param oldEvents    - old events posted by join
     * @param isSynthesize - set to true to indicate that synthetic events are required for an iterator result set
     * @return pair of new events and old events
     */
    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isSynthesize);

    /**
     * Returns the iterator implementing the group-by and aggregation and order-by logic
     * specific to each case of use of these construct.
     *
     * @param parent is the parent view iterator
     * @return event iterator
     */
    public Iterator<EventBean> getIterator(Viewable parent);

    /**
     * Returns the iterator for iterating over a join-result.
     *
     * @param joinSet is the join result set
     * @return iterator over join results
     */
    public Iterator<EventBean> getIterator(Set<MultiKey<EventBean>> joinSet);

    /**
     * Clear out current state.
     */
    public void clear();

    /**
     * Processes batched events in case of output-rate limiting.
     *
     * @param joinEventsSet     the join results
     * @param generateSynthetic flag to indicate whether synthetic events must be generated
     * @return results for dispatch
     */
    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinEventsSet, boolean generateSynthetic);

    /**
     * Processes batched events in case of output-rate limiting.
     *
     * @param viewEventsList    the view results
     * @param generateSynthetic flag to indicate whether synthetic events must be generated
     * @return results for dispatch
     */
    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> processOutputLimitedView(List<UniformPair<EventBean[]>> viewEventsList, boolean generateSynthetic);

    public void setAgentInstanceContext(AgentInstanceContext context);

    public void applyViewResult(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData);

    public void applyJoinResult(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents);

    public void processOutputLimitedLastAllNonBufferedView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic);

    public void processOutputLimitedLastAllNonBufferedJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic);

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedView(boolean isSynthesize);

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastAllNonBufferedJoin(boolean isSynthesize);

    public void acceptHelperVisitor(ResultSetProcessorOutputHelperVisitor visitor);
}
