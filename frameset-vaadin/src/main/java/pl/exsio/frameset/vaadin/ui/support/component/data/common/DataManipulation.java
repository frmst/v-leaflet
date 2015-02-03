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
package pl.exsio.frameset.vaadin.ui.support.component.data.common;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 *
 * @author exsio
 */
public interface DataManipulation {

    public interface DataAdditionListener<F extends Object, C extends Container, I extends Item> {

        void beforeDataAddition(F form, I item, C container);

        void dataAdded(F form, I item, C container);
    }

    public interface DataUpdateListener<F extends Object, C extends Container, I extends Item> {

        void beforeDataUpdate(F form, I item, C container);

        void dataUpdated(F form, I item, C container);
    }

    public interface DataDeletionListener<C extends Container, I extends Item> {

        void beforeDataDeletion(I item, C container);

        void dataDeleted(I item, C container);
    }
}
