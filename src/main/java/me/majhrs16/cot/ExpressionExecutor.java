package me.majhrs16.cot;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ExpressionParser;

public class ExpressionExecutor {
	public Object invoke(StandardEvaluationContext context, String expression) {
		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(expression).getValue(context);
	}
}
