package com.mycompany.jsf.servlets;

import com.mycompany.jsf.model.TreeNode;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class is a servlet that produces a response showing the
 * state captured by the CaptureStatePhaseListener, as a tree
 * with links for opening and closing nodes.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class ShowViewStateServlet extends HttpServlet {

    /**
     * If invoked without a "viewId" parameter, renders a response
     * with links back to itself for all views available in the
     * session scope variable "com.mycompany.debug". Otherwise,
     * toggles the "expanded" flag for the node identified by the
     * "nodeId" parameter (if any) and renders the view state
     * tree by calling renderDocument().
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {

	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	HttpSession session = request.getSession();
	Map debugMap = 	(Map) session.getAttribute("com.mycompany.debug");
	if (debugMap == null) {
	    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
			       "No state available");
	    return;
	}
	
	String uri = request.getContextPath() + request.getServletPath();
	String viewId = request.getParameter("viewId");
	if (viewId == null) {
	    renderLinks(debugMap, out, uri);
	}
	else {
	    TreeNode root = (TreeNode) debugMap.get(viewId);
	    if (root == null) {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST,
				   "No state for viewId parameter");
		return;
	    }

	    String path = request.getParameter("nodePath");
	    if (path != null) {
		toggleExpanded(path, root);
	    }

	    renderDocument(root, out, viewId, uri);
	}
    }

    /**
     * Renders a response with links for all views in the Map.
     */
    private void renderLinks(Map debugMap, PrintWriter out, String uri) {
	out.println("<html>");
	out.println("  <head>");
	out.println("    <title>Debug info for views</title>");
	out.println("  </head>");
	out.println("  <body bgcolor=\"white\">");
	out.println("    Info for the following views is available:");
	out.println("    <ul>");

	Iterator i = debugMap.keySet().iterator();
	while (i.hasNext()) {
	    String viewId = (String) i.next();
	    out.println(indent(3) + "<li><a href=\"" + uri + 
			"?viewId=" + URLEncoder.encode(viewId) + 
			"\">" + viewId + "</a></li>");
	}
	
	out.println("  </body>");
	out.println("</html>");
    }

    /**
     * Locates the node with the specified path in the tree and
     * flips its "expanded" flag.
     */
    private void toggleExpanded(String path, TreeNode root) {
	List kids = root.getChildren();
	boolean foundElement = false;
	TreeNode matchingNode = null;

	StringTokenizer st = new StringTokenizer(path, "/");
	// Throw away the root token
	st.nextToken();
	while (st.hasMoreTokens()) {
	    String t = st.nextToken();
	    for (int i = 0; i < kids.size(); i++) {
		TreeNode kid = (TreeNode) kids.get(i);
		if (t.equals(kid.getName())) {
		    matchingNode = kid;
		    kids = kid.getChildren();
		    foundElement = true;
		    break;
		}
	    }
	    if (!foundElement) {
		break;
	    }
	}
	if (matchingNode != null) {
	    boolean isExpanded = matchingNode.isExpanded();
	    matchingNode.setExpanded(!isExpanded);
	}
    }

    /**
     * Renders the "html", "head" and "body" elements and calls
     * the renderTreeNode() method to render the tree.
     */
    private void renderDocument(TreeNode root, PrintWriter out,
				String viewId, String uri) {
	out.println("<html>");
	out.println("  <head>");
	out.println("    <title>Info for view '" + viewId + "'</title>");
	out.println("  </head>");
	out.println("  <body bgcolor=\"white\">");
	renderTreeNode(root, out, viewId, uri, 2, "0", 0);
	out.println("  </body>");
	out.println("</html>");
    }

    /**
     * Renders the node, as "name=value" if it's a leaf node or as a
     * link for expanding or collapsing the node otherwise. If the
     * node is expanded, calls itself recursively for all node
     * children, rendering the children within a "blockquote" element.
     */
    private void renderTreeNode(TreeNode node, PrintWriter out, 
				String viewId, String uri, int level,
				String parentNodeId, int childId) {
	if (node.isLeafNode()) {
	    out.println(indent(level) + node.getName() + " = " +
			node.getValue() + "<br>");
	}
	else {
	    String nodeId = parentNodeId + ":" + childId;
	    out.println(indent(level) + "<a id=\"" + nodeId + 
			"\" href=\"" + uri + 
			"?viewId=" + URLEncoder.encode(viewId) + 
			"&nodePath=" + 
			URLEncoder.encode(node.getPath()) +
			"&expand=" + (!node.isExpanded()) +
			"#" + nodeId +
			"\">" + (node.isExpanded() ? "-" : "+") +
			"</a> " + node.getName() + "<br>");
	    if (node.isExpanded()) {
		out.println(indent(level + 1) + "<blockquote>");
		int kidId = 0;
		Iterator i = node.getChildren().iterator();
		while (i.hasNext()) {
		    TreeNode kid = (TreeNode) i.next();
		    renderTreeNode(kid, out, viewId, uri, level + 2, nodeId,
				   kidId++);
		}
		out.println(indent(level + 1) + "</blockquote>");
	    }
	}
    }

    /**
     * Returns a blank String of the appropriate length for the indention
     * level.
     */
    private String indent(int level) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < level; i++) {
	    sb.append("  ");
	}
	return sb.toString();
    }
}
