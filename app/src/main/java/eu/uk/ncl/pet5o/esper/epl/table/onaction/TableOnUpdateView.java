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
package eu.uk.ncl.pet5o.esper.epl.table.onaction;

import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import com.espertech.esper.epl.spec.OnTriggerType;
import com.espertech.esper.epl.table.mgmt.TableMetadata;
import com.espertech.esper.epl.table.mgmt.TableStateInstance;
import com.espertech.esper.epl.table.upd.TableUpdateStrategy;
import com.espertech.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.Arrays;

public class TableOnUpdateView extends TableOnViewBase {

    private final TableOnUpdateViewFactory parent;

    public TableOnUpdateView(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance rootView, ExprEvaluatorContext exprEvaluatorContext, TableMetadata metadata, TableOnUpdateViewFactory parent) {
        super(lookupStrategy, rootView, exprEvaluatorContext, metadata, true);
        this.parent = parent;
    }

    public void handleMatching(com.espertech.esper.client.EventBean[] triggerEvents, com.espertech.esper.client.EventBean[] matchingEvents) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qInfraOnAction(OnTriggerType.ON_UPDATE, triggerEvents, matchingEvents);
        }

        com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[3];

        boolean postUpdates = parent.getStatementResultService().isMakeNatural() || parent.getStatementResultService().isMakeSynthetic();
        com.espertech.esper.client.EventBean[] postedOld = null;
        if (postUpdates) {
            postedOld = TableOnViewUtil.toPublic(matchingEvents, parent.getTableMetadata(), triggerEvents, false, super.getExprEvaluatorContext());
        }

        TableUpdateStrategy tableUpdateStrategy = parent.getTableUpdateStrategy();

        for (com.espertech.esper.client.EventBean triggerEvent : triggerEvents) {
            eventsPerStream[1] = triggerEvent;
            tableUpdateStrategy.updateTable(Arrays.asList(matchingEvents), tableStateInstance, eventsPerStream, exprEvaluatorContext);
        }

        // The on-delete listeners receive the events deleted, but only if there is interest
        if (postUpdates) {
            com.espertech.esper.client.EventBean[] postedNew = TableOnViewUtil.toPublic(matchingEvents, parent.getTableMetadata(), triggerEvents, true, super.getExprEvaluatorContext());
            updateChildren(postedNew, postedOld);
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aInfraOnAction();
        }
    }
}
