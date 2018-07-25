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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Represents an exists-subselect in an expression tree.
 */
public class ExprSubselectExistsNode extends ExprSubselectNode {
    private static final Logger log = LoggerFactory.getLogger(ExprSubselectExistsNode.class);
    private static final long serialVersionUID = 7082390247880356269L;

    private transient SubselectEvalStrategyNR subselectEvalStrategyNR;

    /**
     * Ctor.
     *
     * @param statementSpec is the lookup statement spec from the parser, unvalidated
     */
    public ExprSubselectExistsNode(StatementSpecRaw statementSpec) {
        super(statementSpec);
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    public void validateSubquery(ExprValidationContext validationContext) throws ExprValidationException {
        subselectEvalStrategyNR = SubselectEvalStrategyNRFactory.createStrategyExists(this);
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return subselectEvalStrategyNR.evaluate(eventsPerStream, isNewData, matchingEvents, exprEvaluatorContext, subselectAggregationService);
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

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) {
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
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
