package com.liu.util.converter.support;

import nw.util.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ElementToObject implements GenericConverter {

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (Element.class.isInstance(source)) {
            try {
                Element el = (Element) source;
                Object dest = targetType.getType().newInstance();

                List<Attribute> attrs = el.attributes();
                for (Attribute attr : attrs) {
                    try {
                        BeanUtils.setProperty(dest, attr.getName(), attr.getValue());
                    } catch (Exception e) {
                        try {
                            BeanUtils.setPropertyInMap(dest, attr.getName(), attr.getValue());
                        } catch (Exception e1) {

                        }
                    }
                }
                return dest;
            } catch (Exception e) {
                throw new IllegalStateException("failed to convert element to bean", e);
            }
        } else {
            throw new IllegalStateException("source object must be a Element");
        }
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        Set<ConvertiblePair> set = new HashSet<ConvertiblePair>();
        set.add(new ConvertiblePair(Element.class, Object.class));
        return set;
    }

}
