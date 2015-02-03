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
package pl.exsio.frameset.vaadin.module.management.users;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Field;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.security.model.Group;
import pl.exsio.frameset.security.model.Role;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.vaadin.ui.support.component.data.ex.DataManipulationException;
import pl.exsio.frameset.vaadin.forms.fieldfactory.FramesetFieldFactory;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.JPADataTable;
import pl.exsio.frameset.vaadin.ui.support.component.data.form.TabbedForm;
import pl.exsio.frameset.vaadin.ui.support.component.data.table.TableDataConfig;
import pl.exsio.jin.annotation.TranslationPrefix;

/**
 *
 * @author exsio
 */
@TranslationPrefix("core.management.users")
public class UsersDataTable extends JPADataTable<User, TabbedForm> {

    private transient PasswordEncoder passwordEncoder;

    private transient SecurityEntityFactory securityEntities;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UsersDataTable(SecurityContext security) {
        super(TabbedForm.class, new TableDataConfig(UsersDataTable.class) {
            {
                setColumnHeaders("id", "username", "is_enabled", "first_name", "last_name", "groups", "roles");
                setVisibleColumns("id", "username", "isEnabled", "firstName", "lastName", "groups", "roles");
            }
        }, security);
        this.flexibleControls = true;
        this.addDataAddedListener(this);
        this.addDataUpdatedListener(this);
    }

    @Override
    protected Table createTable(JPAContainer<User> container) {
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
                    case "groups":
                        StringBuilder groups = new StringBuilder();
                        for (Group group : (Set<Group>) property.getValue()) {
                            groups.append(", ").append(group.getName());
                        }
                        String groupsStr = groups.toString();
                        return groupsStr.isEmpty() ? "" : groupsStr.substring(2);
                    case "isEnabled":
                        if (property != null && property.getValue() != null && (Boolean) property.getValue()) {
                            return t("core.yes");
                        } else {
                            return t("core.no");
                        }

                    default:
                        return super.formatPropertyValue(rowId, colId, property);
                }
            }
        };
    }

    @Override
    protected Layout decorateForm(TabbedForm form, EntityItem<? extends User> item, int mode) {

        form.init(this.getTabsConfig());
        form.getLayout().setWidth("400px");
        VerticalLayout formLayout = new VerticalLayout();

        FramesetFieldFactory<? extends User> ff = new FramesetFieldFactory<>(this.securityEntities.getUserClass(), this.getClass());
        ff.setMultiSelectType(this.securityEntities.getRoleClass(), OptionGroup.class);
        ff.setMultiSelectType(this.securityEntities.getGroupClass(), OptionGroup.class);
        form.setFormFieldFactory(ff);
        form.setItemDataSource(item, Arrays.asList(new String[]{"username", "firstName", "lastName", "phoneNo", "isEnabled", "groups", "roles"}));
        Field passwordField = new PasswordField(t("plainPassword"), "");
        Field passwordFieldRepeated = new PasswordField(t("plainPasswordRepeated"), "");
        passwordField.setPropertyDataSource(item.getItemProperty("plainPassword"));
        passwordFieldRepeated.setPropertyDataSource(item.getItemProperty("plainPasswordRepeated"));
        form.addField("plainPassword", passwordField);
        form.addField("plainPasswordRepeated", passwordFieldRepeated);
        form.setBuffered(true);
        form.getField("username").addValidator(new EmailValidator(t("invalid_username")));
        form.setEnabled(true);

        formLayout.addComponent(form);
        return formLayout;
    }

    @Override
    protected String getItemDescription(EntityItem<? extends User> item) {
        return item.getEntity().getUsername();
    }

    private Map<String, Set<String>> getTabsConfig() {
        return new LinkedHashMap() {
            {
                put(t("tab.basic_data"), new LinkedHashSet() {
                    {
                        add("username");
                        add("firstName");
                        add("lastName");
                        add("phoneNo");
                        add("isEnabled");
                        add("plainPassword");
                        add("plainPasswordRepeated");
                    }
                });
                put(t("tab.groups"), new LinkedHashSet() {
                    {
                        add("groups");
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
    public void beforeDataAddition(TabbedForm form, EntityItem<? extends User> item, JPAContainer<User> container) {
        this.handlePasswordChange(form, item, false);
    }

    @Override
    public void beforeDataUpdate(TabbedForm form, EntityItem<? extends User> item, JPAContainer<User> container) {
        this.handlePasswordChange(form, item, true);
    }

    /**
     *
     * @param user
     */
    private void handlePasswordChange(TabbedForm form, EntityItem<? extends User> item, boolean allowNull) {
        Object plainPassword = form.getField("plainPassword").getValue();
        Object plainPasswordRepeated = form.getField("plainPasswordRepeated").getValue();
        if (plainPassword != null && plainPasswordRepeated != null) {
            if (!plainPassword.equals(plainPasswordRepeated)) {
                Notification.show(t("msg.pwd_mismatch"), Notification.Type.ERROR_MESSAGE);
                throw new DataManipulationException("Mismatched passwords");
            } else if (!plainPassword.equals("")) {
                item.getItemProperty("password").setValue(passwordEncoder.encode(plainPassword.toString()));
            }
        } else {
            if (!allowNull) {
                Notification.show(t("msg.pwd_null"), Notification.Type.ERROR_MESSAGE);
                throw new DataManipulationException("Null password when creating new user");
            }
        }
    }

    @Override
    protected Class<? extends User> getEntityClass() {
        return this.securityEntities.getUserClass();
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

}
