package eu.lod.ed.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.lod.ed.ner.StandfordEntity;
import eu.lod.ed.text.TextProcessingFacade;
import eu.lod.ed.text.impl.DefaultTextProcessingFacade;

/**
 * Servlet implementation class EdServlet
 */
@WebServlet(description = "a simple servlet", urlPatterns = { "/EdServlet" })
public class EdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TextProcessingFacade textFacade;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EdServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		InputStream is = config.getServletContext().getResourceAsStream("classifiers/ner-eng-ie.crf-4-conll.ser.gz");
		try {
			textFacade = new DefaultTextProcessingFacade(new GZIPInputStream(is));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		* Set the content type(MIME Type) of the response.
		*/
		String input = request.getParameter("input");
		System.out.println(input);
		List<StandfordEntity> entities = textFacade.getEntities(input);
		
		response.setContentType("text/html");
		 
		PrintWriter out = response.getWriter();
		/*
		* Write the HTML to the response
		*/
		
		out.println(entities);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
