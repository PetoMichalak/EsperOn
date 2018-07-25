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

import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraph;
import eu.uk.ncl.pet5o.esper.epl.table.upd.TableUpdateStrategy;
import eu.uk.ncl.pet5o.esper.epl.updatehelper.EventBeanUpdateHelper;

import java.lang.annotation.Annotation;

public class EPPreparedExecuteIUDSingleStreamExecUpdate implements EPPreparedExecuteIUDSingleStreamExec {
    private final QueryGraph queryGraph;
    private final ExprNode optionalWhereClause;
    private final Annotation[] annotations;
    private final EventBeanUpdateHelper updateHelper;
    private final TableUpdateStrategy tableUpdateStrategy;
    private final ExprTableAccessNode[] optionalTableNodes;
    private final EPServicesContext services;

    public EPPreparedExecuteIUDSingleStreamExecUpdate(QueryGraph queryGraph, ExprNode optionalWhereClause, Annotation[] annotations, EventBeanUpdateHelper updateHelper, TableUpdateStrategy tableUpdateStrategy, ExprTableAccessNode[] optionalTableNodes, EPServicesContext services) {
        this.queryGraph = queryGraph;
        this.optionalWhereClause = optionalWhereClause;
        this.annotations = annotations;
        this.updateHelper = updateHelper;
        this.tableUpdateStrategy = tableUpdateStrategy;
        this.optionalTableNodes = optionalTableNodes;
        this.services = services;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] execute(FireAndForgetInstance fireAndForgetProcessorInstance) {
        return fireAndForgetProcessorInstance.processUpdate(this);
    }

    public ExprNode getOptionalWhereClause() {
        return optionalWhereClause;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public EventBeanUpdateHelper getUpdateHelper() {
        return updateHelper;
    }

    public TableUpdateStrategy getTableUpdateStrategy() {
        return tableUpdateStrategy;
    }

    public ExprTableAccessNode[] getOptionalTableNodes() {
        return optionalTableNodes;
    }

    public EPServicesContext getServices() {
        return services;
    }

    public QueryGraph getQueryGraph() {
        return queryGraph;
    }
}
