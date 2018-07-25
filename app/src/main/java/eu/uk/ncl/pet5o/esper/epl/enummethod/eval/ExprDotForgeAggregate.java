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
package eu.uk.ncl.pet5o.esper.epl.enummethod.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotEvalParam;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotEvalParamLambda;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotForgeEnumMethodBase;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotNodeUtility;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.List;

public class ExprDotForgeAggregate extends ExprDotForgeEnumMethodBase {

    public EventType[] getAddStreamTypes(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType, List<ExprDotEvalParam> bodiesAndParameters, EventAdapterService eventAdapterService) {
        EventType evalEventType;
        if (inputEventType == null) {
            evalEventType = ExprDotNodeUtility.makeTransientOAType(enumMethodUsedName, goesToNames.get(1), collectionComponentType, eventAdapterService);
        } else {
            evalEventType = inputEventType;
        }

        Class initializationType = bodiesAndParameters.get(0).getBodyForge().getEvaluationType();
        EventType typeResult = ExprDotNodeUtility.makeTransientOAType(enumMethodUsedName, goesToNames.get(0), initializationType, eventAdapterService);

        return new EventType[]{typeResult, evalEventType};
    }

    public EnumForge getEnumForge(EngineImportService engineImportService, EventAdapterService eventAdapterService, StreamTypeService streamTypeService, int statementId, String enumMethodUsedName, List<ExprDotEvalParam> bodiesAndParameters, EventType inputEventType, Class collectionComponentType, int numStreamsIncoming, boolean disablePropertyExpressionEventCollCache) {
        ExprDotEvalParam initValueParam = bodiesAndParameters.get(0);
        ExprForge initValueEval = initValueParam.getBodyForge();
        super.setTypeInfo(EPTypeHelper.singleValue(JavaClassHelper.getBoxedType(initValueEval.getEvaluationType())));

        ExprDotEvalParamLambda resultAndAdd = (ExprDotEvalParamLambda) bodiesAndParameters.get(1);

        if (inputEventType != null) {
            return new EnumAggregateEventsForge(initValueEval,
                    resultAndAdd.getBodyForge(), resultAndAdd.getStreamCountIncoming(),
                    (ObjectArrayEventType) resultAndAdd.getGoesToTypes()[0]);
        } else {
            return new EnumAggregateScalarForge(initValueEval,
                    resultAndAdd.getBodyForge(), resultAndAdd.getStreamCountIncoming(),
                    (ObjectArrayEventType) resultAndAdd.getGoesToTypes()[0],
                    (ObjectArrayEventType) resultAndAdd.getGoesToTypes()[1]);
        }
    }
}
