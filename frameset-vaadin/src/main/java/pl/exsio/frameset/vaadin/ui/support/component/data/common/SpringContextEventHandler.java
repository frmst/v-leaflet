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
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author exsio
 */
public class SpringContextEventHandler<F extends Object, C extends Container, I extends Item> {

    private final transient ApplicationEventPublisher publisher;

    public SpringContextEventHandler(ApplicationEventPublisher aep) {
        this.publisher = aep;
    }

    public void publishBeforeAdditionEvent(F form, C container, I item) {
        BeforeEntityAdditionEvent ev = new BeforeEntityAdditionEvent(form, container, item, this);
        this.publishEvent(ev);
    }

    public void publishAddedEvent(F form, C container, I item) {
        EntityAddedEvent ev = new EntityAddedEvent(form, container, item, this);
        this.publishEvent(ev);
    }

    public void publishBeforeUpdateEvent(F form, C container, I item) {
        BeforeEntityUpdateEvent ev = new BeforeEntityUpdateEvent(form, container, item, this);
        this.publishEvent(ev);
    }

    public void publishUpdatedEvent(F form, C container, I item) {
        EntityUpdatedEvent ev = new EntityUpdatedEvent(form, container, item, this);
        this.publishEvent(ev);
    }

    public void publishBeforeDeletionEvent(C container, I item) {
        BeforeEntityDeletionEvent ev = new BeforeEntityDeletionEvent(container, item, this);
        this.publishEvent(ev);
    }

    public void publishDeleteEvent(C container, I item) {
        EntityDeletedEvent ev = new EntityDeletedEvent(container, item, this);
        this.publishEvent(ev);
    }

    private void publishEvent(EntityActionEvent event) {
        this.publisher.publishEvent(event);
    }

    public static class EntityDeletedEvent<C extends Container, I extends Item> extends EntityActionEvent<C, I> {

        public EntityDeletedEvent(C container, I item, Object source) {
            super(container, item, source);
        }
    }

    public static class EntityUpdatedEvent<F extends Object, C extends Container, I extends Item> extends EntityActionFormEvent<F, C, I> {

        public EntityUpdatedEvent(F form, C container, I item, Object source) {
            super(form, container, item, source);
        }
    }

    public static class BeforeEntityAdditionEvent<F extends Object, C extends Container, I extends Item> extends EntityActionFormEvent<F, C, I> {

        public BeforeEntityAdditionEvent(F form, C container, I item, Object source) {
            super(form, container, item, source);
        }
    }

    public static class BeforeEntityDeletionEvent<C extends Container, I extends Item> extends EntityActionEvent<C, I> {

        public BeforeEntityDeletionEvent(C container, I item, Object source) {
            super(container, item, source);
        }
    }

    public static class BeforeEntityUpdateEvent<F extends Object, C extends Container, I extends Item> extends EntityActionFormEvent<F, C, I> {

        public BeforeEntityUpdateEvent(F form, C container, I item, Object source) {
            super(form, container, item, source);
        }
    }

    public static class EntityAddedEvent<F extends Object, C extends Container, I extends Item> extends EntityActionFormEvent<F, C, I> {

        public EntityAddedEvent(F form, C container, I item, Object source) {
            super(form, container, item, source);
        }
    }

    private static class EntityActionFormEvent<F extends Object, C extends Container, I extends Item> extends EntityActionEvent<C, I> {

        private final F form;

        public EntityActionFormEvent(F form, C container, I item, Object source) {
            super(container, item, source);
            this.form = form;
        }

        public F getForm() {
            return form;
        }

    }

    private static class EntityActionEvent<C extends Container, I extends Item> extends ApplicationEvent {

        private final Container container;

        private final Item item;

        public EntityActionEvent(C container, I item, Object source) {
            super(source);
            this.container = container;
            this.item = item;
        }

        public Container getContainer() {
            return container;
        }

        public Item getItem() {
            return item;
        }

    }

}
