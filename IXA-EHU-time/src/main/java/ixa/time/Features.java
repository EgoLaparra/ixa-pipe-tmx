package ixa.time;

import java.util.Iterator;
import java.util.List;

import ixa.kaflib.*;
import ixa.kaflib.KAFDocument.Layer;

public class Features {

	public String label;
	public String lemma;
	public String pos;
	public String phrase;
	public String polarity;
	public String tense;
	public String aspect;
	
	protected void setLemma (String lemma) {
		this.lemma = lemma;
	}
	
	protected void setPoS (String pos) {
		this.pos = pos;
	}
	
    private String walkConsituency (Term t, TreeNode node, String label) {
    	String phrase = null;
    	if (node.isTerminal()) {
    		Terminal term = (Terminal) node;
    		if (term.getSpan().getFirstTarget().getId().equals(t.getId())){
    			phrase = label;
    		}
    	}
    	else {
    		NonTerminal nonTerm = (NonTerminal) node;
    		List<TreeNode> children = node.getChildren();
    		Iterator childrenIterator = children.iterator();
    		while (childrenIterator.hasNext() && phrase == null) {
    			TreeNode child = (TreeNode) childrenIterator.next();
    			phrase = walkConsituency(t, child, nonTerm.getLabel());
    		}
    	}
    	
    	return phrase;
    }
    
    protected void setPhrase (Term term, List<Annotation> constituency) {
    	Iterator trees = constituency.iterator();
    	while (trees.hasNext()) {
        	Tree tree = (Tree) trees.next();
        	NonTerminal treeNonTerminal = (NonTerminal) tree.getRoot();
        	this.phrase = walkConsituency(term, tree.getRoot(), treeNonTerminal.getLabel());
    	}
	}
	
	private void setPolarity () {
		
	}
	
	private void setTense () {
		
	}
	
	private void setAspect () {
		
	}
}
