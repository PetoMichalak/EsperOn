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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceLock;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraph;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessorInstance;
import eu.uk.ncl.pet5o.esper.epl.virtualdw.VirtualDWView;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.lang.annotation.Annotation;
import java.util.Collection;

public class FireAndForgetInstanceNamedWindow extends FireAndForgetInstance {
    private final NamedWindowProcessorInstance processorInstance;

    public FireAndForgetInstanceNamedWindow(NamedWindowProcessorInstance processorInstance) {
        this.processorInstance = processorInstance;
    }

    public NamedWindowProcessorInstance getProcessorInstance() {
        return processorInstance;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] processInsert(EPPreparedExecuteIUDSingleStreamExecInsert insert) {
        EPPreparedExecuteTableHelper.assignTableAccessStrategies(insert.getServices(), insert.getOptionalTableNodes(), processorInstance.getTailViewInstance().getAgentInstanceContext());
        try {
            eu.uk.ncl.pet5o.esper.client.EventBean event = insert.getInsertHelper().process(new eu.uk.ncl.pet5o.esper.client.EventBean[0], true, true, insert.getExprEvaluatorContext());
            eu.uk.ncl.pet5o.esper.client.EventBean[] inserted = new eu.uk.ncl.pet5o.esper.client.EventBean[]{event};

            AgentInstanceContext ctx = processorInstance.getTailViewInstance().getAgentInstanceContext();
            StatementAgentInstanceLock ailock = ctx.getAgentInstanceLock();
            ailock.acquireWriteLock();
            try {
                processorInstance.getRootViewInstance().update(inserted, null);
            } catch (EPException ex) {
                processorInstance.getRootViewInstance().update(null, inserted);
            } finally {
                ailock.releaseWriteLock();
            }
            return inserted;
        } finally {
            insert.getServices().getTableService().getTableExprEvaluatorContext().releaseAcquiredLocks();
        }
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] processDelete(EPPreparedExecuteIUDSingleStreamExecDelete delete) {
        EPPreparedExecuteTableHelper.assignTableAccessStrategies(delete.getServices(), delete.getOptionalTableNodes(), processorInstance.getTailViewInstance().getAgentInstanceContext());
        return processorInstance.getTailViewInstance().snapshotDelete(delete.getQueryGraph(), delete.getOptionalWhereClause(), delete.getAnnotations());
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] processUpdate(EPPreparedExecuteIUDSingleStreamExecUpdate update) {
        EPPreparedExecuteTableHelper.assignTableAccessStrategies(update.getServices(), update.getOptionalTableNodes(), processorInstance.getTailViewInstance().getAgentInstanceContext());
        return processorInstance.getTailViewInstance().snapshotUpdate(update.getQueryGraph(), update.getOptionalWhereClause(), update.getUpdateHelper(), update.getAnnotations());
    }

    public Collection<EventBean> snapshotBestEffort(EPPreparedExecuteMethodQuery query, QueryGraph queryGraph, Annotation[] annotations) {
        EPPreparedExecuteTableHelper.assignTableAccessStrategies(query.getServices(), query.getTableNodes(), processorInstance.getTailViewInstance().getAgentInstanceContext());
        return processorInstance.getTailViewInstance().snapshot(queryGraph, annotations);
    }

    public AgentInstanceContext getAgentInstanceContext() {
        return processorInstance.getTailViewInstance().getAgentInstanceContext();
    }

    public Viewable getTailViewInstance() {
        return processorInstance.getTailViewInstance();
    }

    public VirtualDWView getVirtualDataWindow() {
        return processorInstance.getRootViewInstance().getVirtualDataWindow();
    }
}
