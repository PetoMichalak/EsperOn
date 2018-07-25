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
package eu.uk.ncl.pet5o.esper.event.property;

import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayDynamicPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayPropertyGetterDefaultObjectArray;
import eu.uk.ncl.pet5o.esper.event.bean.BeanEventType;
import eu.uk.ncl.pet5o.esper.event.bean.DynamicSimplePropertyGetter;
import eu.uk.ncl.pet5o.esper.event.map.MapDynamicPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.map.MapEventPropertyGetter;
import eu.uk.ncl.pet5o.esper.event.xml.BaseXMLEventType;
import eu.uk.ncl.pet5o.esper.event.xml.DOMAttributeAndElementGetter;
import eu.uk.ncl.pet5o.esper.event.xml.SchemaElementComplex;
import eu.uk.ncl.pet5o.esper.event.xml.SchemaItem;

import java.io.StringWriter;
import java.util.Map;

/**
 * Represents a dynamic simple property of a given name.
 * <p>
 * Dynamic properties always exist, have an Object type and are resolved to a method during runtime.
 */
public class DynamicSimpleProperty extends PropertyBase implements DynamicProperty, PropertySimple {
    /**
     * Ctor.
     *
     * @param propertyName is the property name
     */
    public DynamicSimpleProperty(String propertyName) {
        super(propertyName);
    }

    public EventPropertyGetterSPI getGetter(BeanEventType eventType, EventAdapterService eventAdapterService) {
        return new DynamicSimplePropertyGetter(propertyNameAtomic, eventAdapterService);
    }

    public boolean isDynamic() {
        return true;
    }

    public String[] toPropertyArray() {
        return new String[]{this.getPropertyNameAtomic()};
    }

    public Class getPropertyType(BeanEventType eventType, EventAdapterService eventAdapterService) {
        return Object.class;
    }

    public GenericPropertyDesc getPropertyTypeGeneric(BeanEventType beanEventType, EventAdapterService eventAdapterService) {
        return GenericPropertyDesc.getObjectGeneric();
    }

    public Class getPropertyTypeMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService) {
        return Object.class;
    }

    public MapEventPropertyGetter getGetterMap(Map optionalMapPropTypes, EventAdapterService eventAdapterService) {
        return new MapDynamicPropertyGetter(propertyNameAtomic);
    }

    public void toPropertyEPL(StringWriter writer) {
        writer.append(propertyNameAtomic);
    }

    public EventPropertyGetterSPI getGetterDOM(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService, BaseXMLEventType eventType, String propertyExpression) {
        return new DOMAttributeAndElementGetter(propertyNameAtomic);
    }

    public EventPropertyGetterSPI getGetterDOM() {
        return new DOMAttributeAndElementGetter(propertyNameAtomic);
    }

    public SchemaItem getPropertyTypeSchema(SchemaElementComplex complexProperty, EventAdapterService eventAdapterService) {
        return null;    // always returns Node
    }

    public ObjectArrayEventPropertyGetter getGetterObjectArray(Map<String, Integer> indexPerProperty, Map<String, Object> nestableTypes, EventAdapterService eventAdapterService) {
        // The simple, none-dynamic property needs a definition of the map contents else no property
        if (nestableTypes == null) {
            return new ObjectArrayDynamicPropertyGetter(propertyNameAtomic);
        }
        Integer propertyIndex = indexPerProperty.get(propertyNameAtomic);
        if (propertyIndex == null) {
            return new ObjectArrayDynamicPropertyGetter(propertyNameAtomic);
        }
        return new ObjectArrayPropertyGetterDefaultObjectArray(propertyIndex, null, eventAdapterService);
    }
}
