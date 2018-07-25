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

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Represents a subselect in an expression tree.
 */
public class ExprSubselectInNode extends ExprSubselectNode {
    private final boolean isNotIn;
    private transient SubselectEvalStrategyNR subselectEvalStrategyNR;
    private static final long serialVersionUID = -7233906204211162498L;

    public ExprSubselectInNode(StatementSpecRaw statementSpec, boolean isNotIn) {
        super(statementSpec);
        this.isNotIn = isNotIn;
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    /**
     * Returns true for not-in, or false for in.
     *
     * @return true for not-in
     */
    public boolean isNotIn() {
        return isNotIn;
    }

    public void validateSubquery(ExprValidationContext validationContext) throws ExprValidationException {
        subselectEvalStrategyNR = SubselectEvalStrategyNRFactory.createStrategyAnyAllIn(this, isNotIn, false, false, null, validationContext.getEngineImportService(), validationContext.getStatementName());
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return subselectEvalStrategyNR.evaluate(eventsPerStream, isNewData, matchingEvents, exprEvaluatorContext, getSubselectAggregationService());
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

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) {
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public boolean isAllowMultiColumnSelect() {
        return false;
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }
}
