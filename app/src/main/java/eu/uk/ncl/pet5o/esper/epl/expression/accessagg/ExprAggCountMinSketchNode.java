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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.util.CountMinSketchAgent;
import eu.uk.ncl.pet5o.esper.client.util.CountMinSketchAgentStringUTF16;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.core.service.StatementType;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateFactoryCountMinSketch;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchAggType;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchSpec;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchSpecHashes;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumnAggregation;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.Collection;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;

/**
 * Represents the Count-min sketch aggregate function.
 */
public class ExprAggCountMinSketchNode extends ExprAggregateNodeBase implements ExprAggregateAccessMultiValueNode {
    private static final long serialVersionUID = 202339518989532184L;

    private static final double DEFAULT_EPS_OF_TOTAL_COUNT = 0.0001;
    private static final double DEFAULT_CONFIDENCE = 0.99;
    private static final int DEFAULT_SEED = 1234567;
    private static final CountMinSketchAgentStringUTF16 DEFAULT_AGENT = new CountMinSketchAgentStringUTF16();

    private static final String MSG_NAME = "Count-min-sketch";
    private static final String NAME_EPS_OF_TOTAL_COUNT = "epsOfTotalCount";
    private static final String NAME_CONFIDENCE = "confidence";
    private static final String NAME_SEED = "seed";
    private static final String NAME_TOPK = "topk";
    private static final String NAME_AGENT = "agent";

    private final CountMinSketchAggType aggType;

    public ExprAggCountMinSketchNode(boolean distinct, CountMinSketchAggType aggType) {
        super(distinct);
        this.aggType = aggType;
    }

    public AggregationMethodFactory validateAggregationChild(ExprValidationContext validationContext) throws ExprValidationException {
        return validateAggregationInternal(validationContext, null);
    }

    public AggregationMethodFactory validateAggregationParamsWBinding(ExprValidationContext context, TableMetadataColumnAggregation tableAccessColumn) throws ExprValidationException {
        return validateAggregationInternal(context, tableAccessColumn);
    }

    public ExprEnumerationEval getExprEvaluatorEnumeration() {
        return this;
    }

    public String getAggregationFunctionName() {
        return aggType.getFuncName();
    }

    public final boolean equalsNodeAggregateMethodOnly(ExprAggregateNode node) {
        return false;
    }

    public CountMinSketchAggType getAggType() {
        return aggType;
    }

    public EventType getEventTypeCollection(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return null;
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Class getComponentTypeCollection() throws ExprValidationException {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public CodegenExpression evaluateGetROCollectionScalarCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return null;
    }

    public CodegenExpression evaluateGetROCollectionEventsCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }

