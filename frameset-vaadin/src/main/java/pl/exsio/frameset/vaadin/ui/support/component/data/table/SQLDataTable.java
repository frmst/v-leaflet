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

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;
import java.sql.SQLException;
import javax.sql.DataSource;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataAdditionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataDeletionListener;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataManipulation.DataUpdateListener;

/**
 *
 * @author exsio
 */
public abstract class SQLDataTable<F extends Object> extends DataTable<F, SQLContainer, RowItem> implements DataAdditionListener<F, SQLContainer, RowItem>, DataUpdateListener<F, SQLContainer, RowItem>, DataDeletionListener<SQLContainer, RowItem> {

    protected DataSource dataSource;

    static {
        /**
         * MySQL compilance.
         */
        QueryBuilder.setStringDecorator(new StringDecorator("`", "`"));
    }

    public SQLDataTable(DataSource dataSource, Class<F> formClass, TableDataConfig config, SecurityContext security) {
        super(formClass, config, security);
        this.addDataAddedListener(this);
        this.addDataUpdatedListener(this);
        this.addDataDeletedListener(this);
        this.dataSource = dataSource;
    }

    @Override
    protected SQLContainer createContainer() {
        QueryDelegate query = this.getQuery(this.getConnectionPool());
        try {
            return new SQLContainer(query);
        } catch (SQLException ex) {
            throw new DataManipulationException("couldn't create an SQL Container", ex);
        }
    }

    @Override
    protected void commitContainer(final SQLContainer container) {
        try {
            container.commit();
        } catch (SQLException ex) {
            throw new DataManipulationException("couldn't commit the SQL Container", ex);
        }
    }

    @Override
    protected void removeItem(final RowItem item, final SQLContainer container) {
        container.removeItem(item.getId());
    }

    protected J2EEConnectionPool getConnectionPool() {
        return new J2EEConnectionPool(this.dataSource);
    }

    @Override
    protected Object addItem(RowItem item, SQLContainer container, F form) {
        this.commitForm(form);
        this.commitContainer(container);
        return item.getId();
    }

    @Override
    protected RowItem createItem(SQLContainer container) {
        RowId id = (RowId) container.addItem();
        if (id instanceof RowId) {
            return (RowItem) container.getItem(id);
        } else {
            throw new DataManipulationException("Error creating Row item");
        }

    }

    protected QueryDelegate getQuery(J2EEConnectionPool pool) {
        TableQuery tq = new TableQuery(this.getTableName(), pool);
        tq.setVersionColumn(this.getVersionColumnName());
        return tq;
    }

    protected String getTableName() {
        throw new UnsupportedOperationException("You must provide the table name in child class");
    }

    protected String getVersionColumnName() {
        return "id";
    }

    @Override
    public void beforeDataAddition(F form, RowItem item, SQLContainer container) {
    }

    @Override
    public void dataAdded(F form, RowItem item, SQLContainer container) {
    }

    @Override
    public void beforeDataUpdate(F form, RowItem item, SQLContainer container) {
    }

    @Override
    public void dataUpdated(F form, RowItem item, SQLContainer container) {
    }

    @Override
    public void beforeDataDeletion(RowItem item, SQLContainer container) {
    }

    @Override
    public void dataDeleted(RowItem item, SQLContainer container) {
    }

}
