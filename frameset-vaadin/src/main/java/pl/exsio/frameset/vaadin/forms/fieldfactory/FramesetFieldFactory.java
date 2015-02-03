/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.frameset.vaadin.forms.fieldfactory;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.fieldfactory.FieldFactory;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.vaadin.ex.FieldFactoryException;
import pl.exsio.jin.translationprefix.retriever.TranslationPrefixRetriever;

/**
 *
 * @author exsio
 * @param <T>
 */
public class FramesetFieldFactory<T> extends FieldFactory {

    private final Class<T> itemEntityClass;

    private String translationPrefix = TranslationPrefixRetriever.EMPTY_PREFIX;

    public FramesetFieldFactory(Class<T> itemEntityClass) {
        this.itemEntityClass = itemEntityClass;
    }

    public FramesetFieldFactory(Class<T> itemEntityClass, Class translatedClass) {
        this.itemEntityClass = itemEntityClass;
        this.translationPrefix = TranslationPrefixRetriever.getTranslationPrefix(translatedClass);
    }

    @Override
    public Field<?> createField(Item item, Object propertyId, Component uiContext) {
        Field<?> field = super.createField(item, propertyId, uiContext);
        if (field instanceof Field) {
            field.setCaption(this.getCaption(propertyId));
            return field;
        } else {
            throw new FieldFactoryException("couldn't create field for property id '" + propertyId + "' of class '" + ((EntityItem) item).getEntity().getClass().getCanonicalName() + "'");
        }
    }

    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
        Field<?> field = super.createField(container, itemId, propertyId, uiContext);
        field.setCaption(this.getCaption(propertyId));
        return field;
    }

    private String getCaption(Object propertyId) {
        if (this.translationPrefix.equals(TranslationPrefixRetriever.EMPTY_PREFIX)) {
            return t(this.itemEntityClass.getCanonicalName() + "." + propertyId);
        } else {
            return t(this.translationPrefix + propertyId);
        }

    }

}
