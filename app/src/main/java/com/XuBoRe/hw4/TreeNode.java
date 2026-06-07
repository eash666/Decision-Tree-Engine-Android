package com.XuBoRe.hw4;

/**
 * The <code>TreeNode</code> class represents a single node in the decision tree.
 * It contains information: label, prompt, and message.
 * It references to its child nodes.
 *
 * @author XuBoRe
 * e-mail: R124302031@stu.ahu.edu.cn
 * Stony Brook ID: R124302031
 */
public class TreeNode {	
	private String label;
	private String prompt;
	private String message;
	private TreeNode[] children;

	/**
	 * Overloaded constructor initializes a TreeNode with specified values.
	 *
	 * @param label   the unique identifier of the node
	 * @param prompt  the option text that leads to this node
	 * @param message the question or final message displayed to the user
	 */
	public TreeNode(String label, String prompt, String message) {
		this.label = label;
		this.prompt = prompt;
		this.message = message;
		this.children = new TreeNode[9]; 
	}
	

	/**
	 * Gets the label of the node.
	 *
	 * @return the label of the node
	 */
	public String getLabel() {
		return label;
	}
	

	/**
	 * Sets the label of the node.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	

	/**
	 * Gets the prompt of the node.
	 *
	 * @return the prompt of the node
	 */
	public String getPrompt() {
		return prompt;
	}
	

	/**
	 * Sets the prompt of the node.
	 *
	 * @param prompt the prompt to set
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	

	/**
	 * Gets the message of the node.
	 *
	 * @return the message of the node
	 */
	public String getMessage() {
		return message;
	}
	

	/**
	 * Sets the message of the node.
	 *
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	

	/**
	 * Gets the array of child references.
	 *
	 * @return the array of TreeNode children
	 */
	public TreeNode[] getChildren() {
		return children;
	}
	

	/**
	 * Determines if the current node is a leaf (has no children).
	 *
	 * @return true if the node is a leaf, false otherwise
	 */
	public boolean isLeaf() {
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null) {
				return false;
			}
		}
		return true;
	}


}
