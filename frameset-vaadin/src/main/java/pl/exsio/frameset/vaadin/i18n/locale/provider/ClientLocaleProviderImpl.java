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
package pl.exsio.frameset.vaadin.i18n.locale.provider;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import java.util.Locale;
import pl.exsio.jin.locale.provider.LocaleProvider;

/**
 *
 * @author exsio
 */
public class ClientLocaleProviderImpl implements LocaleProvider {

    public static final String FORCED_LOCALE_PARAM = "l";
    
    private Locale lastKnownLocale;
    
    public ClientLocaleProviderImpl(String initialLang) {
        this.lastKnownLocale = this.getLocaleFromString(initialLang);
    }
    
    @Override
    public Locale getLocale() {
        VaadinRequest request = this.getCurrentVaadinRequest();
        if(request instanceof VaadinRequest) {
            String forcedLocale = request.getParameter(FORCED_LOCALE_PARAM);
            if(forcedLocale != null && !forcedLocale.isEmpty()) {
                this.lastKnownLocale = this.getLocaleFromString(forcedLocale);
            } else {
                this.lastKnownLocale = request.getLocale();
            }
        }
        return this.lastKnownLocale; 
    }

    private VaadinRequest getCurrentVaadinRequest() {
        return VaadinService.getCurrentRequest();
    }

    private Locale getLocaleFromString(String localeString) {
        return new Locale(localeString);
    }
    
}
