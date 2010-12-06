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
		
		//Perf Timers
		PerfTimer totalTurnaroundTime = new PerfTimer();
		//combined Perf Timers
		PerfTimer globalPopPerfTimer = new PerfTimer();
		PerfTimer socialPopPerfTimer = new PerfTimer();
		PerfTimer personalPopPerfTimer = new PerfTimer();
		//Individual Perf Timers
		PerfTimer alg1PerfTimer = new PerfTimer();
		PerfTimer alg2aPerfTimer = new PerfTimer();
		PerfTimer alg2bPerfTimer = new PerfTimer();
		PerfTimer alg7PerfTimer = new PerfTimer();
		PerfTimer alg3PerfTimer = new PerfTimer();
		
		// the recommendations are of 3 types
		// 1 - global popularity - this is alg1, alg2 depends on the popularity of artists and genre 
		// 2 - personal history - this is alg2b, alg7 looks in to the users' history to see what he might like comparing it with others histories
		// 3 - social popularity - this is the local artist that are playing (alg3) and also the fb thing (alg5/6)
		
		//start thetotal turnaround timer
		totalTurnaroundTime.Start();
		/*
		 * 1 - Global Popularity
		 */
		globalPopPerfTimer.Start();
		
		alg1PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg1(user, "Metallica"), user.getGlobalPopularityWeight());
		alg1PerfTimer.Stop();
		
		alg2aPerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg2a(user, "Rock"), user.getGlobalPopularityWeight());
		alg2aPerfTimer.Stop();
		
		globalPopPerfTimer.Stop();
		
		/*
		 * 2 - Personal Popularity
		 */
		personalPopPerfTimer.Start();
		
		alg2bPerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg2b(user), user.getPersonalPopularityWeight());
		alg2bPerfTimer.Stop();
		
		alg7PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg7(user), user.getPersonalPopularityWeight());
		alg7PerfTimer.Stop();
		
		personalPopPerfTimer.Stop();
		
		/*
		 * 3 - Social Popularity
		 */
		socialPopPerfTimer.Start();
		
		alg3PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg3(user), user.getSocialPopularityWeight());
		alg3PerfTimer.Stop();
		
		socialPopPerfTimer.Stop();
		
		//stop the total turnaround timer
		totalTurnaroundTime.Stop();
		
		//serialize results, and we're done! :)
		out.println("Hello, World! These are the results :)<br><br>");
		if (SessionSettings.DumpDebugData) {
			out.println( "totalTurnaroundTime = " + totalTurnaroundTime.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "globalPopPerfTimer = " + globalPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "socialPopPerfTimer = " + socialPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "personalPopPerfTimer = " + personalPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg1PerfTimer = " + alg1PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg2aPerfTimer = " + alg2aPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg2bPerfTimer = " + alg2bPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg7PerfTimer = " + alg7PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg3PerfTimer = " + alg3PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
		}
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
