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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateKey;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

public class ExprAggMultiFunctionLinearAccessNodeFactoryAccess implements AggregationMethodFactory {

    private final ExprAggMultiFunctionLinearAccessNode parent;
    private final AggregationAccessorForge accessor;
    private final Class accessorResultType;
    private final EventType containedEventType;

    private final AggregationStateKey optionalStateKey;
    private final AggregationStateFactoryForge optionalStateFactory;
    private final AggregationAgentForge optionalAgent;

    public ExprAggMultiFunctionLinearAccessNodeFactoryAccess(ExprAggMultiFunctionLinearAccessNode parent, AggregationAccessorForge accessor, Class accessorResultType, EventType containedEventType, AggregationStateKey optionalStateKey, AggregationStateFactoryForge optionalStateFactory, AggregationAgentForge optionalAgent) {
        this.parent = parent;
        this.accessor = accessor;
        this.accessorResultType = accessorResultType;
        this.containedEventType = containedEventType;
        this.optionalStateKey = optionalStateKey;
        this.optionalStateFactory = optionalStateFactory;
        this.optionalAgent = optionalAgent;
    }

    public boolean isAccessAggregation() {
        return true;
    }

    public AggregationMethod make() {
        throw new UnsupportedOperationException();
    }

    public Class getResultType() {
        return accessorResultType;
    }

    public AggregationStateKey getAggregationStateKey(boolean isMatchRecognize) {
        return optionalStateKey;
    }

    public AggregationStateFactoryForge getAggregationStateFactory(boolean isMatchRecognize) {
        return optionalStateFactory;
    }

    public AggregationAccessorForge getAccessorForge() {
        return accessor;
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        ExprAggMultiFunctionLinearAccessNodeFactoryAccess other = (ExprAggMultiFunctionLinearAccessNodeFactoryAccess) intoTableAgg;
        AggregationValidationUtil.validateEventType(this.containedEventType, other.getContainedEventType());
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return optionalAgent;
    }

    public EventType getContainedEventType() {
        return containedEventType;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return null;
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        throw new UnsupportedOperationException();
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        throw new UnsupportedOperationException();
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        throw new UnsupportedOperationException();
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        throw new UnsupportedOperationException();
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        throw new UnsupportedOperationException();
    }
}
