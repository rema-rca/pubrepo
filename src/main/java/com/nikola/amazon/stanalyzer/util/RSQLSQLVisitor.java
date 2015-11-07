package com.nikola.amazon.stanalyzer.util;

import java.util.StringJoiner;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.NoArgRSQLVisitorAdapter;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

public class RSQLSQLVisitor extends NoArgRSQLVisitorAdapter<String> {
	
	private final String collectionName;
	private int visitCount = 0;
	
	public RSQLSQLVisitor(String collectionName) {
		this.collectionName = collectionName;
	}
	

	@Override
	public String visit(AndNode arg0) {

		StringJoiner joiner = new StringJoiner(" AND ");
		for(Node node : arg0.getChildren()) {
			if(node instanceof OrNode) {
				joiner.add(visit((OrNode)node));
			}
			else if(node instanceof AndNode) {
				joiner.add(visit((AndNode)node));
			}
			else if(node instanceof ComparisonNode) {
				joiner.add(visit((ComparisonNode)node));
			}
		}
		String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
		visitCount++;
		return collectionPrefix + "(" + joiner.toString() + ")";
	}

	@Override
	public String visit(OrNode arg0) {
		StringJoiner joiner = new StringJoiner(" OR ");
		for(Node node : arg0.getChildren()) {
			if(node instanceof OrNode) {
				joiner.add(visit((OrNode)node));
			}
			else if(node instanceof AndNode) {
				joiner.add(visit((AndNode)node));
			}
			else if(node instanceof ComparisonNode) {
				joiner.add(visit((ComparisonNode)node));
			}
		}
		String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
		visitCount++;
		return collectionPrefix + "(" + joiner.toString() + ")";
	}

	@Override
	public String visit(ComparisonNode arg0) {
		ComparisonOperator operator = arg0.getOperator();
		String returnString = "";
		if(operator.getSymbol().equals("=in=")) {
			StringJoiner joiner = new StringJoiner(",");
			for(String argument : arg0.getArguments()) {
				joiner.add(argument);
			}
			String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
			returnString = collectionPrefix + arg0.getSelector() + " IN (" + joiner.toString() + ")";
		}
		if(operator.getSymbol().equals("=out=")) {
			StringJoiner joiner = new StringJoiner(",");
			for(String argument : arg0.getArguments()) {
				joiner.add(argument);
			}
			String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
			returnString = collectionPrefix + arg0.getSelector() + " NOT IN (" + joiner.toString() + ")";
		}
		else if(operator.getSymbol().equals("==")) {
			String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
			returnString = collectionPrefix + arg0.getSelector() + " = " + arg0.getArguments().get(0);
		}
		else if(operator.getSymbol().equals("=ge=")) {
			String collectionPrefix = visitCount == 0 ? ("SELECT * FROM " + collectionName + " WHERE ") : "";
			returnString = collectionPrefix + arg0.getSelector() + " >= " + arg0.getArguments().get(0);
		}
		
		visitCount++;
		return returnString;
	}
	
}