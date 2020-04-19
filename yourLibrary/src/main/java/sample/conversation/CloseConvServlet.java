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
@WebServlet(urlPatterns = { "/closeConversation" })
public class CloseConvServlet extends HttpServlet {

	// CDI Beanのインジェクション
	@Inject
	private ConvBean cbean;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		cbean.end();

		response.getWriter().close();

	}
}
