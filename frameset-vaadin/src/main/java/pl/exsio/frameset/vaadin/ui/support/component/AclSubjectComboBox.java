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
package pl.exsio.frameset.vaadin.ui.support.component;

import com.vaadin.data.Item;
import org.springframework.security.acls.domain.BasePermission;
import pl.exsio.frameset.core.dao.GenericDao;
import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.security.acl.AclSubject;
import pl.exsio.frameset.vaadin.component.InitializableComboBox;

/**
 *
 * @author exsio
 */
public class AclSubjectComboBox<T extends AclSubject> extends InitializableComboBox {
    
    private transient final AclManager acl;
    
    private final boolean selectFirstItem;
    
    private transient final GenericDao<T, Long> repository;
    
    private boolean hideIfOnlyOneItemAndSelected = false;
    
    public AclSubjectComboBox(AclManager acl, GenericDao<T, Long> repository) {
        this(acl,  repository, false);
    }
    
    public AclSubjectComboBox(AclManager acl, GenericDao<T, Long> repository, boolean selectFirstItem) {
        this.acl = acl;
        this.selectFirstItem = selectFirstItem;
        this.repository = repository;
    }
    
    public void setHideIfOnlyOneItemAndSelected(boolean hide) {
        this.hideIfOnlyOneItemAndSelected = hide;
    }
    
    @Override
    protected void doInit() {
        this.setItemCaptionMode(ItemCaptionMode.EXPLICIT);
        Long firstItemId = null;
        for(AclSubject entity: this.getSubjects()) {
            if(this.acl.isGranted(entity, BasePermission.READ)) {
                if(firstItemId == null) {
                    firstItemId = entity.getAclObjectId();
                }
                Item item = this.addItem(entity.getAclObjectId());
                this.setItemCaption(entity.getAclObjectId(), entity.toString());
            }
        }
        if(this.selectFirstItem && firstItemId != null) {
            this.setNullSelectionAllowed(false);
            this.setValue(firstItemId);
        }
        if(this.getItemIds().size() == 1 && this.hideIfOnlyOneItemAndSelected && this.selectFirstItem) {
            this.setVisible(false);
        }
    }
    
    public T getSelectedSubject() {
        if(this.getValue() instanceof Long) {
            return this.repository.findOne((Long) this.getValue());
        } else {
            return null;
        }
    }
    
    public T getSubject(Object itemId) {
        if(itemId instanceof Long) {
            return this.repository.findOne((Long) itemId);
        } else {
            return null;
        }
    }
    
    protected Iterable<T> getSubjects() {
        return this.repository.findAll();
    }
}
