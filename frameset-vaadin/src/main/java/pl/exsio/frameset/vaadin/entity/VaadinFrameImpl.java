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
package pl.exsio.frameset.vaadin.entity;

import com.vaadin.server.FontAwesome;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.nestedj.annotation.LeftColumn;
import pl.exsio.nestedj.annotation.LevelColumn;
import pl.exsio.nestedj.annotation.ParentColumn;
import pl.exsio.nestedj.annotation.RightColumn;

/**
 *
 * @author exsio
 */
@Entity
@Table(name = "vaadin_frames")
@Inheritance(strategy = InheritanceType.JOINED)
public class VaadinFrameImpl implements VaadinFrame {

    @Enumerated(EnumType.STRING)
    @Column(name = "icon", nullable = true)
    protected FontAwesome icon;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "slug", nullable = false)
    protected String slug;

    @Column(name = "module_id", nullable = true)
    protected String moduleId;

    @Column(name = "menu_label", nullable = false)
    protected String menuLabel;

    @LeftColumn
    @Column(name = "tree_left", nullable = true)
    protected Long lft;

    @RightColumn
    @Column(name = "tree_right", nullable = true)
    protected Long rgt;

    @LevelColumn
    @Column(name = "tree_level", nullable = true)
    protected Long lvl;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = VaadinFrameImpl.class)
    @JoinColumn(name = "parent_id", nullable = true)
    @ParentColumn
    protected VaadinFrameImpl parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    protected List<VaadinFrameImpl> children;
    
    @Column(name="is_default")
    protected Boolean isDefault = false;

    public VaadinFrameImpl() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getLeft() {
        return lft;
    }

    @Override
    public Long getRight() {
        return rgt;
    }

    @Override
    public Long getLevel() {
        return lvl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Frame setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getSlug() {
        return slug;
    }

    @Override
    public Frame setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    @Override
    public String getModuleId() {
        return this.moduleId;
    }

    @Override
    public Frame setModuleId(String moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    @Override
    public String getMenuLabel() {
        return menuLabel;
    }

    @Override
    public Frame setMenuLabel(String menuLabel) {
        this.menuLabel = menuLabel;
        return this;
    }

    @Override
    public Frame getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "[VaadinFrame id " + this.getId() + ": " + this.getTitle() + "; left: " + this.getLeft() + ", right: " + this.getRight() + ", level: " + this.getLevel() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VaadinFrameImpl) {
            return (this.hashCode() == o.hashCode());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().intValue();
    }

    @Override
    public boolean isRoot() {
        return this.parent == null;
    }

    @Override
    public String getKey() {
        return Long.toString(this.getId());
    }

    @Override
    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public Long getAclObjectId() {
        return this.getId();
    }
    
    @Override
    public FontAwesome getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(FontAwesome icon) {
        this.icon = icon;
    }
    
    @Override
    public boolean isChildOf(Frame frame) {
        return this.getLeft() > frame.getLeft() && this.getRight() < frame.getRight();
    }
    
}