    public EventType getEventTypeSingle(EventAdapterService eventAdapterService, int statementId) throws ExprValidationException {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public CodegenExpression evaluateGetEventBeanCodegen(CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return constantNull();
    }


    @Override
    protected boolean isExprTextWildcardWhenNoParams() {
        return false;
    }

    private AggregationMethodFactory validateAggregationInternal(ExprValidationContext context, TableMetadataColumnAggregation tableAccessColumn)
            throws ExprValidationException {
        if (isDistinct()) {
            throw new ExprValidationException(getMessagePrefix() + "is not supported with distinct");
        }

        // for declaration, validate the specification and return the state factory
        if (aggType == CountMinSketchAggType.STATE) {
            if (context.getExprEvaluatorContext().getStatementType() != StatementType.CREATE_TABLE) {
                throw new ExprValidationException(getMessagePrefix() + "can only be used in create-table statements");
            }
            CountMinSketchSpec specification = validateSpecification(context);
            AggregationStateFactoryCountMinSketch stateFactory = context.getEngineImportService().getAggregationFactoryFactory().makeCountMinSketch(context.getStatementExtensionSvcContext(), this, specification);
            return new ExprAggCountMinSketchNodeFactoryState(stateFactory);
        }

        // validate number of parameters
        if (aggType == CountMinSketchAggType.ADD || aggType == CountMinSketchAggType.FREQ) {
            if (positionalParams.length == 0 || positionalParams.length > 1) {
                throw new ExprValidationException(getMessagePrefix() + "requires a single parameter expression");
            }
        } else {
            if (positionalParams.length != 0) {
                throw new ExprValidationException(getMessagePrefix() + "requires a no parameter expressions");
            }
        }

        // validate into-table and table-access
        if (aggType == CountMinSketchAggType.ADD) {
            if (context.getIntoTableName() == null) {
                throw new ExprValidationException(getMessagePrefix() + "can only be used with into-table");
            }
        } else {
            if (tableAccessColumn == null) {
                throw new ExprValidationException(getMessagePrefix() + "requires the use of a table-access expression");
            }
            ExprNodeUtilityRich.getValidatedSubtree(ExprNodeOrigin.AGGPARAM, this.getChildNodes(), context);
        }

        // obtain evaluator
        ExprForge addOrFrequencyEvaluator = null;
        Class addOrFrequencyEvaluatorReturnType = null;
        if (aggType == CountMinSketchAggType.ADD || aggType == CountMinSketchAggType.FREQ) {
            addOrFrequencyEvaluator = getChildNodes()[0].getForge();
            addOrFrequencyEvaluatorReturnType = addOrFrequencyEvaluator.getEvaluationType();
        }

        return new ExprAggCountMinSketchNodeFactoryUse(this, addOrFrequencyEvaluator, addOrFrequencyEvaluatorReturnType);
    }

    private CountMinSketchSpec validateSpecification(final ExprValidationContext exprValidationContext) throws ExprValidationException {
        // default specification
        final CountMinSketchSpec spec = new CountMinSketchSpec(new CountMinSketchSpecHashes(DEFAULT_EPS_OF_TOTAL_COUNT, DEFAULT_CONFIDENCE, DEFAULT_SEED), null, DEFAULT_AGENT);

        // no parameters
        if (this.getChildNodes().length == 0) {
            return spec;
        }

        // check expected parameter type: a json object
        if (this.getChildNodes().length > 1 || !(this.getChildNodes()[0] instanceof ExprConstantNode)) {
            throw getDeclaredWrongParameterExpr();
        }
        ExprConstantNode constantNode = (ExprConstantNode) this.getChildNodes()[0];
        Object value = constantNode.getConstantValue(exprValidationContext.getExprEvaluatorContext());
        if (!(value instanceof Map)) {
            throw getDeclaredWrongParameterExpr();
        }

        // define what to populate
        PopulateFieldWValueDescriptor[] descriptors = new PopulateFieldWValueDescriptor[]{
            new PopulateFieldWValueDescriptor(NAME_EPS_OF_TOTAL_COUNT, Double.class, spec.getHashesSpec().getClass(), new PopulateFieldValueSetter() {
                public void set(Object value) {
                    if (value != null) {
                        spec.getHashesSpec().setEpsOfTotalCount((Double) value);
                    }
                }
            }, true),
            new PopulateFieldWValueDescriptor(NAME_CONFIDENCE, Double.class, spec.getHashesSpec().getClass(), new PopulateFieldValueSetter() {
                public void set(Object value) {
                    if (value != null) {
                        spec.getHashesSpec().setConfidence((Double) value);
                    }
                }
            }, true),
            new PopulateFieldWValueDescriptor(NAME_SEED, Integer.class, spec.getHashesSpec().getClass(), new PopulateFieldValueSetter() {
                public void set(Object value) {
                    if (value != null) {
                        spec.getHashesSpec().setSeed((Integer) value);
                    }
                }
            }, true),
            new PopulateFieldWValueDescriptor(NAME_TOPK, Integer.class, spec.getClass(), new PopulateFieldValueSetter() {
                public void set(Object value) {
                    if (value != null) {
                        spec.setTopkSpec((Integer) value);
                    }
                }
            }, true),
            new PopulateFieldWValueDescriptor(NAME_AGENT, String.class, spec.getClass(), new PopulateFieldValueSetter() {
                public void set(Object value) throws ExprValidationException {
                    if (value != null) {
                        CountMinSketchAgent transform;
                        try {
                            Class transformClass = exprValidationContext.getEngineImportService().resolveClass((String) value, false);
                            transform = (CountMinSketchAgent) JavaClassHelper.instantiate(CountMinSketchAgent.class, transformClass);
                        } catch (Exception e) {
                            throw new ExprValidationException("Failed to instantiate agent provider: " + e.getMessage(), e);
                        }
                        spec.setAgent(transform);
                    }
                }
            }, true),
        };

        // populate from json, validates incorrect names, coerces types, instantiates transform
        PopulateUtil.populateSpecCheckParameters(descriptors, (Map<String, Object>) value, spec, ExprNodeOrigin.AGGPARAM, exprValidationContext);

        return spec;
    }


    public ExprValidationException getDeclaredWrongParameterExpr() throws ExprValidationException {
        return new ExprValidationException(getMessagePrefix() + " expects either no parameter or a single json parameter object");
    }

    protected boolean isFilterExpressionAsLastParameter() {
        return false;
    }

    private String getMessagePrefix() {
        return MSG_NAME + " aggregation function '" + aggType.getFuncName() + "' ";
    }
}
