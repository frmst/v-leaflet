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
package pl.exsio.frameset.vaadin.entity.provider;

import com.vaadin.addon.jpacontainer.provider.MutableLocalEntityProvider;
import com.vaadin.addon.jpacontainer.util.HibernateLazyLoadingDelegate;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author exsio
 * @param <T>
 */
@Transactional(propagation = Propagation.REQUIRED)
public abstract class TransactionalEntityProvider<T> extends MutableLocalEntityProvider<T> {

    @PersistenceContext
    private EntityManager em;

    public TransactionalEntityProvider(Class<T> entityClass) {
        super(entityClass);
        this.setTransactionsHandledByProvider(false);
        
    }

    @PostConstruct
    public void init() {
        this.setEntityManager(em);
        this.setEntitiesDetached(false);
        this.setLazyLoadingDelegate(new HibernateLazyLoadingDelegate());
    }

    @Override
    protected void runInTransaction(Runnable operation) {
        super.runInTransaction(operation);
    }

    @Override
    public T updateEntity(final T entity) {
        return super.updateEntity(entity);
    }

    @Override
    public T addEntity(final T entity) {
        return super.addEntity(entity);
    }

    @Override
    public void removeEntity(final Object entityId) {
        super.removeEntity(entityId);
    }

    @Override
    public void updateEntityProperty(final Object entityId, final String propertyName, final Object propertyValue)
            throws IllegalArgumentException {
        super.updateEntityProperty(entityId, propertyName, propertyValue);
    }
}
