package uRen;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class main
 */
@WebServlet("/main")
public class main extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";

	RecommendationEngine engine = new RecommendationEngine();
	XMLSerializer serializerXML = new XMLSerializer();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public main() {
		super();
		Logger.Log("Recommendation engine servlet created!");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		//based on params that we get, we're gonna have to decide which algorithm to call
		//alg1	: emailAddress, Album 	: recommends based on artist
		//alg2a	: emailAddress, Album	: recommends based on genre
		//alg2b	: emailAddress			: get user's most popular genre and run alg2a (also have 1a and 1b???)
		//alg3	: emailAddress			: recommends based on the social stuff
		//alg4	: ???					: popular choices.. somehow use the "purchase" table to generate this 
		
		//emailAddress	: is needed for all of the algos.
		//Album			: we're gonna use alg1, alg2a, alg3, alg4
		//!Album		: we're gonna use alg2b, alg3, alg4
		
		
		/*
		response.setContentType("text/html");	
		PrintWriter out = response.getWriter();
		out.println("<BODY>\n" +
				"<H1 ALIGN=CENTER>" + "Params" + "</H1>\n"+
		"<table><TH>Parameter Name<TH>Parameter Value(s)");
		Enumeration paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			out.println("<TR><TD>" + paramName + "\n<TD>");
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() == 0)
					out.print("<I>No Value</I>");
				else
					out.print(paramValue);
			} else {
				out.println("<UL>");
				for(int i=0; i<paramValues.length; i++) {
					out.println("<LI>" + paramValues[i]);
				}
				out.println("</UL>");
			}
		}
		out.println("</TABLE>\n</BODY></HTML>");
		*/

		String email = "aero9@gmail.com";
		Customer user = engine.GetCustomerByEmailAddress(email);
		Purchase userPurchases = engine.GetPurchasesByCustomer(user);
		
		Recommendation results = new Recommendation(user);
		
		results.AddAlbumsPurchased(userPurchases.getPurchases());
		
		// the recommendations are of 3 types
		// 1 - global popularity - this is alg1, alg2 depends on the popularity of artists and genre 
		// 2 - personal history - this is alg2b, alg7 looks in to the users' history to see what he might like comparing it with others histories
		// 3 - social popularity - this is the local artist that are playing (alg3) and also the fb thing (alg5/6)
		
		//1
		results.AddRecommendationList(engine.GetRecommendations_alg1(user, "Metallica"), user.getGlobalPopularityWeight());
		results.AddRecommendationList(engine.GetRecommendations_alg2a(user, "Rock"), user.getGlobalPopularityWeight());
		//2
		results.AddRecommendationList(engine.GetRecommendations_alg2b(user), user.getPersonalPopularityWeight());
		results.AddRecommendationList(engine.GetRecommendations_alg7(user), user.getPersonalPopularityWeight());
		//3
		results.AddRecommendationList(engine.GetRecommendations_alg3(user), user.getSocialPopularityWeight());

		//serialize results, and we're done! :)
		out.println("Hello, World! These are the results :)<br><br>");
		out.println("<pre>");
		
		String output = serializerXML.Serialize(results, "c:\\temp\\out.xml");
		
		output = output.replace("<", "&lt");
		output = output.replace(">", "&gt");
		out.println(output);
		
		out.println("</pre>");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
