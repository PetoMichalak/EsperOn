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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class SubselectEvalStrategyRowUnfilteredUnselectedTable extends SubselectEvalStrategyRowUnfilteredUnselected {

    private static final Logger log = LoggerFactory.getLogger(SubselectEvalStrategyRowUnfilteredUnselectedTable.class);

    private final TableMetadata tableMetadata;

    public SubselectEvalStrategyRowUnfilteredUnselectedTable(TableMetadata tableMetadata) {
        this.tableMetadata = tableMetadata;
    }

    @Override
    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean newData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext,
                           ExprSubselectRowNode parent) {
        if (matchingEvents.size() > 1) {
            log.warn(parent.getMultirowMessage());
            return null;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean event = EventBeanUtility.getNonemptyFirstEvent(matchingEvents);
        return tableMetadata.getEventToPublic().convertToUnd(event, eventsPerStream, newData, exprEvaluatorContext);
    }
}
