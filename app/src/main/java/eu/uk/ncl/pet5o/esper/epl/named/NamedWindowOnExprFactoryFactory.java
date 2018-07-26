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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.soda.StreamSelector;
import eu.uk.ncl.pet5o.esper.core.service.InternalEventRouter;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.metric.StatementMetricHandle;
import eu.uk.ncl.pet5o.esper.epl.spec.*;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelper;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelperFactory;
import eu.uk.ncl.pet5o.esper.event.EventBeanReader;
import eu.uk.ncl.pet5o.esper.event.EventBeanReaderDefaultImpl;
import eu.uk.ncl.pet5o.esper.event.EventTypeSPI;

/**
 * View for the on-delete statement that handles removing events from a named window.
 */
public class NamedWindowOnExprFactoryFactory {
    public static NamedWindowOnExprFactory make(EventType namedWindowEventType,
                                                String namedWindowName,
                                                String namedWindowAlias,
                                                OnTriggerDesc onTriggerDesc,
                                                EventType filterEventType,
                                                String filterStreamName,
                                                boolean addToFront,
                                                InternalEventRouter internalEventRouter,
                                                EventType outputEventType,
                                                StatementContext statementContext,
                                                StatementMetricHandle createNamedWindowMetricsHandle,
                                                boolean isDistinct,
                                                StreamSelector optionalStreamSelector,
                                                String optionalInsertIntoTableName)
            throws ExprValidationException {
        if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_DELETE) {
            return new NamedWindowOnDeleteViewFactory(namedWindowEventType, statementContext.getStatementResultService());
        } else if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_SELECT) {
            EventBeanReader eventBeanReader = null;
            if (isDistinct) {
                if (outputEventType instanceof EventTypeSPI) {
                    eventBeanReader = ((EventTypeSPI) outputEventType).getReader();
                }
                if (eventBeanReader == null) {
                    eventBeanReader = new EventBeanReaderDefaultImpl(outputEventType);
                }
            }
            OnTriggerWindowDesc windowDesc = (OnTriggerWindowDesc) onTriggerDesc;
            return new NamedWindowOnSelectViewFactory(namedWindowEventType, internalEventRouter, addToFront,
                    statementContext.getEpStatementHandle(), eventBeanReader, isDistinct, statementContext.getStatementResultService(), statementContext.getInternalEventEngineRouteDest(), windowDesc.isDeleteAndSelect(), optionalStreamSelector, optionalInsertIntoTableName);
        } else if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_UPDATE) {
            OnTriggerWindowUpdateDesc updateDesc = (OnTriggerWindowUpdateDesc) onTriggerDesc;
            EventBeanUpdateHelper updateHelper = EventBeanUpdateHelperFactory.make(namedWindowName, (EventTypeSPI) namedWindowEventType, updateDesc.getAssignments(), namedWindowAlias, filterEventType, true, statementContext.getStatementName(), statementContext.getEngineURI(), statementContext.getEventAdapterService(), false);
            return new NamedWindowOnUpdateViewFactory(namedWindowEventType, statementContext.getStatementResultService(), updateHelper);
        } else if (onTriggerDesc.getOnTriggerType() == OnTriggerType.ON_MERGE) {
            OnTriggerMergeDesc onMergeTriggerDesc = (OnTriggerMergeDesc) onTriggerDesc;
            NamedWindowOnMergeHelper onMergeHelper = new NamedWindowOnMergeHelper(statementContext, onMergeTriggerDesc, filterEventType, filterStreamName, internalEventRouter, namedWindowName, (EventTypeSPI) namedWindowEventType);
            return new NamedWindowOnMergeViewFactory(namedWindowEventType, onMergeHelper, statementContext.getStatementResultService(), createNamedWindowMetricsHandle, statementContext.getMetricReportingService());
        } else {
            throw new IllegalStateException("Unknown trigger type " + onTriggerDesc.getOnTriggerType());
        }
    }
}
