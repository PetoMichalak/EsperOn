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
package eu.uk.ncl.pet5o.esper.epl.expression.accessagg;

import eu.uk.ncl.pet5o.esper.client.ConfigurationPlugInAggregationMultiFunction;
import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ArrayWrappingCollection;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregationPlugInNodeMarker;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoEvaluateSelf;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumnAggregation;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionFactory;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionHandler;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionValidationContext;

import java.util.Collection;

/**
 * Represents a custom aggregation function in an expresson tree.
 */
public class ExprPlugInAggMultiFunctionNode extends ExprAggregateNodeBase implements ExprEnumerationEval, ExprAggregateAccessMultiValueNode, ExprAggregationPlugInNodeMarker {
    private static final long serialVersionUID = 6356766499476980697L;
    private PlugInAggregationMultiFunctionFactory pluginAggregationMultiFunctionFactory;
    private final String functionName;
    private final ConfigurationPlugInAggregationMultiFunction config;
    private transient ExprPlugInAggMultiFunctionNodeFactory factory;

    public ExprPlugInAggMultiFunctionNode(boolean distinct, ConfigurationPlugInAggregationMultiFunction config, PlugInAggregationMultiFunctionFactory pluginAggregationMultiFunctionFactory, String functionName) {
        super(distinct);
        this.pluginAggregationMultiFunctionFactory = pluginAggregationMultiFunctionFactory;
        this.functionName = functionName;
        this.config = config;
    }

    public ExprEnumerationEval getExprEvaluatorEnumeration() {
        return this;
    }

    public AggregationMethodFactory validateAggregationChild(ExprValidationContext validationContext) throws ExprValidationException {
        validatePositionals();
        return validateAggregationParamsWBinding(validationContext, null);
    }

    public AggregationMethodFactory validateAggregationParamsWBinding(ExprValidationContext validationContext, TableMetadataColumnAggregation tableAccessColumn) throws ExprValidationException {
        // validate using the context provided by the 'outside' streams to determine parameters
        // at this time 'inside' expressions like 'window(intPrimitive)' are not handled
        ExprNodeUtilityRich.getValidatedSubtree(ExprNodeOrigin.AGGPARAM, this.getChildNodes(), validationContext);
        return validateAggregationInternal(validationContext, tableAccessColumn);
    }

    private AggregationMethodFactory validateAggregationInternal(ExprValidationContext validationContext, TableMetadataColumnAggregation optionalTableColumn) throws ExprValidationException {
        PlugInAggregationMultiFunctionValidationContext ctx = new PlugInAggregationMultiFunctionValidationContext(functionName, validationContext.getStreamTypeService().getEventTypes(), positionalParams, validationContext.getStreamTypeService().getEngineURIQualifier(), validationContext.getStatementName(), validationContext, config, optionalTableColumn, getChildNodes());
        PlugInAggregationMultiFunctionHandler handlerPlugin = pluginAggregationMultiFunctionFactory.validateGetHandler(ctx);
        factory = new ExprPlugInAggMultiFunctionNodeFactory(this, handlerPlugin, validationContext.getEngineImportService().getAggregationFactoryFactory(), validationContext.getStatementExtensionSvcContext());
        return factory;
    }

    public String getAggregationFunctionName() {
        return functionName;
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return super.aggregationResultFuture.getCollectionOfEvents(column, eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetROCollectionEvents(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        Object result = super.aggregationResultFuture.getValue(column, context.getAgentInstanceId(), eventsPerStream, isNewData, context);
        if (result == null) {
            return null;
        }
        if (result.getClass().isArray()) {
            return new ArrayWrappingCollection(result);
        }
        return (Collection) result;
    }

    public CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetROCollectionScalar(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) {
        return factory.getEventTypeCollection();
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return factory.getComponentTypeCollection();
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return factory.getEventTypeSingle();
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return super.aggregationResultFuture.getEventBean(column, eventsPerStream, isNewData, context);
    }

    public CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return CodegenLegoEvaluateSelf.evaluateSelfGetEventBean(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    protected boolean isFilterExpressionAsLastParameter() {
        return false;
    }

    public final boolean equalsNodeAggregateMethodOnly(ExprAggregateNode node) {
        return false;
    }
}
