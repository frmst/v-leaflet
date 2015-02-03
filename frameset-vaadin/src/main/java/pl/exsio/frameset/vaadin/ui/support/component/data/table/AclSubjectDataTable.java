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

import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.security.acl.AclSubject;
import pl.exsio.frameset.security.acl.permission.map.provider.PermissionMapProvider;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.ui.support.component.data.form.SecurityPermissionsForm;

/**
 *
 * @author exsio
 */
public abstract class AclSubjectDataTable<T, F> extends JPADataTable<T, F> {

    protected transient AclManager acl;

    protected transient PermissionMapProvider permissionMapProvider;

    public AclSubjectDataTable(Class<F> formClass, TableDataConfig config, SecurityContext security) {
        super(formClass, config, security);
    }

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    public void setPermissionMapProvider(PermissionMapProvider permissionMapProvider) {
        this.permissionMapProvider = permissionMapProvider;
    }

    protected SecurityPermissionsForm getSecurityForm(AclSubject aclSubject) {
        SecurityPermissionsForm form = new SecurityPermissionsForm(aclSubject);
        form.setEntityProvider(this.entityProvider);
        form.setAcl(this.acl);
        form.setPermissionMapProvider(this.permissionMapProvider);
        return form;
    }

}
