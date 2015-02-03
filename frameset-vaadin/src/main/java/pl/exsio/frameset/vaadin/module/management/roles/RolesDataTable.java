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
package pl.exsio.frameset.vaadin.module.management.roles;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.cache.CacheManager;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.security.model.Role;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;
import pl.exsio.frameset.vaadin.forms.fieldfactory.FramesetFieldFactory;
import pl.exsio.frameset.vaadin.ui.support.component.data.form.TabbedForm;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.JPADataTable;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.TableDataConfig;
import pl.exsio.jin.annotation.TranslationPrefix;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
@TranslationPrefix("core.management.roles")
public class RolesDataTable extends JPADataTable<Role, TabbedForm> {

    private transient DataSource dataSource;

    private transient SecurityEntityFactory securityEntities;

    protected CacheManager cacheManager;

    public RolesDataTable(SecurityContext security) {
        super(TabbedForm.class, new TableDataConfig(RolesDataTable.class) {
            {
                setColumnHeaders("id", "name", "label", "child_roles");
                setVisibleColumns("id", "name", "label", "childRoles");
            }
        }, security);
        this.flexibleControls = true;
        this.addDataAddedListener(this);
        this.addDataUpdatedListener(this);
        this.addDataDeletedListener(this);
    }

    @Override
    protected Table createTable(JPAContainer<Role> container) {
        return new Table(this.config.getCaption(), container) {

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                switch (colId.toString()) {
                    case "childRoles":
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
    protected Layout decorateForm(TabbedForm form, EntityItem<? extends Role> item, int mode) {

        form.init(this.getTabsConfig());
        form.getLayout().setWidth("400px");
        FormLayout formLayout = new FormLayout();
        FramesetFieldFactory<? extends Role> ff = new FramesetFieldFactory<>(this.securityEntities.getRoleClass(), this.getClass());
        ff.setMultiSelectType(this.securityEntities.getRoleClass(), OptionGroup.class);
        form.setFormFieldFactory(ff);
        form.setItemDataSource(item, Arrays.asList(new String[]{"name", "label", "childRoles"}));
        item.getEntity().setOldName(item.getEntity().getName());
        form.setBuffered(true);
        formLayout.addComponent(form);
        return formLayout;
    }

    @Override
    protected String getItemDescription(EntityItem<? extends Role> item) {
        return item.getEntity().getLabel();
    }

    private Map<String, Set<String>> getTabsConfig() {
        return new LinkedHashMap() {
            {
                put(t("tab.basic_data"), new LinkedHashSet() {
                    {
                        add("name");
                        add("label");
                    }
                });
                put(t("tab.child_roles"), new LinkedHashSet() {
                    {
                        add("childRoles");
                    }
                });
            }
        };
    }

    @Override
    public void dataAdded(TabbedForm form, EntityItem<? extends Role> item, JPAContainer<Role> container) {
        this.clearRoleHierarchyCache();
        try {
            Connection conn = this.dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("insert into acl_sid(id, principal, sid) values(null,0,?)");
            stmt.setString(1, item.getEntity().getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataManipulationException("An error occured during insertion of new ACL SID", ex);
        }
    }

    @Override
    public void dataUpdated(TabbedForm form, EntityItem<? extends Role> item, JPAContainer<Role> container) {
        this.clearRoleHierarchyCache();
        try {
            Connection conn = this.dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("update acl_sid set sid = ? where sid = ?");
            stmt.setString(1, item.getEntity().getName());
            stmt.setString(2, item.getEntity().getOldName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataManipulationException("An error occured during insertion of new ACL SID", ex);
        }
    }

    @Override
    public void dataDeleted(EntityItem<? extends Role> item, JPAContainer<Role> container) {
        this.clearRoleHierarchyCache();
        try {
            Connection conn = this.dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("delete from acl_sid where sid = ?");
            stmt.setString(1, item.getEntity().getName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new DataManipulationException("An error occured during insertion of new ACL SID", ex);
        }
    }

    protected void clearRoleHierarchyCache() {
        this.cacheManager.getCache("roleHierarchy").clear();
    }

    @Override
    protected Class<? extends Role> getEntityClass() {
        return this.securityEntities.getRoleClass();
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
