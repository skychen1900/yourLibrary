package sample.conversation;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CDIサンプル
 */
@WebServlet(urlPatterns = { "/conversationCountup" })
public class CountUpServlet extends HttpServlet {

	// CDI Beanのインジェクション
	@Inject
	private ConvBean cbean;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		cbean.countUp();

		request.getRequestDispatcher("testConv.jsp").forward(request, response);
	}
}
