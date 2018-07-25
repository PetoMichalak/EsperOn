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
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.EnumMethodEnum;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotEvalParam;
import eu.uk.ncl.pet5o.esper.epl.enummethod.dot.ExprDotForgeEnumMethodBase;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEnumerationSourceForge;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotNodeUtility;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventTypeUtility;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.List;

public class ExprDotForgeSetExceptUnionIntersect extends ExprDotForgeEnumMethodBase {

    public EventType[] getAddStreamTypes(String enumMethodUsedName, List<String> goesToNames, EventType inputEventType, Class collectionComponentType, List<ExprDotEvalParam> bodiesAndParameters, EventAdapterService eventAdapterService) {
        return new EventType[]{};
    }

    public EnumForge getEnumForge(EngineImportService engineImportService, EventAdapterService eventAdapterService, StreamTypeService streamTypeService, int statementId, String enumMethodUsedName, List<ExprDotEvalParam> bodiesAndParameters, EventType inputEventType, Class collectionComponentType, int numStreamsIncoming, boolean disablePropertyExpressionEventCollCache) throws ExprValidationException {
        ExprDotEvalParam first = bodiesAndParameters.get(0);

        ExprDotEnumerationSourceForge enumSrc = ExprDotNodeUtility.getEnumerationSource(first.getBody(), streamTypeService, eventAdapterService, statementId, true, disablePropertyExpressionEventCollCache);
        if (inputEventType != null) {
            super.setTypeInfo(EPTypeHelper.collectionOfEvents(inputEventType));
        } else {
            super.setTypeInfo(EPTypeHelper.collectionOfSingleValue(collectionComponentType));
        }

        if (inputEventType != null) {
            EventType setType = enumSrc.getEnumeration() == null ? null : enumSrc.getEnumeration().getEventTypeCollection(eventAdapterService, statementId);
            if (setType == null) {
                String message = "Enumeration method '" + enumMethodUsedName + "' requires an expression yielding a " +
                        "collection of events of type '" + inputEventType.getName() + "' as input parameter";
                throw new ExprValidationException(message);
            }
            if (setType != inputEventType) {
                boolean isSubtype = EventTypeUtility.isTypeOrSubTypeOf(setType, inputEventType);
                if (!isSubtype) {
                    String message = "Enumeration method '" + enumMethodUsedName + "' expects event type '" + inputEventType.getName() + "' but receives event type '" + setType.getName() + "'";
                    throw new ExprValidationException(message);
                }
            }
        } else {
            Class setType = enumSrc.getEnumeration() == null ? null : enumSrc.getEnumeration().getComponentTypeCollection();
            if (setType == null) {
                String message = "Enumeration method '" + enumMethodUsedName + "' requires an expression yielding a " +
                        "collection of values of type '" + collectionComponentType.getSimpleName() + "' as input parameter";
                throw new ExprValidationException(message);
            }
            if (!JavaClassHelper.isAssignmentCompatible(setType, collectionComponentType)) {
                String message = "Enumeration method '" + enumMethodUsedName + "' expects scalar type '" + collectionComponentType.getSimpleName() + "' but receives event type '" + setType.getSimpleName() + "'";
                throw new ExprValidationException(message);
            }
        }

        if (this.getEnumMethodEnum() == EnumMethodEnum.UNION) {
            return new EnumUnionForge(numStreamsIncoming, enumSrc.getEnumeration(), inputEventType == null);
        } else if (this.getEnumMethodEnum() == EnumMethodEnum.INTERSECT) {
            return new EnumIntersectForge(numStreamsIncoming, enumSrc.getEnumeration(), inputEventType == null);
        } else if (this.getEnumMethodEnum() == EnumMethodEnum.EXCEPT) {
            return new EnumExceptForge(numStreamsIncoming, enumSrc.getEnumeration(), inputEventType == null);
        } else {
            throw new IllegalArgumentException("Invalid enumeration method for this factory: " + this.getEnumMethodEnum());
        }
    }
}
