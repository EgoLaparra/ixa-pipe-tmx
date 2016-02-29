package ixa.time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ixa.kaflib.*;
import ixa.kaflib.Predicate.Role;

public class Features {
	
	
	protected String getLemma (Term term) {
		
		return term.getLemma();
	}

	
	protected String getLemma (Annotation annotation) {
		
		Term term = (Term) annotation;
		return term.getLemma();
	}
	
	
	protected String getPos (Term term) {

		return term.getPos();
	}
	
	
	protected String getPos (Annotation annotation) {

		Term term = (Term) annotation;
		return term.getPos();
	}

	
    protected List<NonTerminal> getConsituencyPath (Term t, TreeNode node, List<NonTerminal> prevPath) {
    	
    	List<NonTerminal> path = new ArrayList<NonTerminal>();
    	if (node.isTerminal()) {
    		Terminal term = (Terminal) node;
    		if (term.getSpan().getFirstTarget().getId().equals(t.getId())){
    			path = prevPath;
    		}
    	}
    	else {
    		NonTerminal nonTerm = (NonTerminal) node;
        	List<NonTerminal> newPath = new ArrayList<NonTerminal>();
        	newPath.addAll(prevPath);
    		newPath.add(nonTerm);
    		List<TreeNode> children = node.getChildren();
    		Iterator<TreeNode> childrenIterator = children.iterator();
    		while (childrenIterator.hasNext() && path.size() == 0) {
    			TreeNode child = childrenIterator.next();
    			path = getConsituencyPath(t, child, newPath);
    		}
    	}
    	
    	return path;
    }

    
    protected String getPhrase (List<NonTerminal> path) {
    	
    	String phrase = path.get(path.size() - 1).getLabel();	
    	return phrase;
	}
   
    
    private String getNonTerminalSpan(NonTerminal nt) {
    	
    	String span = "";
		Iterator<TreeNode> children = nt.getChildren().iterator();
		while (children.hasNext()) {
			TreeNode child = children.next();
			if (child.isTerminal()) {
				Terminal childt = (Terminal) child;
				span = childt.getSpan().getTargets().get(0).getLemma();
			} else {
				NonTerminal childnt = (NonTerminal) child;
				if (span.equals("")) {
					span = getNonTerminalSpan(childnt);
				} else {
					span = span + " " + getNonTerminalSpan(childnt);
				}
			}
		}
    	return span;
    }
    
    
    protected String getHeadingPreposition (List<NonTerminal> path) {
    	
    	String headingPrep = "";
    	Iterator<NonTerminal> ntIter = path.iterator();
    	while (ntIter.hasNext()) {
    		NonTerminal nt = ntIter.next(); 
    		Iterator<TreeNode> children = nt.getChildren().iterator();
    		while (children.hasNext()) {
    			TreeNode child = children.next();
    			if (!child.isTerminal()) {
    				NonTerminal childnt = (NonTerminal) child;
    				if (childnt.getHead() && (childnt.getLabel().equals("IN") || childnt.getLabel().equals("PREP"))){
    					headingPrep = getNonTerminalSpan(childnt);
    				}
    			}
    		}
    	}
    	return headingPrep;
	}

    
    protected String getSyntacticRelation(List<NonTerminal> eventPath, List<NonTerminal> timexPath) {
    	
    	String synRelation = "samesentence"; 	
    	List<NonTerminal> retainedList = new ArrayList<NonTerminal>();
    	retainedList.addAll(eventPath);
    	NonTerminal sentence = retainedList.get(1); // Second item of the list is S. The first one is TOP
    	retainedList.retainAll(timexPath);
     	NonTerminal commonPhrase = retainedList.get(retainedList.size() - 1);
     	if (!commonPhrase.getLabel().equals("S"))
     		synRelation = "samephrase";
     	else if (!commonPhrase.equals(sentence))
     		synRelation = "samesubsentence";
    	return synRelation;
    	
    }
    
    
    protected String[] getSRL (Term term, List<Predicate> predicates) {
    	
    	String srl[] = new String[3];
    	srl[0] = "";
    	srl[1] = "";
    	srl[2] = "";
    	
    	Iterator<Predicate> srlPredicates = predicates.iterator();
    	while (srlPredicates.hasNext()) {
        	String roleConfiguration = "";
    		Predicate srlPredicate = (Predicate) srlPredicates.next();
       			
        	List<Role> srlRoles = srlPredicate.getRoles();
        	Iterator<Role> iteratorRoles = srlRoles.iterator();
        	while (iteratorRoles.hasNext()) {
        		Role srlRole = iteratorRoles.next();
        		String semRole = srlRole.getSemRole();
        		if (roleConfiguration.equals(""))
        			roleConfiguration = semRole;
        		else
        			roleConfiguration = roleConfiguration + "+" + semRole;
        		List<Term> roleTerms = srlRole.getTerms();
        		Iterator<Term> iteratorTerm = roleTerms.iterator();
        		while (iteratorTerm.hasNext()) {
        			if (term.equals(iteratorTerm.next()))
       					srl[0] = semRole;
        				srl[1] = srlPredicate.getExternalRefs().get(0).getReference();
        		}

	        	if (term.equals(srlPredicate.getTerms().get(0))) {
    				srl[1] = srlPredicate.getExternalRefs().get(0).getReference();
	        		srl[2] = roleConfiguration;
	        	}
    		}
    	}
    	
    	return srl;
	}
	
//	private String getPolarity () {
//		
//	}
//	
//	private String getTense () {
//		
//	}
//	
//	private String getAspect () {
//		
//	}
    
}
