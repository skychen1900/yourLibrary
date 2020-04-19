package com.mycompany.jsf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in a tree of nodes. Instances of
 * this class are used as the nodes in the TreeModel.
 *
 * @author Hans Bergsten, Gefion Software <hans@gefionsoftware.com>
 * @version 1.0
 */
public class TreeNode implements Serializable {
	private TreeNode parent;
	private String name;
	private Object value;
	private boolean isExpanded;
	private boolean isLeafNode;
	private List<TreeNode> children;

	/**
	 * Returns the node name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the node name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the node value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the node value.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns "true" if the node is expanded,
	 */
	public boolean isExpanded() {
		return isExpanded;
	}

	/**
	 * Sets the "expanded" property value.
	 */
	public void setExpanded(boolean isExpanded) {
		this.isExpanded = isExpanded;
	}

	/**
	 * Returns "true" if this is a leaf node.
	 */
	public boolean isLeafNode() {
		return isLeafNode;
	}

	/**
	 * Sets the "leafNode" property value.
	 */
	public void setLeafNode(boolean isLeafNode) {
		this.isLeafNode = isLeafNode;
	}

	/**
	 * Adds the provided node as a child of this node.
	 */
	public void addChild(TreeNode child) {
		if (children == null) {
			children = new ArrayList<TreeNode>();
		}
		child.setParent(this);
		children.add(child);
	}

	/**
	 * Returns all children of this node.
	 */
	public List<TreeNode> getChildren() {
		if (children == null) {
			children = new ArrayList<TreeNode>();
		}
		return children;
	}

	/**
	 * Returns a String representing the path to this node, with
	 * the names of each parent node separated by a slash and ending
	 * with the name of the current node.
	 */
	public String getPath() {
		List<String> chain = new ArrayList<String>();
		chain.add(getName());
		TreeNode parent = getParent();
		while (parent != null) {
			chain.add(parent.getName());
			parent = parent.getParent();
		}
		StringBuffer sb = new StringBuffer();
		for (int i = chain.size() - 1; i >= 0; i--) {
			sb.append("/").append(chain.get(i));
		}
		return sb.toString();
	}

	/**
	 * Returns the parent of this node.
	 */
	private TreeNode getParent() {
		return parent;
	}

	/**
	 * Sets the parent of this node, called by the addChild() method.
	 */
	private void setParent(TreeNode parent) {
		this.parent = parent;
	}
}
