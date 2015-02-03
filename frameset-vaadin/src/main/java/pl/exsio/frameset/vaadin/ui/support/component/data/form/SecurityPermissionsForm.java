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
package pl.exsio.frameset.vaadin.ui.support.component.data.form;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Property;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Permission;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.security.acl.AclSubject;
import pl.exsio.frameset.security.acl.permission.map.provider.PermissionMapProvider;
import pl.exsio.frameset.security.entity.RoleImpl;
import pl.exsio.frameset.security.model.Role;
import pl.exsio.frameset.vaadin.component.InitializableFormLayout;

/**
 *
 * @author exsio
 */
public class SecurityPermissionsForm extends InitializableFormLayout {

    private final AclSubject subject;

    private transient AclManager acl;

    private transient EntityProvider entityProvider;

    private transient PermissionMapProvider permissionMapProvider;

    public void setPermissionMapProvider(PermissionMapProvider permissionMapProvider) {
        this.permissionMapProvider = permissionMapProvider;
    }

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    public SecurityPermissionsForm(AclSubject subject) {
        this.subject = subject;
    }

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    protected void doInit() {
        this.setMargin(true);
        final JPAContainer<? extends Role> roles = JPAContainerFactory.make(this.getRoleClass(), this.entityProvider.getEntityManager());
        ComboBox roleSelect = new ComboBox(t("core.security.management.roles"), roles);
        roleSelect.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        roleSelect.setItemCaptionPropertyId("label");
        this.addComponent(roleSelect);
        final FormLayout permissionsLayout = new FormLayout();
        this.addComponent(permissionsLayout);
        final Map<String, Permission> permissionsMap = this.permissionMapProvider.getPermissionMap();
        this.handleRoleSelectionChange(roleSelect, permissionsLayout, roles, permissionsMap);
    }
    
    protected Class<? extends Role> getRoleClass() {
        return RoleImpl.class;
    }

    private void handleRoleSelectionChange(ComboBox roleSelect, final FormLayout permissionsLayout, final JPAContainer<? extends Role> roles, final Map<String, Permission> permissionsMap) {
        roleSelect.addValueChangeListener(new Property.ValueChangeListener() {
            
            private Map<CheckBox, Permission> permissionCbsMap;

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if(event.getProperty().getValue() != null) {
                    this.createRolePermissionsForm(event);
                }
            }

            private void createRolePermissionsForm(Property.ValueChangeEvent event) throws NumberFormatException {
                this.permissionCbsMap = new HashMap<>();
                permissionsLayout.removeAllComponents();

                Long itemId = Long.parseLong(event.getProperty().getValue().toString());
                Role selectedRole = roles.getItem(itemId).getEntity();
                final GrantedAuthoritySid sid = new GrantedAuthoritySid(selectedRole.getName());

                for (String permissionName : permissionsMap.keySet()) {
                    CheckBox permissionCb = this.createPermissionCheckbox(permissionName, sid);
                    permissionsLayout.addComponent(permissionCb);
                }

                Button savePermissions = this.createSaveButton(sid);
                permissionsLayout.addComponent(savePermissions);
            }

            private CheckBox createPermissionCheckbox(String permissionName, final GrantedAuthoritySid sid) {
                CheckBox permissionCb = new CheckBox(permissionName, acl.isGranted(subject, permissionsMap.get(permissionName), sid));
                this.permissionCbsMap.put(permissionCb, permissionsMap.get(permissionName));
                return permissionCb;
            }

            private Button createSaveButton(final GrantedAuthoritySid sid) {
                Button savePermissions = new Button(t("core.save"), FontAwesome.FLOPPY_O);
                savePermissions.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        for (CheckBox permissionCb : permissionCbsMap.keySet()) {
                            if (permissionCb.getValue()) {
                                acl.grant(subject, permissionCbsMap.get(permissionCb), sid);
                            } else {
                                acl.revoke(subject, permissionCbsMap.get(permissionCb), sid);
                            }
                        }
                        Notification.show(t("core.security.management.permision.update.success"));
                    }
                });
                return savePermissions;
            }
        });
    }

}
