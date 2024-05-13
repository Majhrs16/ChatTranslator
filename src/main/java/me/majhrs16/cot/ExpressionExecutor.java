package me.majhrs16.cot;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ExpressionParser;

class ExpressionExecutor {
	public static Object invoke(StandardEvaluationContext context, String expression) {
		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(expression).getValue(context);
	}
}
