/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.semanticwb.endpoint;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.fuseki.servlets.HttpAction;
import org.apache.jena.fuseki.servlets.ResponseResultSet;
import org.semanticwb.store.jenaimp.SWBTSGraph;
import org.semanticwb.store.leveldb.GraphImp;
import com.hp.hpl.jena.sparql.core.Prologue;
import org.apache.jena.fuseki.servlets.ResponseModel;
import org.semanticwb.store.jenaimp.SWBTSGraphCache;

/**
 *
 * @author javiersolis
 */
public class sparql extends HttpServlet 
{
    Model model=null;
    Prologue prologue=null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); //To change body of generated methods, choose Tools | Templates.
        System.out.println("sparql init");
        
        try
        {
            HashMap<String,String> params=new HashMap();
            params.put("path", "/data/leveldb");
            //model=new ModelCom(new SWBTSGraphCache(new SWBTSGraph(new GraphImp("bsbm",params)),1000));
            model=new ModelCom(new SWBTSGraph(new GraphImp("bsbm",params)));
            //model=new ModelCom(new SWBTSGraph(new GraphImp("bsbm",params)));
            prologue=new Prologue(model.getGraph().getPrefixMapping());
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy(); //To change body of generated methods, choose Tools | Templates.
        model.close();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        String query=request.getParameter("query");
        if(query!=null)
        {
            //System.out.println("query:"+query);
            long time=System.currentTimeMillis();
            Query q = QueryFactory.create(query, Syntax.syntaxSPARQL_11);
            QueryExecution qe = QueryExecutionFactory.create(q, model);
            if(q.isSelectType())
            {
                ResultSet rs=qe.execSelect();
                //System.out.println("Time rs:"+(System.currentTimeMillis()-time));
                ResponseResultSet.doResponseResultSet(new HttpAction(0,request,response,false), rs, prologue);
            }else if(q.isDescribeType())
            {
                Model model=qe.execDescribe();
                //System.out.println("Time model:"+(System.currentTimeMillis()-time));
                ResponseModel.doResponseModel(new HttpAction(0,request,response,false), model);
            }else if(q.isConstructType())
            {
                Model model=qe.execConstruct();
                //System.out.println("Time model:"+(System.currentTimeMillis()-time));
                ResponseModel.doResponseModel(new HttpAction(0,request,response,false), model);
            }else if(q.isAskType())
            {
                boolean ret=qe.execAsk();
                ResponseResultSet.doResponseResultSet(new HttpAction(0,request,response,false), ret);
            }
            System.out.println("Time end:"+(System.currentTimeMillis()-time));
        }else
        {
            query="";
            PrintWriter out=response.getWriter();
            out.write("<!DOCTYPE html>\n");
            out.write("<html>\n");
            out.write("    <head>\n");
            out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
            out.write("        <title>JSP Page</title>\n");
            out.write("    </head>\n");
            out.write("    <body>\n");
            out.write("        <h1>SWB SparQL Query Test!</h1>");
            out.write("        <form>\n");
            out.write("            Query:<br>\n");
            out.write("            <textarea rows=\"20\" cols=\"80\" name=\"query\">"+query+"</textarea><br>\n");
            out.write("            <input type=\"submit\">\n");
            out.write("        </form>");
            out.write("        </table>\n");
            out.write("    </body>\n");
            out.write("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
