package uRen;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

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
		 * im looking out for 4 modes of working here...
		 * 1. recommend based on username - 2b, 4, 3, 5 are used in this case ('BASIC_MODE')
		 * 2a. recommend based on username, artist - add 1 to the previous list ('ARTIST_MODE')
		 * 2b. recommend based on username, genre - add 2a to the first list ('GENRE_MODE')
		 * 3. adding mode, username, albumid ('ADDIN_MODE')
		 */
		boolean userNamePresent = false;
		String userName = "null";
		boolean artistPresent = false;
		String artist = "null";
		boolean genrePresent = false;
		String genre = "null";
		boolean albumIDPresent = false;
		int albumID = 0;
		
		Enumeration paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			
			if (paramValues.length <= 0) continue; //skip it as it has no value
			
			if (paramName.compareToIgnoreCase(SessionSettings.ParamUserame) == 0 ) {
				userNamePresent = true;
				userName = paramValues[0];
			} else if (paramName.compareToIgnoreCase(SessionSettings.ParamArtist) == 0 ) {
				artistPresent = true;
				artist = paramValues[0];
			} else if (paramName.compareToIgnoreCase(SessionSettings.ParamGenre) == 0 ) {
				genrePresent = true;
				genre = paramValues[0];
			} else if (paramName.compareToIgnoreCase(SessionSettings.ParamAlbumID) == 0 ) {
				albumIDPresent = true;
				try {
					albumID = Integer.parseInt(paramValues[0].trim());
				} catch (Exception ex) {
					albumIDPresent = false;
				}
			} else {
				//ignore it
			}
		}
		
		//figure out the mode
		if (!userNamePresent) return; //fatal error!
		String email = userName; //"aero9@gmail.com";
		Customer user = engine.GetCustomerByEmailAddress(email);
		if (user == null) Logger.Log("IS mySQL RUNNING??");
		
		//if add in mode, get done, QUICK!
		if (albumIDPresent) {
			Album album = engine.GetAlbumByID(albumID);
			if (album == null) {
				Logger.Log("invalid album");
				return;
			}
			engine.InsertIntoPurchaseTable(user, album);
			return;
		}

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
		PerfTimer alg4PerfTimer = new PerfTimer();
		PerfTimer alg3PerfTimer = new PerfTimer();
		PerfTimer alg5PerfTimer = new PerfTimer();
		
		// the recommendations are of 3 types
		// 1 - global popularity - this is alg1, alg2 depends on the popularity of artists and genre 
		// 2 - personal history - this is alg2b, alg7 looks in to the users' history to see what he might like comparing it with others histories
		// 3 - social popularity - this is the local artist that are playing (alg3) and also the fb thing (alg5/6)
		
		/*
		 * REVISED:
		 * 1  - Based on current selection, using artist
		 * 2a - Based on current selection, using genre
		 * 2b - Based on user history, find popular genre
		 * 3  - Based on gigjunkie concerts in town
		 * 4  - Using similarity index between this user and remaining users in the system
		 * 5  - Based on Facebook friends
		 * 6* - <snipped>
		 */
		
		
		//start the total turnaround timer
		totalTurnaroundTime.Start();
		
		/*
		 * 1 - Global Popularity
		 */
		globalPopPerfTimer.Start();
		
		if (artistPresent) {
			alg1PerfTimer.Start();
			results.AddRecommendationList(engine.GetRecommendations_alg1(user, artist), user.getGlobalPopularityWeight(), "alg1");
			alg1PerfTimer.Stop();
		}		
		if (genrePresent) {
			alg2aPerfTimer.Start();
			results.AddRecommendationList(engine.GetRecommendations_alg2a(user, genre), user.getGlobalPopularityWeight(), "alg2a");
			alg2aPerfTimer.Stop();
		}
		
		globalPopPerfTimer.Stop();
		
		/*
		 * 2 - Personal Popularity
		 */
		personalPopPerfTimer.Start();
		
		alg2bPerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg2b(user), user.getPersonalPopularityWeight(), "alg2b");
		alg2bPerfTimer.Stop();
		
		alg4PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg4(user), user.getPersonalPopularityWeight(), "alg4");
		alg4PerfTimer.Stop();
		
		personalPopPerfTimer.Stop();
		
		/*
		 * 3 - Social Popularity
		 */
		socialPopPerfTimer.Start();
		
		alg3PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg3(user), user.getSocialPopularityWeight(), "alg3");
		alg3PerfTimer.Stop();
		
		alg5PerfTimer.Start();
		results.AddRecommendationList(engine.GetRecommendations_alg5(user), user.getSocialPopularityWeight(), "alg5");
		alg5PerfTimer.Stop();
		
		socialPopPerfTimer.Stop();
		
		//stop the total turnaround timer
		totalTurnaroundTime.Stop();
		
		//serialize results, and we're done! :)
		//out.println("Hello, World! These are the results :)<br><br>");
		if (SessionSettings.DumpDebugData) {
			out.println( "totalTurnaroundTime = " + totalTurnaroundTime.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "globalPopPerfTimer = " + globalPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "socialPopPerfTimer = " + socialPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "personalPopPerfTimer = " + personalPopPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg1PerfTimer = " + alg1PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg2aPerfTimer = " + alg2aPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg2bPerfTimer = " + alg2bPerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg4PerfTimer = " + alg4PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg5PerfTimer = " + alg5PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
			out.println( "alg3PerfTimer = " + alg3PerfTimer.GetExecTimeInMilliSeconds() + "<br>" );
		}
		//out.println("<pre>");
		
		String output = serializerXML.Serialize(results, "C:\\apache-tomcat-7.0.0\\webapps\\RecoEngine\\out.xml");
		
//		output = output.replace("<", "&lt");
//		output = output.replace(">", "&gt");
		//out.println(output);
//		
//		out.println("</pre>");
//		
//		out.println("<title> hello world! </title>"); 

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
