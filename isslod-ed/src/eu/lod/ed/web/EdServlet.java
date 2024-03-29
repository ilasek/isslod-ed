package eu.lod.ed.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.gson.Gson;

import eu.lod.ed.dbpedia.DbpEndpoint;
import eu.lod.ed.dbpedia.DbpEntity;
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
	private DbpEndpoint dbpEndpoint;
	private Gson gson;

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
		gson = new Gson();
		InputStream is = config.getServletContext().getResourceAsStream(
				"classifiers/ner-eng-ie.crf-4-conll.ser.gz");
		try {
			textFacade = new DefaultTextProcessingFacade(
					new GZIPInputStream(is));
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dbpEndpoint = new DbpEndpoint("http://live.dbpedia.org/sparql");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		 * Set the content type(MIME Type) of the response.
		 */
		String input = request.getParameter("input");
		System.out.println(input);

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		HashMap<String, List<RankedEntity>> rankedListMap = new HashMap<String, List<RankedEntity>>();
		HashMap<String, StandfordEntity> entitiesMap = new HashMap<String, StandfordEntity>();
		List<StandfordEntity> entities = textFacade.getEntities(input);

		for (StandfordEntity curEntity : entities) {
			List<DbpEntity> dbpEntities = dbpEndpoint
					.getDbpEntitiesByLabel(curEntity.getName());
			List<RankedEntity> rankedList = rank(dbpEntities, input);
			
				rankedListMap.put(curEntity.getName(),rankedList);
				entitiesMap.put(curEntity.getName(), curEntity);
		}

		out.println("{rankedList:" +  gson.toJson(rankedListMap)+ ",entities:" + gson.toJson(entitiesMap) + "}");

		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private List<RankedEntity> rank(List<DbpEntity> src, String input) {
		List<RankedEntity> result = new ArrayList<RankedEntity>(src.size());
		for (DbpEntity curEntity : src) {
			String eAbstract = curEntity.getDescription();
			List<String> inputStems = textFacade.listStems(input);
			List<String> abstractStems = textFacade.listStems(eAbstract);
			double sim = textFacade.calcSimilarity(inputStems, abstractStems);
			RankedEntity eRanked = new RankedEntity(curEntity, sim);
			result.add(eRanked);
		}
		Collections.sort(result, Collections.reverseOrder());
		return result;
	}
}

class RankedEntity implements Comparable<RankedEntity> {
	private DbpEntity entity;
	private double rank;

	public RankedEntity(DbpEntity entity, double rank) {
		super();
		this.entity = entity;
		this.rank = rank;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RankedEntity)) {
			return false;
		}
		RankedEntity that = (RankedEntity) obj;
		return this.rank == that.rank;
	}

	@Override
	public int compareTo(RankedEntity o) {
		return ((Double) this.rank).compareTo(o.rank);
	}

	@Override
	public int hashCode() {
		return (int) rank;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("entityUri", entity.getUri()).append("rank", rank)
				.toString();
	}

	public DbpEntity getEntity() {
		return entity;
	}

	public double getRank() {
		return rank;
	}
}