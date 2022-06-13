/*
 * Copyright 2022 Vithya Tith
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.ributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See t
 */

package com.component.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/GenericServlet"})
public class GenericServlet extends HttpServlet {

    private CallBack callBack = null;
    private boolean debug = false;

    public GenericServlet() {
        super();
    }

    public GenericServlet(CallBack cb) {
        callBack = cb;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        if (callBack != null) {
            debug = callBack.getDebug();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = request.getReader().readLine()) != null) {
                sb.append(s);
            }

            String json = sb.toString();
            String jsonOrig = sb.toString();

            if (json.equalsIgnoreCase("")) {
                json = sb.toString();
                if (debug) {
                    if (callBack != null) {
                        callBack.onVerbose("Can not decrypt. Returning");
                    }
                }

                return;
            }

            if (callBack != null) {
                callBack.onReceived(json);
            }

            writeResponse(response.getOutputStream(), "hello");

        } catch (Exception ex) {
            ex.printStackTrace();
            if (debug) {
                if (callBack != null) {
                    callBack.onError(ex.getMessage());
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(request, response);

        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");

        Enumeration<String> parameterNames = request.getParameterNames();
        
      //  Map<String, String> map = request.getP

        while (parameterNames.hasMoreElements()) {

            String paramName = parameterNames.nextElement();
           // out.write(paramName);
           // out.write("n");
            
            String value = request.getParameter(paramName);
            out.write(paramName+"<====>"+value);
            out.write("\n");
            
//
//            String[] paramValues = request.getParameterValues(paramName);
//            for (int i = 0; i < paramValues.length; i++) {
//                String paramValue = paramValues[i];
//                out.write(paramValue+"");
//                out.write("n");
//            }

        }

        out.close();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void init(ServletConfig config) {

    }

    private final void writeResponse(ServletOutputStream outSream, boolean responseBool) {
        writeResponse(outSream, Boolean.toString(responseBool));
    }

    private final void writeResponse(ServletOutputStream outSream, int responseInt) {
        writeResponse(outSream, responseInt + "");
    }

    private final void writeResponse(ServletOutputStream outSream, long responseLong) {
        writeResponse(outSream, responseLong + "");
    }

    private final void writeResponse(ServletOutputStream outSream, String responseString) {

        try {

            outSream.print(responseString);

            outSream.flush();
        } catch (Exception ex) {
            System.out.println("VT error writeRespon = " + ex.getMessage());
            Logger.getLogger(GenericServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface CallBack {

        public void onReceived(String json);

        public void onError(String error);

        public void onVerbose(String error);

        public boolean getDebug();
    }
}
