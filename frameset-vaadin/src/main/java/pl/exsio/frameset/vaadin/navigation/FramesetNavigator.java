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
package pl.exsio.frameset.vaadin.navigation;

import pl.exsio.frameset.vaadin.navigation.target.NavigationTarget;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.UI;
import java.io.Serializable;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.vaadin.ex.InvalidNavigationTargetException;

/**
 *
 * @author exsio
 */
public interface FramesetNavigator extends Serializable {

    String NAVIGATOR_BEAN_NAME = "navigator";

    boolean navigateTo(Frame frame);

    boolean navigateTo(Frame frame, boolean force);

    boolean navigateHome();

    void init(UI ui, String pathSegmentDelimiter, NavigationTarget navigationTarget) throws InvalidNavigationTargetException;

    void refresh() throws InvalidNavigationTargetException;

    String getPath(Frame frame);

    String getCurrentState();

    void addViewChangeListener(ViewChangeListener listener);

    void removeViewChangeListener(ViewChangeListener listener);

    void addFrameChangeListener(FrameChangeListener listener);

    void removeFrameChangeListener(FrameChangeListener listener);
}
