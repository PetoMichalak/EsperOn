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
package eu.uk.ncl.pet5o.esper.epl.expression.table;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessor;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowPair;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggregateAccessMultiValueNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoEvaluateSelf;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumnAggregation;
import eu.uk.ncl.pet5o.esper.epl.table.strategy.ExprTableEvalStrategyUtil;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.io.StringWriter;
import java.util.Collection;

public class ExprTableIdentNodeSubpropAccessor extends ExprNodeBase implements ExprForge, ExprEvaluator, ExprEnumerationForge, ExprEnumerationEval {
    private static final long serialVersionUID = -8308528998078977774L;

    private final int streamNum;
    private final String optionalStreamName;
    private final TableMetadataColumnAggregation tableAccessColumn;
    private final ExprNode aggregateAccessMultiValueNode;
    private transient AggregationMethodFactory accessorFactory;
    private transient AggregationAccessor accessor;

    public ExprTableIdentNodeSubpropAccessor(int streamNum, String optionalStreamName, TableMetadataColumnAggregation tableAccessColumn, ExprNode aggregateAccessMultiValueNode) {
        this.streamNum = streamNum;
        this.optionalStreamName = optionalStreamName;
        this.tableAccessColumn = tableAccessColumn;
        this.aggregateAccessMultiValueNode = aggregateAccessMultiValueNode;
    }

    public ExprNode validate(ExprValidationContext validationContext) throws ExprValidationException {
        if (tableAccessColumn.getAccessAccessorSlotPair() == null) {
            throw new ExprValidationException("Invalid combination of aggregation state and aggregation accessor");
        }
        ExprAggregateAccessMultiValueNode mfNode = (ExprAggregateAccessMultiValueNode) aggregateAccessMultiValueNode;
        mfNode.validatePositionals();
        accessorFactory = mfNode.validateAggregationParamsWBinding(validationContext, tableAccessColumn);
        accessor = accessorFactory.getAccessorForge().getAccessor(validationContext.getEngineImportService(), validationContext.getStreamTypeService().isOnDemandStreams(), validationContext.getStatementName());
        return null;
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfPlainWithCast(requiredType, this, getEvaluationType(), codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public Class getEvaluationType() {
        return accessorFactory.getResultType();
    }

    public ExprForge getForge() {
        return this;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }

    public ExprEvaluator getExprEvaluator() {
        return this;
    }

    public ExprEnumerationEval getExprEvaluatorEnumeration() {
        return this;
    }

    public CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetROCollectionEvents(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationState state = getState(eventsPerStream);
        if (state == null) {
            return null;
        }
        return accessor.getValue(state, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.SELF;
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return ((ExprAggregateAccessMultiValueNode) aggregateAccessMultiValueNode).getEventTypeCollection(eventAdapterService, statementId);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        AggregationState state = getState(eventsPerStream);
        if (state == null) {
            return null;
        }
        return accessor.getEnumerableEvents(state, eventsPerStream, isNewData, context);
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return ((ExprAggregateAccessMultiValueNode) aggregateAccessMultiValueNode).getComponentTypeCollection();
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        AggregationState state = getState(eventsPerStream);
        if (state == null) {
            return null;
        }
        return accessor.getEnumerableScalar(state, eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetROCollectionScalar(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return ((ExprAggregateAccessMultiValueNode) aggregateAccessMultiValueNode).getEventTypeSingle(eventAdapterService, statementId);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        AggregationState state = getState(eventsPerStream);
        if (state == null) {
            return null;
        }
        return accessor.getEnumerableEvent(state, eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetEventBean(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public void toPrecedenceFreeEPL(StringWriter writer) {
        if (optionalStreamName != null) {
            writer.append(optionalStreamName);
            writer.append(".");
        }
        writer.append(tableAccessColumn.getColumnName());
        writer.append(".");
        aggregateAccessMultiValueNode.toEPL(writer, ExprPrecedenceEnum.MINIMUM);
    }

    public ExprPrecedenceEnum getPrecedence() {
        return ExprPrecedenceEnum.UNARY;
    }

    public boolean isConstantResult() {
        return false;
    }

    public boolean equalsNode(ExprNode node, boolean ignoreStreamPrefix) {
        return false;
    }

    private AggregationState getState(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = eventsPerStream[streamNum];
        if (event == null) {
            return null;
        }
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow((ObjectArrayBackedEventBean) event);
        return row.getStates()[tableAccessColumn.getAccessAccessorSlotPair().getSlot()];
    }
}
