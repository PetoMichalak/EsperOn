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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.lookup.SubordWMatchExprLookupStrategy;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerType;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableStateInstance;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class TableOnDeleteView extends TableOnViewBase {
    private final TableOnDeleteViewFactory parent;

    public TableOnDeleteView(SubordWMatchExprLookupStrategy lookupStrategy, TableStateInstance rootView, ExprEvaluatorContext exprEvaluatorContext, TableMetadata metadata,
                             TableOnDeleteViewFactory parent) {
        super(lookupStrategy, rootView, exprEvaluatorContext, metadata, true);
        this.parent = parent;
    }

    public void handleMatching(eu.uk.ncl.pet5o.esper.client.EventBean[] triggerEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] matchingEvents) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qInfraOnAction(OnTriggerType.ON_DELETE, triggerEvents, matchingEvents);
        }

        if ((matchingEvents != null) && (matchingEvents.length > 0)) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean event : matchingEvents) {
                tableStateInstance.deleteEvent(event);
            }

            // The on-delete listeners receive the events deleted, but only if there is interest
            if (parent.getStatementResultService().isMakeNatural() || parent.getStatementResultService().isMakeSynthetic()) {
                eu.uk.ncl.pet5o.esper.client.EventBean[] posted = TableOnViewUtil.toPublic(matchingEvents, parent.getTableMetadata(), triggerEvents, true, super.getExprEvaluatorContext());
                updateChildren(posted, null);
            }
        }

        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aInfraOnAction();
        }
    }

    public EventType getEventType() {
        return parent.getTableMetadata().getPublicEventType();
    }
}
