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
package eu.uk.ncl.pet5o.esper.epl.expression.subquery;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.Collection;

public class SubselectEvalStrategyRowFilteredUnselectedTable extends SubselectEvalStrategyRowFilteredUnselected {

    private final TableMetadata tableMetadata;

    public SubselectEvalStrategyRowFilteredUnselectedTable(TableMetadata tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, ExprSubselectRowNode parent) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsZeroBased = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        eu.uk.ncl.pet5o.esper.client.EventBean subSelectResult = ExprSubselectRowNodeUtility.evaluateFilterExpectSingleMatch(eventsZeroBased, newData, matchingEvents, exprEvaluatorContext, parent);
        if (subSelectResult == null) {
            return null;
        }
        return tableMetadata.getEventToPublic().convertToUnd(subSelectResult, eventsPerStream, newData, exprEvaluatorContext);
    }
}
