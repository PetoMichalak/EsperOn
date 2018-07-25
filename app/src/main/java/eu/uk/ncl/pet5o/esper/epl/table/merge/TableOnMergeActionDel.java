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
package eu.uk.ncl.pet5o.esper.epl.table.merge;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.epl.table.onaction.TableOnMergeViewChangeHandler;

public class TableOnMergeActionDel extends TableOnMergeAction {

    public TableOnMergeActionDel(ExprEvaluator optionalFilter) {
        super(optionalFilter);
    }

    public void apply(eu.uk.ncl.pet5o.esper.client.EventBean matchingEvent, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, TableStateInstance tableStateInstance, TableOnMergeViewChangeHandler changeHandlerAdded, TableOnMergeViewChangeHandler changeHandlerRemoved, ExprEvaluatorContext exprEvaluatorContext) {
        tableStateInstance.deleteEvent(matchingEvent);
        if (changeHandlerRemoved != null) {
            changeHandlerRemoved.add(matchingEvent, eventsPerStream, false, exprEvaluatorContext);
        }
    }

    public String getName() {
        return "delete";
    }
}
