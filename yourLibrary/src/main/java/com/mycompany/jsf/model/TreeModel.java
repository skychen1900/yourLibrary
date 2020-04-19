package com.mycompany.jsf.model;

import java.util.StringTokenizer;

import javax.faces.component.NamingContainer;

/**
 * This class represents a tree of nodes, used as the model for
 * the UITree component.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TreeModel {
	private final static String SEPARATOR = String.valueOf(NamingContainer.SEPARATOR_CHAR);

	private TreeNode root;
	private TreeNode currentNode;

	/**
	 * Create a model with the provided TreeNode as the root.
	 */
	public TreeModel(TreeNode root) {
		this.root = root;
	}

	/**
	 * Returns the current node, or null if no node is selected.
	 */
	public TreeNode getNode() {
		return currentNode;
	}

	/**
	 * Selects the node matching the specified node ID, or deselects
	 * the current node if the node ID is null. The node ID is a
	 * colon-separated list of zero-based node indexes, e.g., the
	 * node ID "0:0:1" represents the second subnode of the first
	 * node under the root node.
	 */
	public void setNodeId(String nodeId) {
		if (nodeId == null) {
			currentNode = null;
			return;
		}

		TreeNode node = root;
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(nodeId, SEPARATOR);

		// Trow away the root index
		sb.append(st.nextToken()).append(SEPARATOR);
		while (st.hasMoreTokens()) {
			int nodeIndex = Integer.parseInt(st.nextToken());
			sb.append(nodeIndex);
			try {
				node = (TreeNode) node.getChildren().get(nodeIndex);
			} catch (IndexOutOfBoundsException e) {
				String msg = "Node node with ID " + sb.toString() +
						". Failed to parse " + nodeId;
				throw new IllegalArgumentException(msg);
			}
			sb.append(SEPARATOR);
		}
		currentNode = node;
	}
}
