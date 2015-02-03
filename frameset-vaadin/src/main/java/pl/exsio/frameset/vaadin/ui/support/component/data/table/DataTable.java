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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.ui.support.component.data.common.DataComponent;

/**
 *
 * @author exsio
 */
public abstract class DataTable<F extends Object, C extends Container, I extends Item> extends DataComponent<F, C, I, Table, TableDataConfig> {

    protected final Set<ItemClickEvent.ItemClickListener> tableClickListeners;

    public DataTable(Class<F> formClass, TableDataConfig config, SecurityContext security) {
        super(formClass, config, security);
        this.tableClickListeners = new LinkedHashSet<>();
    }

    @Override
    protected final Table createDataComponent(C container) {

        Table table = this.createTable(container);
        table.setStyleName("frameset-dt-table");
        table.setSizeFull();
        table.setVisibleColumns(this.config.getVisibleColumns());
        table.setColumnHeaders(t(this.config.getColumnHeaders()));
        table.setSelectable(true);
        for (ItemClickEvent.ItemClickListener listener : this.tableClickListeners) {
            table.addItemClickListener(listener);
        }
        return table;
    }

    @Override
    protected final void hookHandlersToDataComponent(Table table, final C container) {
        super.hookHandlersToDataComponent(table, container);
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                I item = (I) event.getItem();
                hookDeleteHandler(item, container);
                hookEditHandler(item, container, event);
            }
        });
    }

    protected Table createTable(C container) {
        return new Table(t(this.config.getCaption()), container);
    }

    public final void addTableItemClickListener(ItemClickEvent.ItemClickListener listener) {
        this.tableClickListeners.add(listener);
        if (this.dataComponent instanceof Table) {
            this.dataComponent.addItemClickListener(listener);
        }
    }

}
