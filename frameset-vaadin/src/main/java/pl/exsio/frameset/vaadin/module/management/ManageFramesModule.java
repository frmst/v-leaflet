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
package pl.exsio.frameset.vaadin.module.management;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.navigator.ViewChangeListener;
import pl.exsio.frameset.core.dao.FrameDao;
import pl.exsio.frameset.core.entity.factory.CoreEntityFactory;
import pl.exsio.frameset.core.repository.provider.CoreRepositoryProvider;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.security.acl.permission.map.provider.PermissionMapProvider;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.security.context.provider.SecurityContextProvider;
import pl.exsio.frameset.vaadin.entity.VaadinFrameImpl;
import pl.exsio.frameset.vaadin.module.HorizontalModule;
import pl.exsio.frameset.vaadin.module.management.frames.FrameDetails;
import pl.exsio.frameset.vaadin.module.management.frames.FramesTree;

/**
 *
 * @author exsio
 */
public class ManageFramesModule extends HorizontalModule {

    private transient EntityProvider frameEntityProvider;

    private transient EntityProvider roleEntityProvider;

    private transient FrameDao<VaadinFrameImpl> frameDao;

    private transient AclManager acl;

    private transient PermissionMapProvider permissionMapProvider;

    private transient CoreEntityFactory coreEntities;

    private transient CoreRepositoryProvider coreRepositories;

    public void setPermissionMapProvider(PermissionMapProvider permissionMapProvider) {
        this.permissionMapProvider = permissionMapProvider;
    }

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    public void setFrameDao(FrameDao<VaadinFrameImpl> frameDao) {
        this.frameDao = frameDao;
    }

    public void setFrameEntityProvider(EntityProvider entityProvider) {
        this.frameEntityProvider = entityProvider;
    }

    public void setRoleEntityProvider(EntityProvider roleEntityProvider) {
        this.roleEntityProvider = roleEntityProvider;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        this.removeAllComponents();
        SecurityContext security = SecurityContextProvider.getFor(this.frame);
        FramesTree tree = this.getFramesTree(security);
        this.addComponent(tree.init());
        FrameDetails details = this.getFrameDetails(security);
        tree.addTreeItemClickListener(details);
        this.addComponent(details);
        this.setExpandRatio(tree, 2);
        this.setExpandRatio(details, 10);
        this.setMargin(true);
        this.setSpacing(true);
    }

    private FrameDetails getFrameDetails(SecurityContext security) {
        FrameDetails details = createFrameDetails(security);
        details.setEntityProvider(this.roleEntityProvider);
        details.setAcl(this.acl);
        details.setPermissionMapProvider(this.permissionMapProvider);
        return details;
    }

    protected FrameDetails createFrameDetails(SecurityContext security) {
        FrameDetails details = new FrameDetails(security);
        return details;
    }

    private FramesTree getFramesTree(SecurityContext security) {
        FramesTree tree = createFramesTree(security);
        tree.setEntityProvider(this.frameEntityProvider);
        tree.setCoreEntities(this.coreEntities);
        tree.setCoreRepositories(this.coreRepositories);
        return tree;
    }

    protected FramesTree createFramesTree(SecurityContext security) {
        FramesTree tree = new FramesTree(security);
        return tree;
    }

    public ManageFramesModule() {
        setSizeFull();
        this.setCaption(t("core.management.frames.caption"));
    }

    public void setCoreEntities(CoreEntityFactory coreEntities) {
        this.coreEntities = coreEntities;
    }

    public void setCoreRepositories(CoreRepositoryProvider coreRepositories) {
        this.coreRepositories = coreRepositories;
    }

}
