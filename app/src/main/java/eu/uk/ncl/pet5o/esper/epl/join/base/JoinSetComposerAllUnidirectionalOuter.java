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
package eu.uk.ncl.pet5o.esper.epl.join.base;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.context.factory.StatementAgentInstancePostLoadIndexVisitor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implements the function to determine a join result for a all-unidirectional full-outer-join (all streams),
 * in which a single stream's events are ever only evaluated and repositories don't exist.
 */
public class JoinSetComposerAllUnidirectionalOuter implements JoinSetComposer {
    private final QueryStrategy[] queryStrategies;

    private Set<MultiKey<EventBean>> emptyResults = new LinkedHashSet<MultiKey<EventBean>>();
    private Set<MultiKey<EventBean>> newResults = new LinkedHashSet<MultiKey<EventBean>>();

    public JoinSetComposerAllUnidirectionalOuter(QueryStrategy[] queryStrategies) {
        this.queryStrategies = queryStrategies;
    }

    public boolean allowsInit() {
        return false;
    }

    public void init(EventBean[][] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
    }

    public void destroy() {
    }

    public UniformPair<Set<MultiKey<EventBean>>> join(EventBean[][] newDataPerStream, EventBean[][] oldDataPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qJoinCompositionStreamToWin();
        }
        newResults.clear();

        for (int i = 0; i < queryStrategies.length; i++) {
            if (newDataPerStream[i] != null) {
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().qJoinCompositionQueryStrategy(true, i, newDataPerStream[i]);
                }
                queryStrategies[i].lookup(newDataPerStream[i], newResults, exprEvaluatorContext);
                if (InstrumentationHelper.ENABLED) {
                    InstrumentationHelper.get().aJoinCompositionQueryStrategy();
                }
            }
        }

        return new UniformPair<>(newResults, emptyResults);
    }

    public Set<MultiKey<EventBean>> staticJoin() {
        throw new UnsupportedOperationException("Iteration over a unidirectional join is not supported");
    }

    public void visitIndexes(StatementAgentInstancePostLoadIndexVisitor visitor) {
    }
}
