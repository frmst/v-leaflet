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
package pl.exsio.frameset.vaadin.ui.support.component.data.table;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.ui.Notification;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataAdditionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataDeletionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataUpdateListener;

/**
 *
 * @author exsio
 */
public abstract class JPADataTable<T extends Object, F extends Object> extends DataTable<F, JPAContainer<T>, EntityItem<? extends T>> implements DataAdditionListener<F, JPAContainer<T>, EntityItem<? extends T>>, DataUpdateListener<F, JPAContainer<T>, EntityItem<? extends T>>, DataDeletionListener<JPAContainer<T>, EntityItem<? extends T>> {

    protected transient EntityProvider entityProvider;

    public JPADataTable(Class<F> formClass, TableDataConfig config, SecurityContext security) {
        super(formClass, config, security);
        this.addDataAddedListener(this);
        this.addDataUpdatedListener(this);
        this.addDataDeletedListener(this);
    }

    @Override
    protected JPAContainer<T> createContainer() {
        JPAContainer<T> container = JPAContainerFactory.make(this.getEntityClass(), this.entityProvider.getEntityManager());
        container.setEntityProvider(this.entityProvider);
        return container;
    }

    @Override
    protected Object addItem(final EntityItem<? extends T> item, final JPAContainer<T> container, F form) {
        this.commitForm(form);
        Object itemId = container.addEntity(item.getEntity());
        this.commitContainer(container);
        return itemId;
    }

    @Override
    protected EntityItem<T> createItem(JPAContainer<T> container) {
        try {
            return container.createEntityItem(this.getEntityClass().newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
            Notification.show("An error occured during element creation", Notification.Type.ERROR_MESSAGE);
            throw new DataManipulationException("Couldn't instantiate class " + getEntityClass().getCanonicalName(), ex);
        }

    }

    protected abstract <S extends T> Class<S> getEntityClass();

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void beforeDataAddition(F form, EntityItem<? extends T> item, JPAContainer<T> container) {
    }

    @Override
    public void dataAdded(F form, EntityItem<? extends T> item, JPAContainer<T> container) {
    }

    @Override
    public void beforeDataUpdate(F form, EntityItem<? extends T> item, JPAContainer<T> container) {
    }

    @Override
    public void dataUpdated(F form, EntityItem<? extends T> item, JPAContainer<T> container) {
    }

    @Override
    public void beforeDataDeletion(EntityItem<? extends T> item, JPAContainer<T> container) {
    }

    @Override
    public void dataDeleted(EntityItem<? extends T> item, JPAContainer<T> container) {
    }

}
