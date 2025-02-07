/*******************************************************************************
 * Copyright (c) 2009 Alexander Koderman <ak[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Alexander Koderman <ak[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.gs.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import sernet.hui.common.connect.HUITypeFactory;
import sernet.hui.common.connect.SNCAMessages;

/**
 * Servlet implementation class GetHitroConfig
 */
public class GetHitroConfig extends HttpServlet {

    private static final Logger log = Logger.getLogger(GetHitroConfig.class);

    private static final long serialVersionUID = 1L;

    /*
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String basePath = getInitParameter("snca.xml.path");
            if (basePath == null) {
                String message = "init parameter snca.xml.path is not set in web.xml";
                sendErrorMessage(response, message);
                throw new RuntimeException(message);
            } else {
                String fileName = HUITypeFactory.HUI_CONFIGURATION_FILE;
                String resourceParameter = request.getParameter("resource");
                if (resourceParameter != null) {
                    // return a resource bundle
                    // security check
                    if (resourceParameter.indexOf("..") != -1
                            || resourceParameter.indexOf(':') != -1
                            || !resourceParameter.startsWith(SNCAMessages.BUNDLE_NAME)
                            || !resourceParameter.endsWith(SNCAMessages.BUNDLE_EXTENSION)) {
                        String message = "illegal parameter: " + resourceParameter;
                        sendErrorMessage(response, message);
                        throw new RuntimeException(message);
                    }
                    fileName = resourceParameter;
                }
                String path = basePath + "/" + fileName;
                if (log.isDebugEnabled()) {
                    log.debug("returning: " + path);
                }

                try (InputStream in = getServletContext().getResourceAsStream(path)) {
                    if (in == null) {
                        String message = "Resource not found: " + path;
                        sendErrorMessage(response, message);
                        // check if an language only file is searched (i.e.
                        // snca-messages_de.properties)
                        // or an language-region file (i.e.
                        // snca-messages_de_DE.properties)
                        if (fileName
                                .matches("snca\\-messages_[A-Za-z]{2}_[A-Za-z]{2}\\.properties")) {
                            if (log.isInfoEnabled()) {
                                log.info(message);
                            }
                        } else {
                            throw new RuntimeException(message);
                        }
                    } else {
                        try (OutputStream out = response.getOutputStream()) {
                            IOUtils.copy(in, out);
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while getting hitro config or resource bundle.", e);
        }
    }

    protected void sendErrorMessage(HttpServletResponse response, String message)
            throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.append(message);
        }
    }
}