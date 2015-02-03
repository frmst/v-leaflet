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
package pl.exsio.frameset.vaadin.module.management.frames;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.security.acl.permission.map.provider.PermissionMapProvider;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.ui.support.component.data.form.SecurityPermissionsForm;

/**
 *
 * @author exsio
 */
public class FrameDetails extends VerticalLayout implements ItemClickEvent.ItemClickListener {

    private transient AclManager acl;

    private transient EntityProvider entityProvider;

    private transient PermissionMapProvider permissionMapProvider;

    private transient final SecurityContext security;

    public FrameDetails(SecurityContext security) {
        this.security = security;
        this.setSizeFull();
        this.setStyleName("frames-management-details");
    }

    public void setPermissionMapProvider(PermissionMapProvider permissionMapProvider) {
        this.permissionMapProvider = permissionMapProvider;
    }

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        EntityItem<VaadinFrame> item = (EntityItem<VaadinFrame>) event.getItem();
        VaadinFrame frame = item.getEntity();
        if (!frame.isRoot()) {
            removeAllComponents();
            addComponent(getFrameForm(item));
        }
    }

    private VerticalLayout getFrameForm(EntityItem<VaadinFrame> item) {

        VerticalLayout formWrapper = new VerticalLayout();
        formWrapper.addComponent(new Label(t("core.management.frames.edition.title") + ": " + t(item.getEntity().getTitle())));
        TabSheet tabs = new TabSheet();

        final BasicFrameDataForm basicData = new BasicFrameDataForm(item);
        final SecurityPermissionsForm permissions = this.createSecurityPermissionsForm(item);
        if (this.security.canWrite()) {
            tabs.addTab(new VerticalLayout() {
                {
                    addComponent(basicData.init());
                    setMargin(true);
                }
            }, t("core.management.frames.tab.basic_data"));
        }
        if (this.security.canAdminister()) {
            tabs.addTab(new VerticalLayout() {
                {
                    addComponent(permissions.init());
                    setMargin(true);
                }
            }, t("core.management.frames.tab.security"));
        }
        formWrapper.addComponent(tabs);
        formWrapper.setSpacing(true);
        return formWrapper;

    }

    private SecurityPermissionsForm createSecurityPermissionsForm(EntityItem<VaadinFrame> item) {
        SecurityPermissionsForm permissions = new SecurityPermissionsForm(item.getEntity());
        permissions.setAcl(this.acl);
        permissions.setEntityProvider(this.entityProvider);
        permissions.setPermissionMapProvider(this.permissionMapProvider);
        return permissions;
    }
}
