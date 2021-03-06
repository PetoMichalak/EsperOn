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
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.spec.StatementSpecRaw;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.type.RelationalOpEnum;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Represents a subselect in an expression tree.
 */
public class ExprSubselectAllSomeAnyNode extends ExprSubselectNode {
    private final boolean isNot;
    private final boolean isAll;
    private final RelationalOpEnum relationalOp;

    private transient SubselectEvalStrategyNR evalStrategy;
    private static final long serialVersionUID = -3884694910286266280L;

    /**
     * Ctor.
     *
     * @param statementSpec    is the lookup statement spec from the parser, unvalidated
     * @param not              when NOT
     * @param all              when ALL, false for ANY
     * @param relationalOpEnum operator
     */
    public ExprSubselectAllSomeAnyNode(StatementSpecRaw statementSpec, boolean not, boolean all, RelationalOpEnum relationalOpEnum) {
        super(statementSpec);
        isNot = not;
        isAll = all;
        this.relationalOp = relationalOpEnum;
    }

    /**
     * Returns true for not.
     *
     * @return not indicator
     */
    public boolean isNot() {
        return isNot;
    }

    /**
     * Returns true for all.
     *
     * @return all indicator
     */
    public boolean isAll() {
        return isAll;
    }

    /**
     * Returns relational op.
     *
     * @return op
     */
    public RelationalOpEnum getRelationalOp() {
        return relationalOp;
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    public void validateSubquery(ExprValidationContext validationContext) throws ExprValidationException {
        evalStrategy = SubselectEvalStrategyNRFactory.createStrategyAnyAllIn(this, isNot, isAll, !isAll, relationalOp, validationContext.getEngineImportService(), validationContext.getStatementName());
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return evalStrategy.evaluate(eventsPerStream, isNewData, matchingEvents, exprEvaluatorContext, getSubselectAggregationService());
    }

    public LinkedHashMap<String, Object> typableGetRowProperties() {
        return null;
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public Object[][] evaluateTypableMulti(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) {
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    @Override
    public boolean isAllowMultiColumnSelect() {
        return false;
    }
}
