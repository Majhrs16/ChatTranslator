package me.majhrs16.cot;

import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.ExpressionParser;

public class ExpressionExecutor {
	public Object invoke(String contextName, Object contextObject, String expression) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		ExpressionParser parser           = new SpelExpressionParser();

		context.setVariable(contextName, contextObject);

		return parser.parseExpression(expression).getValue(context);
	}
}
