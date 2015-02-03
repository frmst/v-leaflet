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
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.provider.SecurityContextProvider;
import pl.exsio.frameset.security.entity.factory.SecurityEntityFactory;
import pl.exsio.frameset.vaadin.module.VerticalModule;
import pl.exsio.frameset.vaadin.module.management.roles.RolesDataTable;

/**
 *
 * @author exsio
 */
public class ManageRolesModule extends VerticalModule {

    private transient EntityProvider entityProvider;

    private transient DataSource dataSource;

    private transient SecurityEntityFactory securityEntities;

    protected transient CacheManager cacheManager;

    @Autowired
    private transient ApplicationEventPublisher aep;

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        this.removeAllComponents();
        RolesDataTable table = createRolesDataTable();
        table.setEntityProvider(this.entityProvider);
        table.setDataSource(this.dataSource);
        table.setApplicationEventPublisher(this.aep);
        table.setSecurityEntities(securityEntities);
        table.setCacheManager(cacheManager);
        this.addComponent(table.init());
        this.setMargin(true);
    }

    protected RolesDataTable createRolesDataTable() {
        RolesDataTable table = new RolesDataTable(SecurityContextProvider.getFor(this.frame));
        return table;
    }

    public ManageRolesModule() {
        setSizeFull();
        this.setCaption(t("core.management.roles.caption"));
    }

    public void setSecurityEntities(SecurityEntityFactory securityEntities) {
        this.securityEntities = securityEntities;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
