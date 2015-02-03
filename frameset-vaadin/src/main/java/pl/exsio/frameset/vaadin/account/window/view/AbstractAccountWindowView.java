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
package pl.exsio.frameset.vaadin.account.window.view;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Form;
import pl.exsio.frameset.security.model.User;
import pl.exsio.frameset.vaadin.account.window.presenter.AccountWindowPresenter;
import pl.exsio.frameset.vaadin.component.InitializableWindow;
import pl.exsio.frameset.vaadin.ui.view.FramesetView;

/**
 *
 * @author exsio
 */
public abstract class AbstractAccountWindowView extends InitializableWindow implements FramesetView<AccountWindowPresenter> {

    public final static String FIELD_USERNAME = "username";

    public final static String FIELD_FIRST_NAME = "firstName";

    public final static String FIELD_lAST_NAME = "lastName";

    public final static String FIELD_PHONE_NO = "phoneNo";

    public final static String FIELD_PASSWORD = "plainPassword";

    public final static String FIELD_PASSWORD_REPEATED = "plainPasswordRepeated";

    public AbstractAccountWindowView(String caption) {
        super(caption);
    }

    public abstract Form getForm();

    public abstract EntityItem<? extends User> getItem();
}
