package com.smartform.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tree {
	private TreeElement node;
	private LinkedHashMap<String, Tree> children;
	
	public Tree(TreeElement node) {
		super();
		this.node = node;
		this.children = new LinkedHashMap<String, Tree>();
	}
	
	public Tree(TreeElement node, Tree child) {
		super();
		this.node = node;
		this.children = new LinkedHashMap<String, Tree>();
		if (child != null && child.getNode() != null) {
			this.children.put(child.getNode().getName(), child);
		}
	}
	public void merge(Tree other) {
		if (this.children == null) {
			this.children = other.children;
		} else if (other.getChildren() != null) {
			for(Map.Entry<String, Tree> otherChild : other.getChildren().entrySet()) {
				this.addChild(otherChild.getValue());
			}
		}
	}
	public void addChild(Tree child) {
		if (children == null) {
			children = new LinkedHashMap<String, Tree>();
		}
		if (child != null) {
			Tree storedChild = children.get(child.getNode().getName());
			if (storedChild == null) {
				children.put(child.getNode().getName(), child);
			} else {
				storedChild.merge(child);
			}
		}
	}
	public TreeElement getNode() {
		return node;
	}
	public void setNode(TreeElement node) {
		this.node = node;
	}
	public LinkedHashMap<String, Tree> getChildren() {
		return children;
	}
	public void setChildren(LinkedHashMap<String, Tree> children) {
		this.children = children;
	}
	
}
