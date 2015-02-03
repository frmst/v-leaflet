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
package pl.exsio.frameset.vaadin.module.management.groups;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.security.model.Group;
import pl.exsio.frameset.security.model.Role;
import pl.exsio.frameset.vaadin.forms.fieldfactory.FramesetFieldFactory;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.JPADataTable;
import pl.exsio.frameset.vaadin.ui.support.component.data.form.TabbedForm;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.TableDataConfig;
import pl.exsio.jin.annotation.TranslationPrefix;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
@TranslationPrefix("core.management.groups")
public class GroupsDataTable extends JPADataTable<Group, TabbedForm> {

    private transient SecurityEntityFactory securityEntities;

    public GroupsDataTable(SecurityContext security) {
        super(TabbedForm.class, new TableDataConfig(GroupsDataTable.class) {
            {
                setColumnHeaders("id", "name", "roles");
                setVisibleColumns("id", "name", "roles");
            }
        }, security);
        this.flexibleControls = true;
    }

    @Override
    protected Table createTable(JPAContainer<Group> container) {
        return new Table(this.config.getCaption(), container) {

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                switch (colId.toString()) {
                    case "roles":
                        StringBuilder roles = new StringBuilder();
                        for (Role role : (Set<Role>) property.getValue()) {
                            roles.append(", ").append(role.getLabel());
                        }
                        String rolesStr = roles.toString();
                        return rolesStr.isEmpty() ? "" : rolesStr.substring(2);
                    default:
                        return super.formatPropertyValue(rowId, colId, property);
                }
            }
        };
    }

    @Override
    protected Layout decorateForm(TabbedForm form, EntityItem<? extends Group> item, int mode) {

        form.init(this.getTabsConfig());
        form.getLayout().setWidth("400px");
        VerticalLayout formLayout = new VerticalLayout();

        FramesetFieldFactory<? extends Group> ff = new FramesetFieldFactory<>(this.securityEntities.getGroupClass(), this.getClass());
        ff.setMultiSelectType(this.securityEntities.getRoleClass(), OptionGroup.class);
        form.setFormFieldFactory(ff);
        form.setItemDataSource(item, Arrays.asList(new String[]{"name", "roles"}));
        form.setEnabled(true);
        form.setBuffered(true);
        formLayout.addComponent(form);
        return formLayout;
    }

    @Override
    protected String getItemDescription(EntityItem<? extends Group> item) {
        return item.getEntity().getName();
    }

    private Map<String, Set<String>> getTabsConfig() {
        return new LinkedHashMap() {
            {
                put(t("tab.basic_data"), new LinkedHashSet() {
                    {
                        add("name");
                    }
                });
                put(t("tab.roles"), new LinkedHashSet() {
                    {
                        add("roles");
                    }
                });
            }
        };
    }

    @Override
    protected Class<? extends Group> getEntityClass() {
        return this.securityEntities.getGroupClass();
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

}
